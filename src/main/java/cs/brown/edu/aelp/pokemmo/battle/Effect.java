package cs.brown.edu.aelp.pokemmo.battle;

import cs.brown.edu.aelp.pokemmo.battle.events.AttackEvent;
import cs.brown.edu.aelp.pokemmo.battle.events.StartOfBattleEvent;
import cs.brown.edu.aelp.pokemmo.battle.events.StartOfTurnEvent;

public abstract class Effect {

  private EffectSlot effectSlot = null;

  public void handle(StartOfTurnEvent event) {
    System.out.printf("Got Start of Turn Event -- No handler\n");
  }

  public void handle(StartOfBattleEvent startOfBattleEvent) {
    System.out.printf("Got Start of Battle Event -- No handler\n");
  }

  public void handle(AttackEvent event) {
    System.out.printf("Got Attack Event -- No handler\n");
  }

  public void setEffectSlot(EffectSlot effectSlot) {
    this.effectSlot = effectSlot;
  }

  protected EffectSlot getEffectSlot() {
    return effectSlot;
  }

}
