package game.card

class Spell(override val data: SpellData) extends Card(data)

class SpellData(override val id: Int, override val origName: String, override val origEffects: Array[Effect])
  extends CardData(id, origName, origEffects) {
  override def createCard(): Card = new Spell(this)
}