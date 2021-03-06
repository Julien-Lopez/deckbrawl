package game

import deckbrawl.DeckBrawlException
import game.card._
import interface.DeckBrawlInterface
import player._

import scala.annotation.tailrec
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class Game(protected val interface: DeckBrawlInterface) {
  protected val actionHistory: ListBuffer[(Player, Action)] = ListBuffer()

  // Constants
  val startHand: Int = 5
  val startLife: Int = 30
  val nbMonsterZones: Int = 5
  val nbMpZones: Int = 5
  val nbCardsDrawnInDrawPhase: Int = 1
  val nbAttacksPerTurn: Int = 1
  val nbNormalSummonsPerTurn: Int = 1

  // Current turn variables
  var nbAttacks: mutable.Map[Card, Int] = _
  var nbNormalSummons = 0

  def start(teams: Array[Team]): Unit = {
    // Starts game
    interface.startGame(teams)
    // Give starting hand and life to players
    teams.foreach(_.players.foreach(p => {
      p.life = startLife
      interface.firstDraw(p, p.draw(startHand), teams)
    }))
    interface.wins(playerTurnLoop(teams, new Schedule(teams)))
  }

  @tailrec
  private def playerTurnLoop(teams: Array[Team], schedule: Schedule): Array[Team] = {
    playerTurn(schedule.nextPlayerTurn(), teams, schedule) match {
      // We have winners, the game is over
      case Some(x) => x
      // We don't have winners, we start the next player's turn
      case None => playerTurnLoop(teams, schedule)
    }
  }

  private def playerTurn(player: Player, teams: Array[Team], schedule: Schedule): Option[Array[Team]] = {
    var action: Action = null
    var res: Option[Array[Team]] = None

    nbAttacks = new mutable.HashMap()
    nbNormalSummons = 0
    // Turn starts
    interface.startTurn(player)
    // Player draws at the beginning of the turn
    playerDraw(player, teams, nbCardsDrawnInDrawPhase)
    // As long as the last action was not ending the turn and there is no winners we execute player actions
    while (action != EndTurn && res.isEmpty) {
      // Request a new action from the player
      action = player.play(this, teams)
      // If action is a human input, get it and use it as player action
      if (action == HumanInput) action = interface.humanInput(player, teams)
      // Execute the action
      action match {
        case CheckGraveyard(graveyardOwner) => interface.checkGraveyard(player, graveyardOwner)
        case NormalSummon(p, c) =>
          if (p == player && p.hand.contains(c) && player.monsterBoard.lengthCompare(nbMonsterZones) < 0
            && nbNormalSummons < nbNormalSummonsPerTurn) {
            nbNormalSummons += 1
            player.monsterBoard += c
            player.hand -= c
            interface.printBoardForPlayer(player, teams)
          }
          else interface.moveError(c)
        case PlaySpell(p, c) =>
          if (p == player && p.hand.contains(c) && player.mpBoard.lengthCompare(nbMpZones) < 0) {
            player.mpBoard += c
            player.hand -= c
            interface.printBoardForPlayer(player, teams)
            c.asInstanceOf[Spell].effects.foreach(effect => {
              applyEffect(player, teams, effect)
              interface.printBoardForPlayer(player, teams)
            })
            player.mpBoard -= c
            interface.printBoardForPlayer(player, teams)
          }
          else interface.moveError(c)
        case SetTrap(p, c) =>
          if (p == player && p.hand.contains(c) && player.mpBoard.lengthCompare(nbMpZones) < 0) {
            player.mpBoard += c
            player.hand -= c
            interface.printBoardForPlayer(player, teams)
          }
          else interface.moveError(c)
        case Attack(atkPlayer, atkCard, defPlayer, defCard) =>
          val atkCardMonster = atkCard.asInstanceOf[Monster]
          val defCardMonster = defCard.asInstanceOf[Monster]
          def atkCardNbAttacks = nbAttacks.getOrElse(atkCard, 0)
          if (schedule.turn > 1 && atkCardNbAttacks < nbAttacksPerTurn) {
            interface.attack(atkPlayer, atkCard, defPlayer, defCard)
            nbAttacks.put(atkCard, atkCardNbAttacks + 1)
            defCardMonster.life -= atkCardMonster.atk
            atkCardMonster.life -= defCardMonster.atk
            if (defCardMonster.life <= 0) {
              interface.cardDestroyed(defPlayer, defCard)
              defCardMonster.life = defCardMonster.data.origLife
              defPlayer.monsterBoard -= defCard
              defPlayer.graveyard += defCard
            }
            if (atkCardMonster.life <= 0) {
              interface.cardDestroyed(atkPlayer, atkCard)
              atkCardMonster.life = atkCardMonster.data.origLife
              atkPlayer.monsterBoard -= atkCard
              atkPlayer.graveyard += atkCard
            }
            interface.printBoardForPlayer(player, teams)
          }
          else interface.moveError(atkCard)
        case AttackPlayer(atkCard, defPlayer) =>
          val atkCardMonster = atkCard.asInstanceOf[Monster]
          def atkCardNbAttacks = nbAttacks.getOrElse(atkCard, 0)
          if (schedule.turn > 1 && atkCardNbAttacks < nbAttacksPerTurn && defPlayer.monsterBoard.isEmpty) {
            interface.attackPlayer(player, atkCard, defPlayer)
            nbAttacks.put(atkCard, atkCardNbAttacks + 1)
            defPlayer.life -= atkCardMonster.atk
            interface.printBoardForPlayer(player, teams)
          }
          else interface.moveError(atkCard)
        case EndTurn => interface.endTurn(player)
        case _ => throw DeckBrawlException("Unhandled action: " + action)
      }
      actionHistory += ((player, action))
      res = winners(teams)
    }
    res
  }

  private def applyEffect(player: Player, teams: Array[Team], effect: Effect): Unit = {
    effect match {
      case Draw(n) => playerDraw(player, teams, n)
      case Heal(n) => player.life += n
      case Camouflage => throw DeckBrawlException("UNIMPLEMENTED")
      case DestroyAttacking => throw DeckBrawlException("UNIMPLEMENTED")
    }
  }

  private def playerDraw(player: Player, teams: Array[Team], n: Int): Unit = {
    try {
      interface.draw(player, player.draw(n), teams)
    } catch {
      case _: DeckBrawlException =>
        player.life -= 5
    }
  }

  private def winners(teams: Array[Team]): Option[Array[Team]] = {
    val livingTeams = teams.filter(_.players.exists(_.life > 0))
    if (livingTeams.length == 1) Some(livingTeams) else None
  }
}

protected class Schedule(teams: Array[Team]) {
  var turn: Int = 0
  var teamIndex: Int = 0
  var playerIndexes: Array[Int] = Array.fill(teams.length)(0)

  def nextPlayerTurn(): Player = {
    val playerIndex = playerIndexes(teamIndex)
    val result = teams(teamIndex).players(playerIndex)
    playerIndexes(teamIndex) = if (playerIndex != teams(teamIndex).players.length - 1) playerIndex + 1 else 0
    teamIndex = if (teamIndex != teams.length - 1) teamIndex + 1 else 0
    turn = turn + 1
    result
  }
}