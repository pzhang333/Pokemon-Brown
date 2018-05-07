package cs.brown.edu.aelp.pokemmo.battle;

import cs.brown.edu.aelp.pokemmo.battle.events.KnockedOutEvent;
import cs.brown.edu.aelp.pokemmo.battle.events.SwitchOutEvent;

public class LassoEffect extends Effect {

  @Override
  public void handle(SwitchOutEvent event) {
    event.disallow();
  }

  @Override
  public void handle(KnockedOutEvent event) {
    this.getEffectSlot().deregister(this);
  }

}
