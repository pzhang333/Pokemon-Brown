package cs.brown.edu.aelp.pokemmo.battle;

public class BattleSummary {

  public static enum SummaryType {
    FIGHT, SWITCH, HEAL, DAMAGE, ITEM
  }

  private final Integer type;
  private String message;

  public BattleSummary(SummaryType type, String message) {
    this.type = type.ordinal();
    this.message = message;
  }

  public void setMessage(String s) {
    this.message = s;
  }

  public String getMessage() {
    return this.message;
  }

}
