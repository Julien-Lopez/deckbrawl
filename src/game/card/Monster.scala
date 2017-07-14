package game.card

class Monster(override val id: Int, override val origName: String, override val origEffects: Array[Effect],
              val origAtk: Int, val origLife: Int) extends Card(id, origName, origEffects) {
  val atk: Int = origAtk
  val life: Int = origLife

  override def toString: String = super.toString + "(" + atk + ", " + life + ")"
}
