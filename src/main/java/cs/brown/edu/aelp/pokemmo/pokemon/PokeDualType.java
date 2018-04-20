package cs.brown.edu.aelp.pokemmo.pokemon;

/**
 * Class representing PokemonType.
 */
public class PokeDualType extends PokeType {

  private final PokeRawType rawTypeA;

  private final PokeRawType rawTypeB;

  protected PokeDualType(PokeRawType rawTypeA, PokeRawType rawTypeB) {
    super();

    if (rawTypeA.equals(rawTypeB)) {
      throw new IllegalArgumentException(
          "PokeDualType cannot contain two of the same type!");
    }

    this.rawTypeA = rawTypeA;
    this.rawTypeB = rawTypeB;
  }

  @Override
  public Double getDefensiveEffectiveness(PokeType moveType) {
    return 1.0;
  }

  @Override
  public boolean typeMatches(PokeType type) {
    return PokeType.getType(rawTypeA).equals(type)
        || PokeType.getType(rawTypeB).equals(type);
  }

  @Override
  public Double getOffensiveEffectiveness(PokeType moveType) {
    if (typeMatches(moveType)) {
      return 1.5;
    }

    return 1.0;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "PokeDualType(" + rawTypeA + ", " + rawTypeB + ")";
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((rawTypeA == null) ? 0 : rawTypeA.hashCode());
    result = prime * result + ((rawTypeB == null) ? 0 : rawTypeB.hashCode());
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
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    PokeDualType other = (PokeDualType) obj;
    if (rawTypeA != other.rawTypeA) {
      return false;
    }
    if (rawTypeB != other.rawTypeB) {
      return false;
    }
    return true;
  }

}
