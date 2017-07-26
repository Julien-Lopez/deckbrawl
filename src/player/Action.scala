package player

import game.card.Card

sealed trait Action
{
  override def toString: String = this.getClass.getSimpleName.replace("$", "")
}

case class PlayCard(p: Player, c: Card) extends Action
case class CheckGraveyard(p: Player) extends Action
case class Attack(atkPlayer: Player, atkCard: Card, defPlayer: Player, defCard: Card) extends Action
case class AttackPlayer(atkCard: Card, defPlayer: Player) extends Action
case object EndTurn extends Action
case object HumanInput extends Action
