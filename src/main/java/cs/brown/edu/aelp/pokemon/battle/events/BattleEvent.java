package cs.brown.edu.aelp.pokemon.battle.events;

import cs.brown.edu.aelp.pokemon.battle.Battle;

public class BattleEvent {

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

}
