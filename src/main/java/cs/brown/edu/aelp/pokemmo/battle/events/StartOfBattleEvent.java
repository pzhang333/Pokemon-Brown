package cs.brown.edu.aelp.pokemmo.battle.events;

import cs.brown.edu.aelp.pokemmo.battle.Battle;
import cs.brown.edu.aelp.pokemmo.battle.Effect;
import cs.brown.edu.aelp.pokemmo.battle.EffectSlot;

public class StartOfBattleEvent extends BattleEvent {

  public StartOfBattleEvent(Battle battle) {
    super(battle);
  }

  @Override
  public void invokeOn(EffectSlot slot) {
    for (Effect e : slot.getEffects()) {
      e.handle(this);
    }
  }
}
