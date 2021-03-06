package cs.brown.edu.aelp.pokemmo.battle.events;

import cs.brown.edu.aelp.pokemmo.battle.Battle;
import cs.brown.edu.aelp.pokemmo.battle.Effect;
import cs.brown.edu.aelp.pokemmo.battle.EffectSlot;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;

public class SwitchInEvent extends BattleEvent {

  private final Pokemon in;

  private final Pokemon out;

  public SwitchInEvent(Battle battle, Pokemon in, Pokemon out) {
    super(battle);

    this.in = in;
    this.out = out;
  }

  @Override
  public void invokeOn(EffectSlot slot) {
    for (Effect e : slot.getEffects()) {
      e.handle(this);
    }
  }

}
