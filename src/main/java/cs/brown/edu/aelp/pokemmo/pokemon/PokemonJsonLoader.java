package cs.brown.edu.aelp.pokemmo.pokemon;

import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

// TODO: Finish implementing
public class PokemonJsonLoader {

  // Reads json of the format of the data in pokemon-showdown-data/pokedex.js
  private String filePath;

  public PokemonJsonLoader(String filePath) {
    this.filePath = filePath;
  }

  public Pokemon loadSinglePokemon(String species) {
    try {
      JsonElement jsonElement = new JsonParser()
          .parse(new FileReader(filePath));
      JsonObject jsonObject = jsonElement.getAsJsonObject();

      JsonObject pokemon = jsonObject.getAsJsonObject(species);
      JsonObject baseStats = pokemon.getAsJsonObject("baseStats");
      JsonArray types = pokemon.getAsJsonArray("types");

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }
}
