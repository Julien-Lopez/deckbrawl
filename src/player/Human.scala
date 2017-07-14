package player

sealed class Human(override val name: String) extends Player
{
  override def play(): Action = HumanInput
}
