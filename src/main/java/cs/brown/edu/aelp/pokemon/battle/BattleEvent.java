package cs.brown.edu.aelp.pokemon.battle;

/**
 * The battle event class.
 */
public class BattleEvent {

  // TODO: Add way, way more...
  /**
   * Battle event enum. Missing many event types.
   */
  public enum BattleEventType {
    BEFORE_MOVE, ON_KO;
  }

  private final BattleEventType type;

  private final Battle battle;

  public BattleEvent(BattleEventType type, Battle battle) {
    this.type = type;
    this.battle = battle;
  }

  /**
   * @return the type
   */
  public BattleEventType getType() {
    return type;
  }

  /**
   * @return the battle
   */
  public Battle getBattle() {
    return battle;
  }

}
