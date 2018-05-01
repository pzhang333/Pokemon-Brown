package cs.brown.edu.aelp.pokemmo.battle;

public abstract class BattleSummary {

  public static enum SummaryType {
    FIGHT, SWITCH
  }

  private final Integer type;

  protected BattleSummary(SummaryType type) {
    this.type = type.ordinal();
  }

}
