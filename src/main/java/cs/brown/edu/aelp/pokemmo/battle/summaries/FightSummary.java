package cs.brown.edu.aelp.pokemmo.battle.summaries;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import cs.brown.edu.aelp.pokemmo.battle.BattleSummary;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;
import cs.brown.edu.aelp.pokemon.Main;

public class FightSummary extends BattleSummary {

  private final Pokemon attacking;

  private final Pokemon defending;

  private final String message;

  private final String animation;

  public FightSummary(Pokemon attacking, Pokemon defending, String message,
      String animation) {
    super(SummaryType.FIGHT);

    this.attacking = attacking.snapshot();
    this.defending = defending.snapshot();
    this.message = message;
    this.animation = animation;
  }

  public static class FightSummaryAdapter
      implements JsonSerializer<FightSummary> {

    @Override
    public JsonElement serialize(FightSummary src, Type typeOfSrc,
        JsonSerializationContext context) {

      JsonObject o = new JsonObject();
      o.add("attacking", Main.GSON().toJsonTree(src.attacking));
      o.add("defending", Main.GSON().toJsonTree(src.defending));
      o.addProperty("message", src.message);
      o.addProperty("animation", src.animation);

      return o;

    }

  }
}
