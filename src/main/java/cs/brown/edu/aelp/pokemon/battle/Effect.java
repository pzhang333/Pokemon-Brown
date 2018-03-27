package cs.brown.edu.aelp.pokemon.battle;

import cs.brown.edu.aelp.pokemon.battle.events.BattleEvent;
import cs.brown.edu.aelp.pokemon.battle.events.StartOfTurnEvent;

public abstract class Effect {

  private EffectSlot effectSlot = null;

  public void handle(BattleEvent event) {

    if (event instanceof StartOfTurnEvent) {
      handle((StartOfTurnEvent) event);
      return;
    }

    System.out.printf("Got BattleEvent -- No handler\n");
  }

  public void handle(StartOfTurnEvent event) {
    System.out.printf("Got Start of Turn Event -- No handler\n");
  }

  public void setEffectSlot(EffectSlot effectSlot) {
    this.effectSlot = effectSlot;
  }

  protected EffectSlot getEffectSlot() {
    return effectSlot;
  }

}
