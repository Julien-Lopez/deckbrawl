package game.interface

import game._
import game.card.Card
import player._

import scala.annotation.tailrec
import scala.collection.mutable
import scala.io.StdIn._

object ConsoleInterface extends GameInterface {
  var cardIndexes: mutable.Map[Int, (Player, Int)] = _
  override def startGame(teams: Array[Team]): Unit = Console println "Deck brawl!"
  override def order(teams: Array[Team]): Array[Team] = teams
  override def firstDraw(p: Player, cards: List[Card], teams: Array[Team]): Unit = ()
  override def draw(p: Player, cards: List[Card], teams: Array[Team]): Unit = {
    Console println p + " draws!"
    printBoardForPlayer(p, teams)
  }
  override def checkGraveyard(p: Player, graveyardOwner: Player): Unit =
    Console println p + " checks " + graveyardOwner + "'s graveyard."
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
        val player = teams.head.players.find(_.name == pName)
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
          case Some((p, j)) if j < p.hand.size => PlayCard(p, j)
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
          case (Some((atkPlayer, atkIndex)), Some((defPlayer, defIndex)))
            if atkIndex < atkPlayer.monsterBoard.size && defIndex < defPlayer.monsterBoard.size =>
            Attack(atkPlayer, atkIndex, defPlayer, defIndex)
          case _ =>
            Console println "Invalid attack: " + input
            humanInput(human, teams)
        }
      case attackPlayer(i, pName) =>
        (cardIndexes.get(i.toInt), findPlayer(pName, teams))  match {
          case (Some((atkPlayer, atkIndex)), Some(player)) if atkIndex < atkPlayer.monsterBoard.size =>
            AttackPlayer(atkPlayer, atkIndex, player)
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
    def printCards(cards: Traversable[Card]): Unit =
      if (cards.nonEmpty)
        Console println cards.foldLeft(("", 0))((acc, c) => {
          index += 1
          cardIndexes.put(index, (player, acc._2))
          (acc._1 + index + ":" + c + " ", acc._2 + 1)
        })._1
    cardIndexes = new mutable.HashMap()
    teams.foreach(team => team.players.foreach(p => {
      Console println p.name + "[" + p.life + "]:"
      printCards(p.monsterBoard)
    }))
    Console print "Hand: "
    printCards(player.hand)
  }
}
