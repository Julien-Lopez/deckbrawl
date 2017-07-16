package player

sealed trait Action
{
  override def toString: String = this.getClass.getSimpleName.replace("$", "")
}

case class PlayCard(p: Player, i: Int) extends Action
case class CheckGraveyard(p: Player) extends Action
case class Attack(atkPlayer: Player, atkCard: Int, defPlayer: Player, defCard: Int) extends Action
case class AttackPlayer(atkPlayer: Player, atkCard: Int, defPlayer: Player) extends Action
case object EndTurn extends Action
case object HumanInput extends Action
