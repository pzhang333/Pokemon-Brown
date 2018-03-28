package cs.brown.edu.aelp.pokemmo.battle.events;

import cs.brown.edu.aelp.pokemmo.battle.Battle;
import cs.brown.edu.aelp.pokemmo.battle.Effect;
import cs.brown.edu.aelp.pokemmo.battle.EffectSlot;
import cs.brown.edu.aelp.pokemmo.trainer.Trainer;

public class EndOfBattleEvent extends BattleEvent {

  private Trainer trainer;

  public EndOfBattleEvent(Battle battle, Trainer trainer) {
    super(battle);

    this.trainer = trainer;
  }

  @Override
  public void invokeOn(EffectSlot slot) {
    for (Effect e : slot.getEffects()) {
      e.handle(this);
    }
  }

}
