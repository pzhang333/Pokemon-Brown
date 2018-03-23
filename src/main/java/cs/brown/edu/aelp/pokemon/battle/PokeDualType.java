package cs.brown.edu.aelp.pokemon.battle;

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
  protected Double getDefensiveEffectiveness(PokeType moveType) {
    return 1.0;
  }

  @Override
  protected Double getOffensiveEffectiveness(PokeType moveType) {

    if (rawTypeA.equals(moveType) || rawTypeB.equals(moveType)) {
      return SAME_ATTACK_TYPE_MULTIPLIER;
    }

    return 1.0;
  }
}
