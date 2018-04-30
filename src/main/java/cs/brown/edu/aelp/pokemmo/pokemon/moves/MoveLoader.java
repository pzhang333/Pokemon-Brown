package cs.brown.edu.aelp.pokemmo.pokemon.moves;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cs.brown.edu.aelp.pokemmo.pokemon.PokeTypes;
import cs.brown.edu.aelp.pokemmo.pokemon.PokemonLoader;
import cs.brown.edu.aelp.pokemmo.pokemon.Status;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.Move.Builder;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.Move.MoveCategory;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.Move.MoveComplexity;

public final class MoveLoader {

  private static final String FILEPATH = "data/pokemon/moves.json";

  private static final Map<Integer, Move> overrides = new HashMap<>();

  public static void addOverride(Move m) {
    overrides.put(m.getId(), m);
  }

  public static Move getMoveById(Integer id) {

    try {
      JsonElement jsonElement = new JsonParser()
          .parse(new FileReader(FILEPATH));
      JsonObject jsonObject = jsonElement.getAsJsonObject();
      JsonObject move = jsonObject.getAsJsonObject(Integer.toString(id));

      Builder builder = new Move.Builder(id);

      Integer accuracy = move.get("accuracy").getAsInt();
      Integer basePower = move.get("basePower").getAsInt();
      String description = move.get("desc").getAsString();
      String shortDesc = move.get("shortDesc").getAsString();
      String name = move.get("name").getAsString();
      Integer pp = move.get("pp").getAsInt();
      Integer priority = move.get("priority").getAsInt();
      PokeTypes type = PokemonLoader
          .stringToType(move.get("type").getAsString());
      MoveComplexity complexity = stringToMoveComplexity(
          move.get("complexity").getAsString());
      MoveCategory category = stringToMoveCategory(
          move.get("category").getAsString());

      builder.withAccuracy(accuracy).withPower(basePower)
          .withDescription(description).withShortDescription(shortDesc)
          .withName(name).withPP(pp).withPriority(priority).ofType(type)
          .withComplexity(complexity).ofCategory(category);

      if (complexity == MoveComplexity.BUFF
          || complexity == MoveComplexity.DEBUFF) {
        String stat = move.get("stat").getAsString();
        Integer stages = move.get("stages").getAsInt();
        builder.affectsStat(stat);
        builder.withStages(stages);
      } else if (complexity == MoveComplexity.RECOIL) {
        JsonArray fraction = move.getAsJsonArray("recoil");
        Double numerator = fraction.get(0).getAsDouble();
        Double denominator = fraction.get(1).getAsDouble();
        builder.withRecoil(numerator / denominator);
      } else if (complexity == MoveComplexity.DMG_STATUS) {
        JsonArray fraction = move.getAsJsonArray("chance");
        Double numerator = fraction.get(0).getAsDouble();
        Double denominator = fraction.get(1).getAsDouble();
        Status status = Status
            .valueOf(move.get("status").getAsString().toUpperCase());
        builder.afflictsStatus(status);
        builder.withStatusChance(numerator / denominator);
      }

      Move m = builder.build();
      if (overrides.containsKey(id)) {
        try {
          return overrides.get(id).getClass().getConstructor(Move.class)
              .newInstance(m);
        } catch (Exception e) {
          return m;
        }
      }

      return m;
    } catch (FileNotFoundException e) {
      // Should not occur since path is hard-coded in
      e.printStackTrace();
    }
    return null;
  }

  public static MoveCategory stringToMoveCategory(String moveCatergory) {
    switch (moveCatergory) {
    case ("Physical"):
      return MoveCategory.PHYSICAL;
    case ("Special"):
      return MoveCategory.SPECIAL;
    default:
      return MoveCategory.NONE;
    }
  }

  public static MoveComplexity stringToMoveComplexity(String moveComplexity) {
    switch (moveComplexity) {
    case ("Basic"):
      return MoveComplexity.BASIC;
    case ("Buff"):
      return MoveComplexity.BUFF;
    case ("Debuff"):
      return MoveComplexity.DEBUFF;
    case ("Status"):
      return MoveComplexity.STATUS;
    case ("Dmg_Status"):
      return MoveComplexity.DMG_STATUS;
    case ("Weather"):
      return MoveComplexity.WEATHER;
    case ("Ohko"):
      return MoveComplexity.OHKO;
    case ("Recoil"):
      return MoveComplexity.RECOIL;
    case ("Complex"):
      return MoveComplexity.COMPLEX;
    default:
      return MoveComplexity.ERROR;
    }
  }
}
