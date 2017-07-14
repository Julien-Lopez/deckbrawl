package game.card

class Trap(override val id: Int, override val origName: String, override val origEffects: Array[Effect])
  extends Card(id, origName, origEffects) {
}
