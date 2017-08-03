package player

import game.{Game, Team}

sealed class Human(override val profile: PlayerProfile) extends Player(profile)
{
  override def play(game: Game, teams: Array[Team]): Action = HumanInput
}
