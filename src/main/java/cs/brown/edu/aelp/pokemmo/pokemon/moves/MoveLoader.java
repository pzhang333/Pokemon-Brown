package cs.brown.edu.aelp.pokemmo.pokemon.moves;

import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cs.brown.edu.aelp.pokemmo.pokemon.PokeTypes;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;
import cs.brown.edu.aelp.pokemmo.pokemon.PokemonLoader;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.Move.MoveCategory;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.Move.MoveComplexity;

public final class MoveLoader {

  private static final String FILEPATH = "data/pokemon/moves.json";

  private MoveLoader() {
  }

  public static Move getMoveById(Integer id) {
    try {
      JsonElement jsonElement = new JsonParser()
          .parse(new FileReader(FILEPATH));
      JsonObject jsonObject = jsonElement.getAsJsonObject();
      JsonObject move = jsonObject.getAsJsonObject(Integer.toString(id));
      Integer accuracy = move.get("accuracy").getAsInt();
      Integer basePower = move.get("baserPower").getAsInt();
      String description = move.get("desc").getAsString();
      String shortDesc = move.get("shortDesc").getAsString();
      String name = move.get("name").getAsString();
      Integer pp = move.get("pp").getAsInt();
      Integer priority = move.get("priorty").getAsInt();
      PokeTypes type = PokemonLoader
          .stringToType(move.get("type").getAsString());
      MoveComplexity complexity = stringToMoveComplexity(move.get("complexity").getAsString());
      MoveCategory category = stringToMoveCategory(move.get("category").getAsString());
      
      Move pokemonMove = new Move.Builder(id)
          .withAccuracy(accuracy)
          .withPower(basePower)
          .withDescription(description)
          .withShortDescription(shortDesc)
          .withName(name)
          .withPP(pp)
          .withPriority(priority)
          .ofType(type)
          .withComplexity(complexity)
          .ofCategory(category)
          .build();
      
      return pokemonMove;
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
      case("Buff"):
        return MoveComplexity.BUFF;
      case ("Debuff"):
        return MoveComplexity.DEBUFF;
      case ("Dmg_Status"):
        return MoveComplexity.DMG_STATUS;
      case ("Weather"):
        return MoveComplexity.WEATHER;
      case ("Ohko"):
        return MoveComplexity.OHKO;
      default:
        return MoveComplexity.ERROR;
    }
  }
}
