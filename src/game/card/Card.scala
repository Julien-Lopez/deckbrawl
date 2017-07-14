package game.card

import scala.annotation.tailrec

abstract class Card(val id: Int, val origName: String, val origEffects: Array[Effect]) {
  var name: String = origName
  var effects: Traversable[Effect] = origEffects

  override def toString: String = name
}

object Card {
  def printCards(cards: Traversable[Card]): String = cards.mkString(", ")
  def printCardsWithIndexes(cards: Traversable[Card]): String = printer(cards)

  private def printer(cards: Traversable[Card]): String =
  {
    @tailrec
    def printer(index: Int, res: String, cards: Traversable[Card]): String =
      if (cards.isEmpty) ""
      else if (cards.size == 1) res + index + ":" + cards.head
      else printer(index + 1, res + index + ":" + cards.head + ", ", cards.tail)
    printer(1, "", cards)
  }
}
