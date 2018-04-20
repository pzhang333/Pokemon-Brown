package cs.brown.edu.aelp.pokemmo.battle;

import cs.brown.edu.aelp.pokemmo.battle.events.AttackEvent;
import cs.brown.edu.aelp.pokemmo.battle.events.EndOfBattleEvent;
import cs.brown.edu.aelp.pokemmo.battle.events.EndOfTurnEvent;
import cs.brown.edu.aelp.pokemmo.battle.events.KnockedOutEvent;
import cs.brown.edu.aelp.pokemmo.battle.events.StartOfBattleEvent;
import cs.brown.edu.aelp.pokemmo.battle.events.StartOfTurnEvent;
import cs.brown.edu.aelp.pokemmo.battle.events.SwitchInEvent;
import cs.brown.edu.aelp.pokemmo.battle.events.SwitchOutEvent;

public abstract class Effect {

  private EffectSlot effectSlot = null;

  public void handle(StartOfTurnEvent event) {
    System.out.printf("Got Start of Turn Event -- No handler\n");
  }

  public void handle(EndOfTurnEvent event) {
    System.out.printf("Got End of Turn Event -- No handler\n");
  }

  public void handle(StartOfBattleEvent startOfBattleEvent) {
    System.out.printf("Got Start of Battle Event -- No handler\n");
  }

  public void handle(EndOfBattleEvent endOffBattleEvent) {
    System.out.printf("Got End of Battle Event -- No handler\n");
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

  public void handle(SwitchInEvent switchInEvent) {
    System.out.printf("Got Switch In Event -- No handler\n");
  }

  public void handle(SwitchOutEvent switchOutEvent) {
    System.out.printf("Got Switch Out Event -- No handler\n");
  }

  public void handle(KnockedOutEvent knockedOutEvent) {
    System.out.printf("Got Knocked Out Event -- No handler\n");
  }
}
