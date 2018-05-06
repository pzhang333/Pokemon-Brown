package cs.brown.edu.aelp.pokemmo.pokemon.moves;

import cs.brown.edu.aelp.pokemmo.battle.Arena;
import cs.brown.edu.aelp.pokemmo.pokemon.PokeTypes;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;
import cs.brown.edu.aelp.pokemmo.pokemon.Status;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MoveResult {
  private Pokemon atkPokemon;
  private Pokemon defPokemon;
  private Arena arena;
  private Move move;
  private MoveOutcome outcome = null;
  private Integer damage = 0;

  private static final double NO_EFFECT = 0;
  private static final double NOT_EFFECTIVE = 0.5;
  private static final double NORMAL = 1.0;
  private static final double EFFECTIVE = 2.0;

  public enum MoveOutcome {
    HIT,
    MISS,
    BLOCKED,
    NON_ATTACK_SUCCESS,
    NON_ATTACK_FAIL,
    NO_EFFECT
  }

  public MoveResult(Pokemon atkPokemon, Pokemon defPokemon, Move move,
      Arena arena) {
    this.atkPokemon = atkPokemon;
    this.defPokemon = defPokemon;
    this.move = move;
    this.arena = arena;
  }

  public void evaluate() {
    // TODO: Add checking for negative status effects

    if (outcome != null) {
      return;
    }

    outcome = MoveOutcome.NO_EFFECT;

    List<Move.Flags> flags = move.getFlags();
    if (flags.contains(Move.Flags.OHKO)) {
      ohkoEval();
    } else if (flags.contains(Move.Flags.DAMAGE)) {
      basicEval();
    }
    if (flags.contains(Move.Flags.SELF)) {
      String stat = move.getAffectedStat();
      Integer stages = move.getStages();
      Double chance = move.getStatChance();
      if (chanceCheck(chance)) {
        affectStat(stat, stages, atkPokemon);
        if (!flags.contains(Move.Flags.DAMAGE)) {
          outcome = MoveOutcome.NON_ATTACK_SUCCESS;
        }
      }
    }
    if (flags.contains(Move.Flags.ENEMY)) {
      String stat = move.getAffectedStat();
      Integer stages = move.getStages();
      Double chance = move.getStatChance();
      if (chanceCheck(chance)) {
        affectStat(stat, stages, defPokemon);
        if (!flags.contains(Move.Flags.DAMAGE)) {
          outcome = MoveOutcome.NON_ATTACK_SUCCESS;
        }
      }
    }

  }

  public boolean accuracyCheck() {
    double effAccuracy = move.getAccuracy() * .01
        * (atkPokemon.getEffectiveAcc() / defPokemon.getEffectiveEva());

    // TODO: Remove print line
    System.out.println("Effective Accuracy: " + effAccuracy);

    return Math.random() <= effAccuracy;
  }

  public boolean ohkoAccCheck() {
    if (atkPokemon.getLevel() < defPokemon.getLevel()) {
      return false;
    } else {
      double effAccuracy = .01
          * (atkPokemon.getLevel() - defPokemon.getLevel() + 30);
      return Math.random() <= effAccuracy;
    }
  }

  public void basicEval() {
    if (accuracyCheck()) {
      double atkDefRatio;
      if (move.getCategory() == Move.MoveCategory.PHYSICAL) {
        atkDefRatio = atkPokemon.getEffectiveAttack()
            / defPokemon.getEffectiveDefense();
      } else {
        atkDefRatio = atkPokemon.getEffectiveSpecialAttack()
            / defPokemon.getEffectiveSpecialDefense();
      }

      double modifier = calcWeatherModifier() * calcCrit() * calcRng()
          * calcSTAB() * calcType() * burnModifier();
      Double dmg = ((((((2 * atkPokemon.getLevel()) / 5) + 2)
          * move.getBasePower() * atkDefRatio) / 50) + 2) * modifier;
      damage = dmg.intValue();
      outcome = MoveOutcome.HIT;
    } else {
      damage = 0;
      outcome = MoveOutcome.MISS;
    }
  }

  public void ohkoEval() {
    if (ohkoAccCheck()) {
      damage = 99999999;
      outcome = MoveOutcome.HIT;
    } else {
      damage = 0;
      outcome = MoveOutcome.MISS;
    }
  }

  public Integer getDamage() {
    return damage;
  }

  public MoveOutcome getOutcome() {
    return outcome;
  }

  public Pokemon getAttackingPokemon() {
    return atkPokemon;
  }

  public Pokemon getDefendingPokemon() {
    return defPokemon;
  }

  public Double calcWeatherModifier() {
    if (arena.getWeather() == Arena.Weather.SUN) {
      if (move.getType() == PokeTypes.FIRE) {
        return 1.5;
      } else if (move.getType() == PokeTypes.WATER) {
        return 0.5;
      }
      return 1.0;
    } else if (arena.getWeather() == Arena.Weather.RAIN) {
      if (move.getType() == PokeTypes.FIRE) {
        return 0.5;
      } else if (move.getType() == PokeTypes.WATER) {
        return 1.5;
      }
      return 1.0;
    }
    return 1.0;
  }

  public Double calcCrit() {
    // Flat 1/16th chance of critical
    int rnd = ThreadLocalRandom.current().nextInt(0, 17);
    return (rnd == 1) ? 2.0 : 1.0;
  }

  public Double calcRng() {
    return ThreadLocalRandom.current().nextDouble(0.85, 1);
  }

  public Double calcSTAB() {
    List<PokeTypes> types = atkPokemon.getType();
    if (types.contains(move.getType())) {
      return 1.5;
    } else {
      return 1.0;
    }
  }

  public Double calcType() {
    double base = 1.0;
    for (PokeTypes type : defPokemon.getType()) {
      base *= typeModifier(type);
    }
    return base;
  }

  public void setOutcome(MoveOutcome outcome) {
    this.outcome = outcome;
  }

  public Double typeModifier(PokeTypes targetType) {
    switch (move.getType()) {
    case NORMAL:
      switch (targetType) {
      case ROCK:
        return 0.5;
      case GHOST:
        return 0.0;
      case STEEL:
        return 0.5;
      default:
        return 1.0;
      }
    case FIGHTING:
      switch (targetType) {
      case NORMAL:
        return 2.0;
      case FLYING:
        return 0.5;
      case POISON:
        return 0.5;
      case ROCK:
        return 2.0;
      case BUG:
        return 0.5;
      case GHOST:
        return 0.0;
      case STEEL:
        return 2.0;
      case PSYCHIC:
        return 0.5;
      case ICE:
        return 2.0;
      case DARK:
        return 2.0;
      default:
        return 1.0;
      }
    case FLYING:
      switch (targetType) {
      case FIGHTING:
        return 2.0;
      case ROCK:
        return 0.5;
      case BUG:
        return 2.0;
      case STEEL:
        return 0.5;
      case GRASS:
        return 2.0;
      case ELECTRIC:
        return 0.5;
      default:
        return 1.0;
      }
    case POISON:
      switch (targetType) {
      case POISON:
        return 0.5;
      case GROUND:
        return 0.5;
      case ROCK:
        return 0.5;
      case GHOST:
        return 0.5;
      case STEEL:
        return 0.0;
      case GRASS:
        return 2.0;
      case ELECTRIC:
        return 0.5;
      default:
        return 1.0;
      }
    case GROUND:
      switch (targetType) {
      case FLYING:
        return 0.0;
      case POISON:
        return 2.0;
      case ROCK:
        return 2.0;
      case BUG:
        return 0.5;
      case STEEL:
        return 2.0;
      case FIRE:
        return 2.0;
      case GRASS:
        return 0.5;
      case ELECTRIC:
        return 2.0;
      default:
        return 1.0;
      }
    case ROCK:
      switch (targetType) {
      case FIGHTING:
        return 0.5;
      case FLYING:
        return 2.0;
      case GROUND:
        return 0.5;
      case BUG:
        return 2.0;
      case STEEL:
        return 0.5;
      case FIRE:
        return 2.0;
      case ICE:
        return 2.0;
      default:
        return 1.0;
      }
    case BUG:
      switch (targetType) {
      case FIGHTING:
        return 0.5;
      case FLYING:
        return 0.5;
      case POISON:
        return 0.5;
      case GHOST:
        return 0.5;
      case STEEL:
        return 0.5;
      case FIRE:
        return 0.5;
      case GRASS:
        return 2.0;
      case PSYCHIC:
        return 2.0;
      case DARK:
        return 2.0;
      default:
        return 1.0;
      }
    case GHOST:
      switch (targetType) {
      case NORMAL:
        return 0.0;
      case GHOST:
        return 2.0;
      case PSYCHIC:
        return 2.0;
      case DARK:
        return 0.5;
      default:
        return 1.0;
      }
    case STEEL:
      switch (targetType) {
      case ROCK:
        return 2.0;
      case STEEL:
        return 0.5;
      case FIRE:
        return 0.5;
      case WATER:
        return 0.5;
      case ELECTRIC:
        return 0.5;
      case ICE:
        return 2.0;
      default:
        return 1.0;
      }
    case FIRE:
      switch (targetType) {
      case ROCK:
        return 0.5;
      case BUG:
        return 2.0;
      case STEEL:
        return 2.0;
      case FIRE:
        return 0.5;
      case WATER:
        return 0.5;
      case GRASS:
        return 2.0;
      case ICE:
        return 2.0;
      case DRAGON:
        return 0.5;
      default:
        return 1.0;
      }
    case WATER:
      switch (targetType) {
      case GROUND:
        return 2.0;
      case ROCK:
        return 2.0;
      case FIRE:
        return 2.0;
      case WATER:
        return 0.5;
      case GRASS:
        return 0.5;
      case DRAGON:
        return 0.5;
      default:
        return 1.0;
      }
    case GRASS:
      switch (targetType) {
      case FLYING:
        return 0.5;
      case POISON:
        return 0.5;
      case GROUND:
        return 2.0;
      case ROCK:
        return 2.0;
      case BUG:
        return 0.5;
      case STEEL:
        return 0.5;
      case FIGHTING:
        return 0.5;
      case WATER:
        return 2.0;
      case GRASS:
        return 0.5;
      case DRAGON:
        return 0.5;
      default:
        return 1.0;
      }
    case ELECTRIC:
      switch (targetType) {
      case FLYING:
        return 2.0;
      case GROUND:
        return 0.0;
      case WATER:
        return 2.0;
      case GRASS:
        return 0.5;
      case ELECTRIC:
        return 0.5;
      case DRAGON:
        return 0.5;
      default:
        return 1.0;
      }
    case PSYCHIC:
      switch (targetType) {
      case FIGHTING:
        return 2.0;
      case POISON:
        return 2.0;
      case STEEL:
        return 0.5;
      case PSYCHIC:
        return 0.5;
      case DARK:
        return 0.0;
      default:
        return 1.0;
      }
    case ICE:
      switch (targetType) {
      case FLYING:
        return 2.0;
      case GROUND:
        return 2.0;
      case STEEL:
        return 0.5;
      case FIRE:
        return 0.5;
      case WATER:
        return 0.5;
      case GRASS:
        return 2.0;
      case ICE:
        return 0.5;
      case DRAGON:
        return 2.0;
      default:
        return 1.0;
      }
    case DRAGON:
      switch (targetType) {
      case STEEL:
        return 0.5;
      case DRAGON:
        return 2.0;
      default:
        return 1.0;
      }
    case DARK:
      switch (targetType) {
      case FIGHTING:
        return 0.5;
      case GHOST:
        return 2.0;
      case PSYCHIC:
        return 2.0;
      case DARK:
        return 0.5;
      default:
        return 1.0;
      }
    default:
      return 1.0;
    }
  }

  public Double burnModifier() {
    return (atkPokemon.getStatus() == Status.BURN
        && move.getCategory() == Move.MoveCategory.PHYSICAL) ? 0.5 : 1.0;
  }

  public Boolean chanceCheck(Double chance) {
    Double randomNum = ThreadLocalRandom.current().nextDouble(1.0);
    return randomNum <= chance;
  }

  public static void affectStat(String stat, Integer stages, Pokemon target) {
    switch (stat) {
    case "atk":
      target.modifyAttackStage(stages);
      break;
    case "specAtk":
      target.modifySpecialAttackStage(stages);
      break;
    case "def":
      target.modifyDefenseStage(stages);
      break;
    case "specDef":
      target.modifySpecialDefenseStage(stages);
      break;
    case "spd":
      target.modifySpeedStage(stages);
      break;
    case "acc":
      target.modifyAccuracyStage(stages);
      break;
    case "eva":
      target.modifyEvasionStage(stages);
      break;
    }
  }
}
