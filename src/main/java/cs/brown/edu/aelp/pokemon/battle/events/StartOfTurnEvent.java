package cs.brown.edu.aelp.pokemon.battle.events;

import cs.brown.edu.aelp.pokemon.battle.Battle;

public class StartOfTurnEvent extends BattleEvent {

  public StartOfTurnEvent(Battle battle) {
    super(battle);
  }

}
