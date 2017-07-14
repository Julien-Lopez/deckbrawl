package player

sealed trait Action
{
  override def toString: String = this.getClass.getSimpleName.replace("$", "")
}

case class PlayCard(i: Int) extends Action {
}

case object HumanInput extends Action {
}

case class CheckGraveyard(p: Player) extends Action {
}

case object EndTurn extends Action {
}
