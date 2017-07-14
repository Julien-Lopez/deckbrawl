package game.card

class Spell(override val id: Int, override val origName: String, override val origEffects: Array[Effect])
  extends Card(id, origName, origEffects) {
}
