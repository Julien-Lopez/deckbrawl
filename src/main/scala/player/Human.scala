package player

import game.{Game, Team}

sealed class Human(override val name: String) extends Player(name)
{
  override def play(game: Game, teams: Array[Team]): Action = HumanInput
}
