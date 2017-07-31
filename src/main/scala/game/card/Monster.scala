package game.card

class Monster(override val data: MonsterData) extends Card(data) {
  var atk: Int = data.origAtk
  var life: Int = data.origLife

  override def toString: String = super.toString + "(" + atk + ", " + life + ")"
}

class MonsterData(override val id: Int, override val origName: String, override val origEffects: Array[Effect],
                  val origAtk: Int, val origLife: Int) extends CardData(id, origName, origEffects) {
  override def createCard(): Card = new Monster(this)
}