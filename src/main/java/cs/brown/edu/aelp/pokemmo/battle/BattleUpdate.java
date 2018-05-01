package cs.brown.edu.aelp.pokemmo.battle;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import cs.brown.edu.aelp.pokemon.Main;

public class BattleUpdate {

  public static enum SummaryType {
    FIGHT, SWITCH
  }

  public static class Summary {

    private Integer attackingId;

    private Integer attackingHealth;

    private Integer defendingHealth;

    private Integer defendingId;

    private String attackAnim;

    private String defendAnim;

    private String msg;

    private Integer type;

    public Summary(SummaryType type, Integer attackingId,
        Integer attackingHealth, String attackAnim, Integer defendingId,
        Integer defendingHealth, String defendAnim, String msg) {
      super();
      this.type = type.ordinal();
      this.attackingId = attackingId;
      this.attackingHealth = attackingHealth;
      this.defendingHealth = defendingHealth;
      this.defendingId = defendingId;
      this.attackAnim = attackAnim;
      this.defendAnim = defendAnim;
      this.msg = msg;
    }

  }

  private List<Summary> summaries = new LinkedList<>();

  public void addSummary(Summary s) {
    summaries.add(s);
  }

  public static class BattleUpdateAdapter
      implements JsonSerializer<BattleUpdate> {

    @Override
    public JsonElement serialize(BattleUpdate src, Type typeOfSrc,
        JsonSerializationContext ctx) {
      JsonObject o = new JsonObject();

      o.add("summaries", Main.GSON().toJsonTree(src.summaries));

      return o;
    }

  }
}
