package cs.brown.edu.aelp.pokemon.battle;

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

}
