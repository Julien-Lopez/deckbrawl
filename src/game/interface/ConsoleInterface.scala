package game.interface

import java.text.ParseException
import java.util.NoSuchElementException

import game._
import game.card.Card
import player._

import scala.annotation.tailrec
import scala.io.StdIn._

object ConsoleInterface extends GameInterface {
  override def startGame(teams: Array[Team]): Unit = Console println "Deck brawl!"
  override def order(teams: Array[Team]): Array[Team] = teams
  override def draw(p: Player, cards: List[Card]): Unit = Console println p + " draws!"
  override def checkGraveyard(p: Player, graveyardOwner: Player): Unit =
    Console println p + " checks " + graveyardOwner + "'s graveyard."
  override def moveError(c: Card): Unit = Console println "Invalid card to play: " + c
  override def wins(winners: Array[Team]): Unit = Console println winners + " win!"
  @tailrec
  override def humanInput(human: Player, teams: Array[Team]): Action = {
    @tailrec
    def findPlayer(pName: String, teams: Array[Team]): Player = {
      teams.head.players.find(_.name == pName) match {
        case None => findPlayer(pName, teams.tail)
        case Some(p) => p
      }
    }
    Console println Card.printCardsWithIndexes(human.hand)
    Console print "Your action: "
    val input = readLine

    // Create action from input

    val play = """play ([1-9][0-9]*)""".r
    val checkGraveyard = """check graveyard ([a-zA-Z_]+)""".r
    input match {
      case play(i) =>
        val n = i.toInt
        if (n <= human.hand.size) PlayCard(n - 1)
        else {
          Console println "Invalid number for a card: " + n
          humanInput(human, teams)
        }
      case checkGraveyard(p) =>
        try {
          CheckGraveyard(findPlayer(p, teams))
        }
        catch {
          case _: NoSuchElementException =>
            Console println "Unknown player: " + p
            humanInput(human, teams)
          case e: Throwable => throw e
        }
      case "end turn" => EndTurn
      case _ =>
        Console println "Invalid move: " + input
        humanInput(human, teams)
    }
  }

  override def startTurn(player: Player): Unit = Console println player + "'s turn!"

  override def endTurn(player: Player): Unit = Console println player + " ends turn!"

  @tailrec
  override def readCard(max: Int): Int =
    try
    {
      val res = readf1("{0,number,integer}").asInstanceOf[Long].toInt
      if (res > max || res < 1)
        throw new ParseException("Out of bounds game.card.", 0)
      res - 1
    }
    catch
    {
      case _: ParseException =>
        Console println "Invalid game.card, please enter a number between 1 and " + max
        readCard(max)
      case e: Throwable => throw e
    }
}
