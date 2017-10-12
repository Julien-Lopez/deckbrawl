package player

import deckbrawl.{DeckBrawl, DeckBrawlException}
import game.{Game, Team}
import game.card.{Card, CardData}

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer
import scala.util.Random

abstract class Player(val profile: PlayerProfile) {
  val deck: ListBuffer[Card] = ListBuffer.fill(40)(
    DeckBrawl.cardDatabase(Random.nextInt(DeckBrawl.cardDatabase.length)).createCard())
  val hand: ListBuffer[Card] = ListBuffer()
  var monsterBoard: ListBuffer[Card] = new ListBuffer()
  var mpBoard: ListBuffer[Card] = new ListBuffer()
  val graveyard: ListBuffer[Card] = ListBuffer()
  var life: Int = 0

  def draw(n: Int): List[Card] = {
    @tailrec
    def draw(res: List[Card], i: Int): List[Card] =
      if (i < n)
        if (deck.nonEmpty) draw(deck.remove(0) :: res, i + 1) else throw DeckBrawlException("Deck is empty")
      else res
    val drawn = draw(Nil, 0)
    hand ++= drawn
    drawn
  }
  def take(card: Card): Unit = hand += card
  def play(game: Game, teams: Array[Team]): Action

  override def toString: String = profile.name
}

class PlayerProfile(val name: String) extends Serializable {
  val idName: String = name.toLowerCase
  val deckRecipes: ListBuffer[ListBuffer[CardData]] = ListBuffer()
}