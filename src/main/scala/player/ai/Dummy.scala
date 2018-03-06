package player.ai

import game.{Game, Team}
import game.card.{Monster, Spell, Trap}
import player._

sealed class Dummy(override val profile: PlayerProfile) extends Player(profile)
{
  override def play(game: Game, teams: Array[Team]): Action = {
    if (game.nbNormalSummons < game.nbNormalSummonsPerTurn && monsterBoard.lengthCompare(5) < 0
      && hand.exists(c => c.isInstanceOf[Monster]))
      NormalSummon(this, hand.filter(c => c.isInstanceOf[Monster]).head)
    else if (hand.exists(c => c.isInstanceOf[Spell]) && mpBoard.lengthCompare(5) < 0)
      PlaySpell(this, hand.filter(c => c.isInstanceOf[Spell]).head)
    else if (hand.exists(c => c.isInstanceOf[Trap]) && mpBoard.lengthCompare(5) < 0)
      SetTrap(this, hand.filter(c => c.isInstanceOf[Trap]).head)
    else if (monsterBoard.exists(c => game.nbAttacks.getOrElse(c, 0) < game.nbAttacksPerTurn)) {
      val opponent = teams.filter(t => !t.players.contains(this)).head.players.head
      val atkCard = monsterBoard.find(c => game.nbAttacks.getOrElse(c, 0) < game.nbAttacksPerTurn).get
      if (opponent.monsterBoard.isEmpty)
        AttackPlayer(atkCard, opponent)
      else
        Attack(this, atkCard, opponent, opponent.monsterBoard.head)
    }
    else EndTurn
  }
}
