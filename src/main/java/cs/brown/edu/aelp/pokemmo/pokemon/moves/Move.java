package cs.brown.edu.aelp.pokemmo.pokemon.moves;

import cs.brown.edu.aelp.pokemmo.pokemon.PokeTypes;

/**
 * Pokemon Move class.
 */
public class Move {

  /**
   * Move Complexity Type. Simple moves are calculated normally. Complex moves
   * require Java implemented Handlers.
   */
  public enum MoveComplexity {
    BASIC, BUFF, DEBUFF, COMPLEX, STATUS, DMG_STATUS, WEATHER, OHKO, ERROR
  }

  /**
   * Move Category.
   */
  public enum MoveCategory {
    PHYSICAL, SPECIAL, NONE
  }

  public static class Builder {
    private int id;

    private Integer accuracy;

    private Integer basePower;

    private MoveCategory category;

    private String description;

    private String shortDescription;

    private String name;

    private Integer pp;

    private Integer priority;

    private PokeTypes type;

    private MoveComplexity complexity;

    public Builder(int id) {
      this.id = id;
    }

    public Builder withAccuracy(Integer accuracy) {
      this.accuracy = accuracy;
      return this;
    }

    public Builder withPower(Integer basePower) {
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

    public Builder ofType(PokeTypes type) {
      this.type = type;
      return this;
    }

    public Builder withComplexity(MoveComplexity complexity) {
      this.complexity = complexity;
      return this;
    }

    public Move build() {
      Move move = new Move(this.id);
      move.accuracy = this.accuracy;
      move.basePower = this.basePower;
      move.category = this.category;
      move.description = this.description;
      move.shortDescription = this.shortDescription;
      move.name = this.name;
      move.pp = this.pp;
      move.priority = this.priority;
      move.type = this.type;
      move.complexity = this.complexity;

      return move;
    }

  }

  private int id;

  private Integer accuracy;

  private Integer basePower;

  private MoveCategory category;

  private String description;

  private String shortDescription;

  private String name;

  private Integer pp;

  private Integer priority;

  private PokeTypes type;

  private MoveComplexity complexity;

  // We want to construct moves only using the builder
  private Move(int id) {
    this.id = id;
  }

  /**
   * @return the accuracy
   */

  public Integer getAccuracy() {
    return accuracy;
  }

  /**
   * @return the basePower
   */
  public Integer getBasePower() {
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
   * @return the type
   */
  public PokeTypes getType() {
    return type;
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

  public int getId() {
    return this.id;
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + id;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Move other = (Move) obj;
    if (id != other.id)
      return false;
    return true;
  }

}
