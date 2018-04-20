package cs.brown.edu.aelp.pokemmo.pokemon;

import java.util.HashMap;
import java.util.Map;

/**
 * Class representing PokemonType.
 */
public class PokeType {

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

  /**
   * Check the effectiveness of a PokeType when used AGAINST the current
   * PokeType.
   *
   * @param moveType
   *          The type of the move to check the effectiveness of.
   * @return The effectiveness multiplier.
   */
  public Double getDefensiveEffectiveness(PokeType moveType) {
    return 1.0;
  }

  /**
   * Check the effectiveness of a PokeType when used BY the current PokeType.
   *
   * @param moveType
   *          The type of the move to check the effectiveness of.
   * @return The effectiveness multiplier.
   */
  public Double getOffensiveEffectiveness(PokeType moveType) {
    /* The same-attack-type bonus (1.5x) */
    if (this.equals(moveType)) {
      return 1.5;
    }

    return 1.0;
  }

  public boolean typeMatches(PokeType type) {
    return type.equals(type);
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

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "PokeType(" + pokeRawType + ")";
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + ((pokeRawType == null) ? 0 : pokeRawType.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    PokeType other = (PokeType) obj;
    if (pokeRawType != other.pokeRawType) {
      return false;
    }
    return true;
  }

}
