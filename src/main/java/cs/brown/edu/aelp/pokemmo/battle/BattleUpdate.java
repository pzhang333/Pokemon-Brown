package cs.brown.edu.aelp.pokemmo.battle;

import java.util.LinkedList;
import java.util.List;

public class BattleUpdate {

  private List<BattleSummary> summaries = new LinkedList<>();

  public List<BattleSummary> getSummaries() {
    return summaries;
  }

  public void addSummary(BattleSummary s) {
    summaries.add(s);
  }

}
