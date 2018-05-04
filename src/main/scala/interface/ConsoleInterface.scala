package interface

import deckbrawl.DeckBrawl
import game._
import game.card._
import player._
import player.ai.Dummy

import scala.annotation.tailrec
import scala.collection.mutable
import scala.io.StdIn._

object ConsoleInterface extends DeckBrawlInterface {
  var cardIndexes: mutable.Map[Int, (Player, Card)] = _

  @tailrec
  override def startInterface(): Unit = {
    if (DeckBrawl.players.nonEmpty) {
      Console println "[N]ew game"
      Console println "[L]ogin"
    }
    Console println "[S]ign up"
    readLine match {
      case "N" | "n" =>
        new Game(this).start(Array(new Team("Team1", Array(new Human(DeckBrawl.players.head._2))),
          new Team("Lucky", Array(new Dummy(DeckBrawl.ais.head)))))
      case "L" | "l" =>
        login()
      case "S" | "s" =>
        Console println "Enter your name:"
        DeckBrawl.registerPlayer(readLine)
      case x => Console println "Unknown command: " + x
    }
    startInterface()
  }
  @tailrec
  private def login(): Unit = {
    Console println DeckBrawl.players.foldLeft("")((acc, p) => acc + p._1)
    DeckBrawl.players.get(readLine) match {
      case None =>
        Console println "Unknown name."
        login()
      case Some(p) => DeckBrawl.player = p
    }
  }
  override def startGame(teams: Array[Team]): Unit = Console println "Deck brawl!"
  override def firstDraw(p: Player, cards: List[Card], teams: Array[Team]): Unit = ()
  override def draw(p: Player, cards: List[Card], teams: Array[Team]): Unit = {
    Console println p + " draws!"
    printBoardForPlayer(p, teams)
  }
  override def checkGraveyard(p: Player, graveyardOwner: Player): Unit =
    Console println p + " checks " + graveyardOwner + "'s graveyard."
  override def attack(atkPlayer: Player, atkCard: Card, defPlayer: Player, defCard: Card): Unit =
    Console println atkPlayer + "'s " + atkCard + " attacks " + defPlayer + "'s " + defCard + "!"
  override def attackPlayer(atkPlayer: Player, atkCard: Card, defPlayer: Player): Unit =
    Console println atkPlayer + "'s " + atkCard + " attacks " + defPlayer + "!"
  override def cardDestroyed(player: Player, card: Card): Unit =
    Console println player + "'s " + card + " has been destroyed!"
  override def moveError(c: Card): Unit = Console println "Invalid card to play: " + c
  override def wins(winners: Array[Team]): Unit = {
    Console println "Game over! Winners:"
    winners.foreach(team => Console println team.name + ": " + team.players.foldLeft("")((res, p) => res + p))
  }
  @tailrec
  override def humanInput(human: Player, teams: Array[Team]): Action = {
    @tailrec
    def findPlayer(pName: String, teams: Array[Team]): Option[Player] = {
      if (teams.isEmpty) None else {
        val player = teams.head.players.find(_.profile.name == pName)
        if (player.isEmpty) findPlayer(pName, teams.tail) else player
      }
    }
    Console print "Your action: "
    val input = readLine

    // Create action from input

    val play = """play ([1-9][0-9]*)""".r
    val checkGraveyard = """check graveyard ([a-zA-Z_]+)""".r
    val attack = """atk ([1-9][0-9]*) ([1-9][0-9]*)""".r
    val attackPlayer = """atk ([1-9][0-9]*) ([a-zA-Z_]+)""".r
    input match {
      case play(i) =>
        cardIndexes.get(i.toInt) match {
          case Some((p, c)) if p.hand.contains(c) =>
            (c match {
              case _: Monster => NormalSummon
              case _: Spell => PlaySpell
              case _: Trap => SetTrap
            })(p, c)
          case _ =>
          Console println "Invalid number for a card: " + i
          humanInput(human, teams)
        }
      case checkGraveyard(pName) =>
        findPlayer(pName, teams) match {
          case None =>
            Console println "Unknown player: " + pName
            humanInput(human, teams)
          case Some(player) => CheckGraveyard(player)
        }
      case attack(i, j) =>
        (cardIndexes.get(i.toInt), cardIndexes.get(j.toInt)) match {
          case (Some((atkPlayer, atkCard)), Some((defPlayer, defCard))) if atkPlayer.monsterBoard.contains(atkCard)
            && defPlayer.monsterBoard.contains(defCard) && atkPlayer == human =>
            Attack(atkPlayer, atkCard, defPlayer, defCard)
          case _ =>
            Console println "Invalid attack: " + input
            humanInput(human, teams)
        }
      case attackPlayer(i, pName) =>
        (cardIndexes.get(i.toInt), findPlayer(pName, teams)) match {
          case (Some((atkPlayer, atkCard)), Some(player)) if atkPlayer.monsterBoard.contains(atkCard) =>
            AttackPlayer(atkCard, player)
          case _ =>
            Console println "Invalid attack: " + input
            humanInput(human, teams)
        }
      case "end turn" => EndTurn
      case _ =>
        Console println "Invalid move: " + input
        humanInput(human, teams)
    }
  }

  override def startTurn(player: Player): Unit = Console println player + "'s turn!"

  override def endTurn(player: Player): Unit = Console println player + " ends turn!"

  override def printBoardForPlayer(player: Player, teams: Array[Team]): Unit = {
    var index = 0
    def printCards(owner: Player, cards: Traversable[Card]): Unit =
      Console println cards.foldLeft("")((acc, c) => {
        index += 1
        cardIndexes.put(index, (owner, c))
        acc + index + ":" + c + " "
      })
    cardIndexes = new mutable.HashMap()
    teams.foreach(team => team.players.foreach(p => {
      Console println p.profile.name + "[" + p.life + "]"
      Console print "Monster board:"
      printCards(p, p.monsterBoard)
      Console print "MP board:"
      printCards(p, p.mpBoard)
    }))
    Console print "Hand: "
    printCards(player, player.hand)
  }
}
