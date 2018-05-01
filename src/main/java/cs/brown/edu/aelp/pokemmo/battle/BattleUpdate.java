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

  private List<BattleSummary> summaries = new LinkedList<>();

  public List<BattleSummary> getSummaries() {
    return summaries;
  }

  public void addSummary(BattleSummary s) {
    summaries.add(s);
  }

  public static class BattleUpdateAdapter
      implements JsonSerializer<BattleUpdate> {

    @Override
    public JsonElement serialize(BattleUpdate src, Type typeOfSrc,
        JsonSerializationContext context) {

      JsonObject o = new JsonObject();
      o.add("summaries", Main.GSON().toJsonTree(src.summaries));

      return o;

    }

  }
}
