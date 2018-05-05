package cs.brown.edu.aelp.pokemmo.battle;

public abstract class BattleSummary {

  public static enum SummaryType {
    FIGHT,
    SWITCH,
    MISC
  }

  private final Integer type;
  private String message;

  protected BattleSummary(SummaryType type, String message) {
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
