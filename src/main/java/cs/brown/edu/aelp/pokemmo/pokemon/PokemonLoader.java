package cs.brown.edu.aelp.pokemmo.pokemon;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cs.brown.edu.aelp.pokemmo.pokemon.moves.Move;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.MoveLoader;

public final class PokemonLoader {
  private static final String FILEPATH = "data/pokemon/pokemon.json";

  private PokemonLoader(String filePath) {
  }

  public static Pokemon load(String species, int exp) {
    return load(species, exp, -1);
  }

  public static Pokemon load(String species, int exp, int id) {
    try {
      JsonElement jsonElement = new JsonParser()
          .parse(new FileReader(FILEPATH));
      JsonObject jsonObject = jsonElement.getAsJsonObject();
      JsonObject pokemon = jsonObject.getAsJsonObject(species);
      JsonObject baseStats = pokemon.getAsJsonObject("baseStats");
      JsonArray types = pokemon.getAsJsonArray("types");
      JsonArray moves = pokemon.getAsJsonArray("moves");

      Integer hp = baseStats.getAsJsonPrimitive("hp").getAsInt();
      Integer attack = baseStats.getAsJsonPrimitive("atk").getAsInt();
      Integer defense = baseStats.getAsJsonPrimitive("def").getAsInt();
      Integer spAtk = baseStats.getAsJsonPrimitive("spa").getAsInt();
      Integer spDef = baseStats.getAsJsonPrimitive("spd").getAsInt();
      Integer speed = baseStats.getAsJsonPrimitive("spe").getAsInt();

      Integer xOffset = pokemon.get("xOffset").getAsInt();
      Integer yOffset = pokemon.get("yOffset").getAsInt();
      Integer fps = pokemon.get("fps").getAsInt();

      List<PokeTypes> typesList = new ArrayList<>();

      for (JsonElement type : types) {
        typesList.add(stringToType(type.getAsString()));
      }

      List<Move> moveList = new ArrayList<>();
      for (JsonElement move : moves) {
        moveList.add(MoveLoader.getMoveById(move.getAsInt()));
      }

      int gender = -1;

      if (Math.random() > 0.5) {
        gender = 0;
      } else {
        gender = 1;
      }

      Pokemon target = new Pokemon.Builder(id).ofSpecies(species)
          .withNickName(species).withBaseHp(hp).withAtk(attack).withDef(defense)
          .withSpecAtk(spAtk).withSpecDef(spDef).withSpd(speed)
          .withTypes(typesList).withGender(gender).withExp(exp)
          .withMoves(moveList).withXOffset(xOffset).withYOffset(yOffset)
          .withFPS(fps).build();
      return target;

    } catch (FileNotFoundException e) {
      // Should not occur since path is hard-coded in
      e.printStackTrace();
    }
    return null;
  }

  public static String getEvolutionName(String species) {
    try {
      JsonElement jsonElement = new JsonParser()
          .parse(new FileReader(FILEPATH));
      JsonObject jsonObject = jsonElement.getAsJsonObject();
      JsonObject pokemon = jsonObject.getAsJsonObject(species);
      String evoName = pokemon.getAsJsonObject("evo").getAsString();
      return evoName;
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static PokeTypes stringToType(String type) {
    switch (type) {
      case "Normal":
        return PokeTypes.NORMAL;
      case "Fire":
        return PokeTypes.FIRE;
      case "Water":
        return PokeTypes.WATER;
      case "Electric":
        return PokeTypes.ELECTRIC;
      case "Grass":
        return PokeTypes.GRASS;
      case "Ice":
        return PokeTypes.ICE;
      case "Fighting":
        return PokeTypes.FIGHTING;
      case "Poison":
        return PokeTypes.POISON;
      case "Ground":
        return PokeTypes.GROUND;
      case "Flying":
        return PokeTypes.FLYING;
      case "Psychic":
        return PokeTypes.PSYCHIC;
      case "Bug":
        return PokeTypes.BUG;
      case "Rock":
        return PokeTypes.ROCK;
      case "Ghost":
        return PokeTypes.GHOST;
      case "Dragon":
        return PokeTypes.DRAGON;
      case "Dark":
        return PokeTypes.DARK;
      case "Steel":
        return PokeTypes.STEEL;
      default:
        return PokeTypes.NORMAL;
    }
  }

}
