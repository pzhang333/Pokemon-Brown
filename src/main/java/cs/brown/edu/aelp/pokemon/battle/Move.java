package cs.brown.edu.aelp.pokemon.battle;

import java.util.EnumSet;

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
    NORMAL
  }

  /**
   * MoveFlags.
   */
  public enum MoveFlag {
    MIRROR
  }

  private final String id;

  private final Double number;

  private final Boolean accuracy;

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
  public Move(String id, Double number, Boolean accuracy, Double basePower,
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
  public Boolean getAccuracy() {
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
}
