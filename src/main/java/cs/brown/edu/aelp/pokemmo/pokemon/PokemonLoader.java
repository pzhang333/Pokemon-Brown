package cs.brown.edu.aelp.pokemmo.pokemon;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public final class PokemonLoader {
  private static final String FILEPATH = "data/pokemon/pokemon.json";


  private PokemonLoader(String filePath) {
  }

  public static Pokemon load(String species) {
    try {
      JsonElement jsonElement = new JsonParser()
          .parse(new FileReader(FILEPATH));
      JsonObject jsonObject = jsonElement.getAsJsonObject();
      JsonObject pokemon = jsonObject.getAsJsonObject(species);
      JsonObject baseStats = pokemon.getAsJsonObject("baseStats");
      JsonArray types = pokemon.getAsJsonArray("types");

      Integer hp = baseStats.getAsJsonObject("hp").getAsInt();
      Integer attack = baseStats.getAsJsonObject("atk").getAsInt();
      Integer defense = baseStats.getAsJsonObject("def").getAsInt();
      Integer spAtk = baseStats.getAsJsonObject("spa").getAsInt();
      Integer spDef = baseStats.getAsJsonObject("spd").getAsInt();
      Integer speed = baseStats.getAsJsonObject("spe").getAsInt();

      List<PokeTypes> typesList = new ArrayList<>();

      for (JsonElement type : types) {
        typesList.add(stringToType(type.getAsString()));
      }

      if (typesList.size() == 2) {
        Pokemon target = new Pokemon.Builder(0)
            .ofSpecies("species")
            .withBaseHp(hp)
            .withAtk(attack)
            .withDef(defense)
            .withSpecAtk(spAtk)
            .withSpecDef(spDef)
            .withSpd(speed)
            .withType(typesList.get(0))
            .withType(typesList.get(1))
            .build();
        return target;
      } else {
        Pokemon target = new Pokemon.Builder(0)
            .ofSpecies("species")
            .withBaseHp(hp)
            .withAtk(attack)
            .withDef(defense)
            .withSpecAtk(spAtk)
            .withSpecDef(spDef)
            .withSpd(speed)
            .withType(typesList.get(0))
            .build();
        return target;
      }

    } catch (FileNotFoundException e) {
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
      String evoName =  pokemon.getAsJsonObject("evo").getAsString();
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
