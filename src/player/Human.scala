package player

sealed class Human(override val name: String) extends Player(name)
{
  override def play(): Action = HumanInput
}
