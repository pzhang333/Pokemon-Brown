
package cs.brown.edu.aelp.pokemmo.pokemon;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomWildPokemon {
  private static List<String> normalPokemon = new ImmutableList
      .Builder<String>()
      .add("bulbasaur")
      .add("ivysaur")
      .add("charmander")
      .add("charmeleon")
      .add("squirtle")
      .add("wartortle")
      .add("pichu")
      .add("pikachu")
      .build();

  private static List<String> specialPokemon = new ImmutableList
      .Builder<String>()
      .add("venusaur")
      .add("charizard")
      .add("blastoise")
      .add("raichu")
      .build();

  private static List<String> legendaryPokemon = new ImmutableList
      .Builder<String>()
      .add("arceus")
      .add("giratina")
      .add("groudon")
      .build();

  public static Pokemon generateWildPokemon(int averageLevel){
    int random = ThreadLocalRandom.current().nextInt(0, 101);
      if (random > 95){
        int level = (averageLevel < 65) ? 65 : averageLevel;
        return PokemonLoader.load(randomElementFromList(legendaryPokemon), Pokemon.calcXpByLevel(level));
      } else if (random > 65) {
        int level = (averageLevel < 35) ? 35 : averageLevel;
        return PokemonLoader.load(randomElementFromList(specialPokemon), Pokemon.calcXpByLevel(level));
      } else {
        return PokemonLoader.load(randomElementFromList(normalPokemon), Pokemon.calcXpByLevel(averageLevel));
      }
  }

  public static <T> T randomElementFromList(List<T> list) {
    int random = ThreadLocalRandom.current().nextInt(0, list.size());
    return list.get(random);
  }
}
