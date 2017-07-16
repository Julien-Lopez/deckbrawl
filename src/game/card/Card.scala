package game.card

abstract class Card(val data: CardData) {
  var name: String = data.origName
  var effects: Traversable[Effect] = data.origEffects

  override def toString: String = name
}

abstract class CardData(val id: Int, val origName: String, val origEffects: Array[Effect]) {
  def createCard(): Card
}