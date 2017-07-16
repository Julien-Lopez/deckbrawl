package player

import game.card.Card

sealed trait Action
{
  override def toString: String = this.getClass.getSimpleName.replace("$", "")
}

case class PlayCard(i: Int) extends Action
case class CheckGraveyard(p: Player) extends Action
case class Attack(attacking: Card, defending: Card)
case class AttackPlayer(attacking: Card, player: Player)
case object EndTurn extends Action
case object HumanInput extends Action
