package cs.brown.edu.aelp.pokemmo.battle.summaries;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import cs.brown.edu.aelp.pokemmo.battle.BattleSummary;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;
import cs.brown.edu.aelp.pokemon.Main;
import java.lang.reflect.Type;

public class SwitchSummary extends BattleSummary {

  private final Pokemon pokemonIn;

  private final Pokemon pokemonOut;

  public SwitchSummary(Pokemon pokeIn, Pokemon pokeOut) {
    super(SummaryType.SWITCH,
        String.format("%s (%s) swapped in for %s (%s).", pokeIn.getNickname(),
            pokeIn.getSpecies(), pokeOut.getNickname(), pokeOut.getSpecies()));

    this.pokemonIn = pokeIn.snapshot();
    this.pokemonOut = pokeOut.snapshot();
  }

  public static class SwitchSummaryAdapter
      implements JsonSerializer<SwitchSummary> {

    @Override
    public JsonElement serialize(SwitchSummary src, Type typeOfSrc,
        JsonSerializationContext context) {

      JsonObject o = new JsonObject();
      o.add("pokemonIn", Main.GSON().toJsonTree(src.pokemonIn));
      o.add("pokemonOut", Main.GSON().toJsonTree(src.pokemonOut));

      return o;

    }

  }
}
