package cs.brown.edu.aelp.pokemmo.battle.events;

import cs.brown.edu.aelp.pokemmo.battle.Battle;
import cs.brown.edu.aelp.pokemmo.battle.Effect;
import cs.brown.edu.aelp.pokemmo.battle.EffectSlot;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;

public class KnockedOutEvent extends BattleEvent {

  private final Pokemon attackingPokemon;

  private final Pokemon koedPokemon;

  public KnockedOutEvent(Battle battle, Pokemon attackingPokemon,
      Pokemon koedPokemon) {
    super(battle);

    this.attackingPokemon = attackingPokemon;
    this.koedPokemon = koedPokemon;
  }

  @Override
  public void invokeOn(EffectSlot slot) {
    for (Effect e : slot.getEffects()) {
      e.handle(this);
    }
  }
}
