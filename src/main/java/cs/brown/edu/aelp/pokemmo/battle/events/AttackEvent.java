package cs.brown.edu.aelp.pokemmo.battle.events;

import cs.brown.edu.aelp.pokemmo.battle.Battle;
import cs.brown.edu.aelp.pokemmo.battle.Effect;
import cs.brown.edu.aelp.pokemmo.battle.EffectSlot;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;
import cs.brown.edu.aelp.pokemmo.trainer.Trainer;

/**
 * An attack event.
 */
public class AttackEvent extends BattleEvent {

  private final Trainer attackingTrainer;

  private final Pokemon attackingPokemon;

  private final Trainer defendingTrainer;

  private final Pokemon defendingPokemon;

  /**
   * Construct an AttackEvent.
   *
   * @param battle
   *          The battle in which the event is occuring.
   * @param attackingTrainer
   *          The attacking trainer.
   * @param attackingPokemon
   *          The attacking pokemon.
   * @param defendingTrainer
   *          The defending trainer.
   * @param defendingPokemon
   *          The defending pokemon.
   */
  public AttackEvent(Battle battle, Trainer attackingTrainer,
      Pokemon attackingPokemon, Trainer defendingTrainer,
      Pokemon defendingPokemon) {
    super(battle);
    this.attackingTrainer = attackingTrainer;
    this.attackingPokemon = attackingPokemon;
    this.defendingTrainer = defendingTrainer;
    this.defendingPokemon = defendingPokemon;
  }

  /**
   * Get the attacking trainer.
   *
   * @return the attacking
   */
  public Trainer getAttackingTrainer() {
    return attackingTrainer;
  }

  /**
   * Get the defending trainer.
   *
   * @return The defending trainer.
   */
  public Trainer getDefendingTrainer() {
    return defendingTrainer;
  }

  /**
   * @return the attackingPokemon
   */
  public Pokemon getAttackingPokemon() {
    return attackingPokemon;
  }

  /**
   * @return the defendingPokemon
   */
  public Pokemon getDefendingPokemon() {
    return defendingPokemon;
  }

  @Override
  public void invokeOn(EffectSlot slot) {
    for (Effect e : slot.getEffects()) {
      e.handle(this);
    }
  }

}
