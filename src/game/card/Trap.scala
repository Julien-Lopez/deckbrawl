package game.card

class Trap(override val data: TrapData) extends Card(data)

class TrapData(override val id: Int, override val origName: String, override val origEffects: Array[Effect])
  extends CardData(id, origName, origEffects) {
  override def createCard(): Card = new Trap(this)
}