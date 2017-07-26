package player

import deckbrawl.DeckBrawl
import game.{Game, Team}
import game.card.Card

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer
import scala.util.Random

abstract class Player(val name: String) {
  val deck: ListBuffer[Card] = ListBuffer.fill(40)(if (Random.nextBoolean()) DeckBrawl.cardDatabase(1).createCard()
                                                   else DeckBrawl.cardDatabase(2).createCard())
  val hand: ListBuffer[Card] = ListBuffer()
  var monsterBoard: ListBuffer[Card] = new ListBuffer()
  var mpBoard: ListBuffer[Card] = new ListBuffer()
  val graveyard: ListBuffer[Card] = ListBuffer()
  var life: Int = 0

  def draw(n: Int): List[Card] = {
    @tailrec
    def draw(res: List[Card], i: Int): List[Card] = if (i < n) draw(deck.remove(0) :: res, i + 1) else res
    val drawn = draw(Nil, 0)
    hand ++= drawn
    drawn
  }
  def take(card: Card): Unit = hand += card
  def play(game: Game, teams: Array[Team]): Action

  override def toString: String = name
}
