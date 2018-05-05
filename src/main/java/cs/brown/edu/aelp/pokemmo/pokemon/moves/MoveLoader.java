package cs.brown.edu.aelp.pokemmo.pokemon.moves;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import cs.brown.edu.aelp.pokemmo.pokemon.PokeTypes;
import cs.brown.edu.aelp.pokemmo.pokemon.Status;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.Move.Builder;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.Move.MoveCategory;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.complex.PoisonMove;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.complex.Wish;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MoveLoader {

  private static final String FILEPATH = "data/pokemon/moves.json";

  private static final Map<Integer, Move> overrides = new HashMap<>();

  public static void setupOverrides() {
    addOverride(new Wish());
    addOverride(new PoisonMove(77)); // poison powder
  }

  public static void addOverride(Move m) {
    overrides.put(m.getId(), m);
  }

  public static Move getMoveById(Integer id) {
    try {
      JsonElement jsonElement = new JsonParser()
          .parse(new FileReader(FILEPATH));
      JsonObject jsonObject = jsonElement.getAsJsonObject();
      JsonObject move = jsonObject.getAsJsonObject(Integer.toString(id));

      Integer accuracy = move.get("accuracy").getAsInt();
      Integer basePower = move.get("basePower").getAsInt();
      String description = move.get("desc").getAsString();
      String shortDesc = move.get("shortDesc").getAsString();
      String name = move.get("name").getAsString();
      Integer pp = move.get("pp").getAsInt();
      Integer priority = move.get("priority").getAsInt();
      MoveCategory category = MoveCategory
          .valueOf(move.get("category").getAsString().toUpperCase());
      List<Move.Flags> flags = new ArrayList<>();
      JsonArray flagArray = move.getAsJsonArray("flags");
      for (JsonElement flag : flagArray) {
        flags.add(Move.Flags.valueOf(flag.getAsString()));
      }
      PokeTypes type = PokeTypes
          .valueOf(move.get("type").getAsString().toUpperCase());

      Builder builder = new Move.Builder(id);
      builder.withAccuracy(accuracy).withPower(basePower)
          .withDescription(description).withShortDescription(shortDesc)
          .withName(name).withPP(pp).withCurrPP(pp).withPriority(priority)
          .ofCategory(category).ofCategory(category).ofType(type)
          .withFlags(flags);

      if (flags.contains(Move.Flags.RECOIL)) {
        Double recoil = getDecimal(move.getAsJsonArray("recoil"));
        builder.withRecoil(recoil);
      }
      if (flags.contains(Move.Flags.ENEMY) || flags.contains(Move.Flags.SELF)) {
        String stat = move.get("stat").getAsString();
        Double statChance = getDecimal(move.getAsJsonArray("statChance"));
        Integer stages = move.get("stages").getAsInt();
        builder.affectsStat(stat).withStatChance(statChance).withStages(stages);
      }
      if (flags.contains(Move.Flags.STATUS)) {
        Status status = Status.valueOf(move.get("status").getAsString());
        Double statusChance = getDecimal(move.getAsJsonArray("statusChance"));
        builder.giveStatus(status).withStatusChance(statusChance);
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

  public static Double getDecimal(JsonArray fraction) {
    Double numerator = fraction.get(0).getAsDouble();
    Double denominator = fraction.get(1).getAsDouble();
    return numerator / denominator;
  }
}
