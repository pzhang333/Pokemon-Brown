package cs.brown.edu.aelp.pokemmo.battle;

import cs.brown.edu.aelp.pokemmo.pokemon.PokeType;
import cs.brown.edu.aelp.pokemmo.pokemon.PokeType.PokeRawType;

/**
 * Arena class.
 */
public class Arena {

  /**
   * Weather enum.
   */
  public enum Weather {
    NORMAL, SUN, RAIN, HAIL, SAND;
  };

  private Weather weather = Weather.NORMAL;

  /**
   * Construct a new Arena.
   */
  public Arena() {

  }

  /**
   * Get the current weather in the arena.
   *
   * @return The current weather in the arena.
   */
  public Weather getWeather() {
    return weather;
  }

  /**
   * Set the current weather in the arena.
   *
   * @param weather
   *          The weather in the arena.
   */
  public void setWeather(Weather weather) {
    this.weather = weather;
  }

  /*
  public double getWeatherModifier(PokeType move) {
    if (weather == Weather.SUN) {
      if (move.typeMatches(PokeType.getType(PokeRawType.FIRE))) {
        return 1.5;
      } else if (move.typeMatches(PokeType.getType(PokeRawType.WATER))) {
        return 0.5;
      }
    } else if (weather == Weather.RAIN) {
      if (move.typeMatches(PokeType.getType(PokeRawType.FIRE))) {
        return 0.5;
      } else if (move.typeMatches(PokeType.getType(PokeRawType.WATER))) {
        return 1.5;
      }
    }

    return 1;
  }
  */
}
