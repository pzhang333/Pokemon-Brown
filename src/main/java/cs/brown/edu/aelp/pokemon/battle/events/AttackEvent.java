package cs.brown.edu.aelp.pokemon.battle.events;

import cs.brown.edu.aelp.pokemon.battle.Battle;
import cs.brown.edu.aelp.pokemon.trainer.Trainer;

public class AttackEvent extends BattleEvent {

  private final Trainer attackingTrainer;

  private final Trainer defendingTrainer;

  public AttackEvent(Battle battle, Trainer attackingTrainer,
      Trainer defendingTrainer) {
    super(battle);
    this.attackingTrainer = attackingTrainer;
    this.defendingTrainer = defendingTrainer;
  }

  /**
   * @return the attacking
   */
  public Trainer getAttackingTrainer() {
    return attackingTrainer;
  }

  public Trainer getDefendingTrainer() {
    return defendingTrainer;
  }

}
