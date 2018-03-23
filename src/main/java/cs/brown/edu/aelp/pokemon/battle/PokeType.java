package cs.brown.edu.aelp.pokemon.battle;

import java.util.HashMap;
import java.util.Map;

/**
 * Class representing PokemonType.
 */
public class PokeType {

  protected static final Double SAME_ATTACK_TYPE_MULTIPLIER = 1.5;

  protected static final Double NO_EFFECT_MULTIPLIER = 0.0;

  protected static final Double NOT_EFFECTIVE_MULTIPLIER = 0.5;

  protected static final Double NORMAL_MULTIPLIER = 1.0;

  protected static final Double SUPER_EFFECTIVE_MULTIPLIER = 2.0;

  private static Map<PokeRawType, PokeType> typeMap = new HashMap<>();

  /**
   * Pokemon Types.
   */
  public enum PokeRawType {
    NORMAL, FIRE, WATER, ELECTRIC, GRASS, ICE, FIGHTING, POISION, GROUND,
    FLYING, PSYCHIC, BUG, ROCK, GHOST, DRAGON, DARK, STEEL;
  }

  private PokeRawType pokeRawType;

  protected PokeType() {

  }

  protected PokeType(PokeRawType pokeRawType) {
    this.pokeRawType = pokeRawType;
  }

  protected Double getDefensiveEffectiveness(PokeType moveType) {
    return 1.0;
  }

  protected Double getOffensiveEffectiveness(PokeType moveType) {
    /* The same-attack-type bonus (1.5x) */
    if (pokeRawType.equals(moveType)) {
      return SAME_ATTACK_TYPE_MULTIPLIER;
    }

    return 1.0;
  }

  /**
   * Get a PokeType from a PokeRawType.
   *
   * @param rawType
   *          The PokeRawType to get the PokeType for.
   * @return The PokeType associated with the PokeRaw Type.
   */
  public static PokeType getType(PokeRawType rawType) {
    if (!typeMap.containsKey(rawType)) {
      PokeType type = new PokeType(rawType);
      typeMap.put(rawType, type);

      return type;
    }

    return typeMap.get(rawType);
  }
}
