package cs.brown.edu.aelp.pokemmo.pokemon.moves;

import cs.brown.edu.aelp.pokemmo.pokemon.PokeTypes;

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
    BASIC, BUFF, DEBUFF, COMPLEX, STATUS, DMG_STATUS, WEATHER, OHKO
  }

  /**
   * Move Category.
   */
  public enum MoveCategory {
    PHYSICAL, SPECIAL
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

  public static class Builder {
    private String id;

    private Integer number;

    private Double accuracy;

    private Double basePower;

    private MoveCategory category;

    private String description;

    private String shortDescription;

    private String name;

    private Integer pp;

    private Integer priority;

    private MoveTarget target;

    private PokeTypes type;

    private EnumSet<MoveFlag> flags;

    private MoveComplexity complexity;

    public Builder() {
    }

    public Builder ofId(String id) {
      this.id = id;
      return this;
    }

    public Builder withNumber(Integer number) {
      this.number = number;
      return this;
    }

    public Builder withAccuracy(Double accuracy) {
      this.accuracy = accuracy;
      return this;
    }

    public Builder withPower(Double basePower) {
      this.basePower = basePower;
      return this;
    }

    public Builder ofCategory(MoveCategory category) {
      this.category = category;
      return this;
    }

    public Builder withDescription(String description) {
      this.description = description;
      return this;
    }

    public Builder withShortDescription(String shortDescription) {
      this.shortDescription = shortDescription;
      return this;
    }

    public Builder withName(String name) {
      this.name = name;
      return this;
    }

    public Builder withPP(Integer pp) {
      this.pp = pp;
      return this;
    }

    public Builder withPriority(Integer priority) {
      this.priority = priority;
      return this;
    }

    public Builder withTarget(MoveTarget target) {
      this.target = target;
      return this;
    }

    public Builder ofType(PokeTypes type) {
      this.type = type;
      return this;
    }

    public Builder withFlags(EnumSet<MoveFlag> flags) {
      this.flags = flags;
      return this;
    }

    public Builder withComplexity(MoveComplexity complexity) {
      this.complexity = complexity;
      return this;
    }

    public Move build() {
      Move move = new Move();

      move.id = this.id;
      move.number = this.number;
      move.accuracy = this.accuracy;
      move.basePower = this.basePower;
      move.category = this.category;
      move.description = this.description;
      move.shortDescription = this.shortDescription;
      move.name = this.name;
      move.pp = this.pp;
      move.priority = this.priority;
      move.target = this.target;
      move.type = this.type;
      move.flags = this.flags;
      move.complexity = this.complexity;

      return move;
    }

  }

  private String id;

  private Integer number;

  private Double accuracy;

  private Double basePower;

  private MoveCategory category;

  private String description;

  private String shortDescription;

  private String name;

  private Integer pp;

  private Integer priority;

  private MoveTarget target;

  private PokeTypes type;

  private EnumSet<MoveFlag> flags;

  private MoveComplexity complexity;

  // We want to construct moves only using the builder
  private Move() {
  }

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

  public Move(String id, Integer number, Double accuracy, Double basePower,
              MoveCategory category, String description, String shortDescription,
              String name, Integer pp, Integer priority, MoveTarget target,
              PokeTypes type, EnumSet<MoveFlag> flags, MoveComplexity complexity) {
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
  public Integer getNumber() {
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
  public PokeTypes getType() {
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

  public void setPP(int pp) {
    this.pp = pp;
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
}
