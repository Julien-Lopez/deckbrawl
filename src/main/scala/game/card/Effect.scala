package game.card

import deckbrawl.DeckBrawlException

sealed trait Effect extends Serializable
{
  override def toString: String = this.getClass.getSimpleName.replace("$", "")
}

object Effect {
  def fromString(s: String): Effect = {
    val draw = """draw\(([1-9][0-9]*)\)""".r
    val destroyAttacking = """destroyAttacking""".r
    s match {
      case draw(n) => Draw(n.toInt)
      case "camouflage" => Camouflage
      case destroyAttacking() => DestroyAttacking
      case _ => throw DeckBrawlException("Unknown effect: " + s)
    }
  }
}

case class Draw(nbCards: Int) extends Effect

case object Camouflage extends Effect

case object DestroyAttacking extends Effect