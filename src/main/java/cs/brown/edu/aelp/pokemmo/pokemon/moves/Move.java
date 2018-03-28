package cs.brown.edu.aelp.pokemmo.pokemon.moves;

import java.util.EnumSet;
import java.util.concurrent.ThreadLocalRandom;

import cs.brown.edu.aelp.pokemmo.battle.events.AttackEvent;
import cs.brown.edu.aelp.pokemmo.pokemon.PokeType;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.MoveResult.ModifierType;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.MoveResult.MoveOutcome;

/**
 * Pokemon Move class.
 */
public class Move {

  /**
   * Move Complexity Type. Simple moves are calculated normally. Complex moves
   * require Java implemented Handlers.
   */
  public enum MoveComplexity {
    SIMPLE, COMPLEX
  }

  /**
   * Move Category.
   */
  public enum MoveCategory {
    PHYSICAL, SPECIAL, STATUS
  }

  /**
   * Move Target.
   */
  public enum MoveTarget {
    NORMAL, MULTI
  }

  /**
   * MoveFlags.
   */
  public enum MoveFlag {
    MIRROR
  }

  private final String id;

  private final Double number;

  private final Double accuracy;

  private final Double basePower;

  private final MoveCategory category;

  private final String description;

  private final String shortDescription;

  private final String name;

  private final Integer pp;

  private final Integer priority;

  private final MoveTarget target;

  private final PokeType type;

  private final EnumSet<MoveFlag> flags;

  private final MoveComplexity complexity;

  /**
   * Construct a Move object. This should be done dynamically.
   *
   * @param id
   *          The move's id.
   * @param number
   *          The move's number.
   * @param accuracy
   *          The move's base accuracy.
   * @param basePower
   *          The move's base power.
   * @param category
   *          The move's category.
   * @param description
   *          The move's description.
   * @param shortDescription
   *          The move's short description.
   * @param name
   *          The move's name.
   * @param pp
   *          The move's base PP.
   * @param priority
   *          The move's priority.
   * @param target
   *          The move's target (NORMAL usually, can sometimes be all on battle
   *          field).
   * @param type
   *          The move's type.
   * @param flags
   *          The move's flag set.
   * @param complexity
   *          Whether or not this move expects a Java Handler.
   */
  public Move(String id, Double number, Double accuracy, Double basePower,
      MoveCategory category, String description, String shortDescription,
      String name, Integer pp, Integer priority, MoveTarget target,
      PokeType type, EnumSet<MoveFlag> flags, MoveComplexity complexity) {
    super();
    this.id = id;
    this.number = number;
    this.accuracy = accuracy;
    this.basePower = basePower;
    this.category = category;
    this.description = description;
    this.shortDescription = shortDescription;
    this.name = name;
    this.pp = pp;
    this.priority = priority;
    this.target = target;
    this.type = type;
    this.flags = flags;
    this.complexity = complexity;
  }

  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * @return the number
   */
  public Double getNumber() {
    return number;
  }

  /**
   * @return the accuracy
   */
  public Double getAccuracy() {
    return accuracy;
  }

  /**
   * @return the basePower
   */
  public Double getBasePower() {
    return basePower;
  }

  /**
   * @return the category
   */
  public MoveCategory getCategory() {
    return category;
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @return the shortDescription
   */
  public String getShortDescription() {
    return shortDescription;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @return the pp
   */
  public Integer getPp() {
    return pp;
  }

  /**
   * @return the priority
   */
  public Integer getPriority() {
    return priority;
  }

  /**
   * @return the target
   */
  public MoveTarget getTarget() {
    return target;
  }

  /**
   * @return the type
   */
  public PokeType getType() {
    return type;
  }

  /**
   * @return the flags
   */
  public EnumSet<MoveFlag> getFlags() {
    return flags;
  }

  /**
   * @return the complexity
   */
  public MoveComplexity getComplexity() {
    return complexity;
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
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
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
    Move other = (Move) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!name.equals(other.name)) {
      return false;
    }
    if (type == null) {
      if (other.type != null) {
        return false;
      }
    } else if (!type.equals(other.type)) {
      return false;
    }
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Move [name=" + name + "]";
  }

  public void applyStandardModifiers(AttackEvent evt, MoveResult mr) {

    applyEffectivenessModifiers(evt, mr);

    // Target modifier
    mr.setModifier(ModifierType.TARGET,
        (getTarget().equals(MoveTarget.NORMAL)) ? 1 : .75);

    // Technically this is not inclusive on 1
    mr.setModifier(ModifierType.RANDOM,
        ThreadLocalRandom.current().nextDouble(.85, 1));
  }

  public void applyEffectivenessModifiers(AttackEvent evt, MoveResult mr) {

    mr.setModifier(ModifierType.STAB, evt.getAttackingPokemon().getType()
        .getOffensiveEffectiveness(getType()));

    mr.setModifier(ModifierType.TYPE, evt.getDefendingPokemon().getType()
        .getDefensiveEffectiveness(getType()));

    mr.setModifier(ModifierType.WEATHER,
        evt.getBattle().getArena().getWeatherModifier(getType()));
  }

  public boolean isCrit() {
    return false;
  }

  public double getCritModifier() {
    return 2;
  }

  public boolean accuracyCheck(AttackEvent evt) {

    // If accuracy is -1 don't bother to calculate as the move ALWAYS hits.
    if (getAccuracy() == -1) {
      return true;
    }

    double effAccuracy = getAccuracy()
        * (evt.getAttackingPokemon().getAccuracy()
            / evt.getAttackingPokemon().getEvasion());

    return (Math.random() <= effAccuracy);
  }

  public MoveResult getResult(AttackEvent evt) {
    MoveResult mr = new MoveResult(evt.getAttackingPokemon(),
        evt.getDefendingPokemon());

    // This does not account for stat stages...

    mr.setOutcome(MoveOutcome.HIT);

    boolean isCrit = isCrit();

    double critModifier = isCrit ? getCritModifier() : 1;

    double atkDefRatio = 0;

    if (getCategory().equals(MoveCategory.PHYSICAL)) {
      atkDefRatio = evt.getAttackingPokemon().getEffectiveAttack()
          / evt.getDefendingPokemon().getEffectiveDefense();

    } else if (getCategory().equals(MoveCategory.SPECIAL)) {
      atkDefRatio = evt.getAttackingPokemon().getEffectiveSpecialAttack()
          / evt.getDefendingPokemon().getEffectiveSpecialDefense();
    }

    double baseDamage = ((((((2 * evt.getAttackingPokemon().getLevel()
        * critModifier) / 5) + 2) * getBasePower() * atkDefRatio) / 50) + 2);

    mr.setBaseDamage(baseDamage);

    // Apply general crit modifier
    mr.setModifier(ModifierType.CRIT, critModifier);

    applyStandardModifiers(evt, mr);

    return mr;
  }

}
