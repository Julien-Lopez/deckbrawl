package game.card

sealed trait Effect
{
  override def toString: String = this.getClass.getSimpleName.replace("$", "")
}

case class Draw(nbCards: Int)

case object Camouflage