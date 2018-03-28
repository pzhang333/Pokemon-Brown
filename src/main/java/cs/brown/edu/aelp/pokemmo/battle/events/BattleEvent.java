package cs.brown.edu.aelp.pokemmo.battle.events;

import cs.brown.edu.aelp.pokemmo.battle.Battle;
import cs.brown.edu.aelp.pokemmo.battle.EffectSlot;

public abstract class BattleEvent {

  /*
   * public enum BattleEventType { ON_BATTLE_START, ATTACK, AFTER_ATTACK,
   * DEFEND, AFTER_DEFEND, ON_HIT, ON_KO, ON_SWITCH, ON_ITEM, ON_BATTLE_END,
   * END_OF_TURN, START_OF_TURN };
   */

  private final Battle battle;

  public BattleEvent(Battle battle) {
    this.battle = battle;
  }

  /**
   * @return the battle
   */
  public Battle getBattle() {
    return battle;
  }

  public abstract void invokeOn(EffectSlot slot);
}
