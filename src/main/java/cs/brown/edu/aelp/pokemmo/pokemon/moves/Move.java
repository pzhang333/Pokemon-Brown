package cs.brown.edu.aelp.pokemmo.pokemon.moves;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import cs.brown.edu.aelp.pokemmo.battle.events.AttackEvent;
import cs.brown.edu.aelp.pokemmo.pokemon.PokeTypes;
import cs.brown.edu.aelp.pokemmo.pokemon.Status;

/**
 * Pokemon Move class.
 */
public class Move {

  public enum Flags {
    DAMAGE, OHKO, RECOIL, STATUS, SELF, ENEMY, COMPLEX
  }

  /**
   * Move Category.
   */
  public enum MoveCategory {
    PHYSICAL, SPECIAL, NONE
  }

  public static class Builder {
    private Integer id;

    private Integer accuracy;

    private Integer basePower;

    private String description;

    private String shortDescription;

    private String name;

    private Integer pp;

    private Integer currPP;

    private Integer priority;

    private PokeTypes type;

    private List<Flags> flags;

    private MoveCategory category;

    private String stat;

    private Double statChance;

    private Integer stages;

    private Status status;

    private Double statusChance;

    private Double recoil;

    public Builder(int id) {
      this.id = id;
      flags = new ArrayList<>();
    }

    public Builder withAccuracy(Integer accuracy) {
      this.accuracy = accuracy;
      return this;
    }

    public Builder withPower(Integer basePower) {
      this.basePower = basePower;
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

    public Builder withCurrPP(Integer currPP) {
      this.currPP = currPP;
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

    public Builder withFlags(List<Flags> flags) {
      this.flags.addAll(flags);
      return this;
    }

    public Builder ofCategory(MoveCategory category) {
      this.category = category;
      return this;
    }

    public Builder affectsStat(String stat) {
      this.stat = stat;
      return this;
    }

    public Builder withStatChance(Double statChance) {
      this.statChance = statChance;
      return this;
    }

    public Builder withStages(Integer stages) {
      this.stages = stages;
      return this;
    }

    public Builder giveStatus(Status status) {
      this.status = status;
      return this;
    }

    public Builder withStatusChance(Double statusChance) {
      this.statusChance = statusChance;
      return this;
    }

    public Builder withRecoil(Double recoil) {
      this.recoil = recoil;
      return this;
    }

    public Move build() {
      Move move = new Move(this.id);
      move.accuracy = this.accuracy;
      move.basePower = this.basePower;
      move.description = this.description;
      move.shortDescription = this.shortDescription;
      move.name = this.name;
      move.pp = this.pp;
      if (this.currPP < 0) {
        move.currPP = 0;
      } else if (this.currPP > this.pp) {
        move.currPP = this.pp;
      } else {
        move.currPP = this.currPP;
      }
      move.priority = this.priority;
      move.type = this.type;
      move.flags = this.flags;
      move.category = this.category;
      move.stat = this.stat;
      move.statChance = this.statChance;
      move.stages = this.stages;
      move.status = this.status;
      move.statusChance = this.statusChance;
      move.recoil = this.recoil;

      return move;
    }
  }

  protected Move(int id) {
    this.id = id;
  }

  protected Move(Move m) {
    this.id = m.id;
    this.accuracy = m.accuracy;
    this.basePower = m.basePower;
    this.category = m.category;
    this.description = m.description;
    this.shortDescription = m.shortDescription;
    this.name = m.name;
    this.pp = m.pp;
    if (m.currPP < 0) {
      this.currPP = 0;
    } else if (m.currPP > m.pp) {
      this.currPP = m.pp;
    } else {
      this.currPP = m.currPP;
    }
    this.priority = m.priority;
    this.type = m.type;
    this.flags = m.flags;
    this.category = m.category;
    this.stat = m.stat;
    this.statChance = m.statChance;
    this.stages = m.stages;
    this.status = m.status;
    this.statusChance = m.statusChance;
    this.recoil = m.recoil;
  }

  private Integer id;

  private Integer accuracy;

  private Integer basePower;

  private String description;

  private String shortDescription;

  private String name;

  private Integer pp;

  private Integer currPP;

  private Integer priority;

  private PokeTypes type;

  private List<Flags> flags;

  private MoveCategory category;

  private String stat;

  private Double statChance;

  private Integer stages;

  private Status status;

  private Double statusChance;

  private Double recoil;

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
   * @return the move's name
   */
  public String getName() {
    return name;
  }

  /**
   * @return the max PP
   */
  public Integer getPP() {
    return pp;
  }

  /**
   * @return the current PP
   */
  public Integer getCurrPP() {
    return currPP;
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

  public void setPP(int pp) {
    this.pp = pp;
  }

  public int getId() {
    return this.id;
  }

  public List<Flags> getFlags() {
    return ImmutableList.copyOf(flags);
  }

  public String getAffectedStat() {
    return stat;
  }

  public Integer getStages() {
    return stages;
  }

  public Double getStatChance() {
    return statChance;
  }

  public MoveResult getMoveResult(AttackEvent atkEvent) {
    return new MoveResult(atkEvent.getAttackingPokemon(),
        atkEvent.getDefendingPokemon(), this, atkEvent.getBattle().getArena());
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
