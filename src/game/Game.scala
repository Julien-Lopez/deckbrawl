package game

import deckbrawl.DeckBrawlException
import game.interface.GameInterface
import player._

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer

class Game(protected val interface: GameInterface) {
  protected val actionHistory: ListBuffer[(Player, Action)] = ListBuffer()
  val startHand: Int = 5
  val startLife: Int = 30
  val nbCardsDrawnInDrawPhase: Int = 1

  def start(teams: Array[Team]): Unit = {
    val schedule = new Schedule(teams)

    // Starts game
    interface.startGame(teams)
    // Decide who starts
    interface.order(teams)
    // Give starting hand and life to players
    teams.foreach(team => team.players.foreach(p => {
      p.life = startLife
      interface.draw(p, p.draw(startHand))
    }))
    interface.wins(playerTurnLoop(teams, schedule))
  }

  @tailrec
  private def playerTurnLoop(teams: Array[Team], schedule: Schedule): Array[Team] = {
    val player = schedule.nextPlayerTurn()
    var action: Action = null
    var res: Option[Array[Team]] = None

    // Turn starts
    interface.startTurn(player)
    // Player draws at the beginning of the turn
    interface.draw(player, player.draw(nbCardsDrawnInDrawPhase))
    // As long as the last action was not ending the turn and there is no winners we execute player actions
    while (action != EndTurn && res.isEmpty) {
      // Request a new action from the player
      action = player.play()
      // If action is a human input, get it and use it as player action
      if (action == HumanInput) action = interface.humanInput(player, teams)
      // Execute the action
      action match {
        case CheckGraveyard(graveyardOwner) => interface.checkGraveyard(player, graveyardOwner)
        case PlayCard(i) => player.hand.remove(i)
        case EndTurn => interface.endTurn(player)
        case HumanInput => throw DeckBrawlException() // TODO: Better handling of HumanInput
      }
      actionHistory += ((player, action))
      res = winners(teams)
    }
    res match {
      // We exited the loop because we have winners, the game is over
      case Some(x) => x
      // We exited the loop because player's turn is over, we start the next player's turn
      case None => playerTurnLoop(teams, schedule)
    }
  }

  private def winners(teams: Array[Team]): Option[Array[Team]] = {
    val livingTeams = teams.filter(_.players.exists(_.life != 0))
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