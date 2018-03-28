package cs.brown.edu.aelp.pokemmo.pokemon;

import java.util.List;

import cs.brown.edu.aelp.pokemmo.battle.EffectSlot;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.Move;

public class Pokemon {

  private final String id;

  private Integer baseHealth;

  private Integer health;

  private Integer attack;

  private Integer defense;

  private Integer specialAttack;

  private Integer specialDefense;

  private Integer speed;

  private Integer level;

  private PokeType type;

  private List<Move> moves;

  private EffectSlot effectSlot = new EffectSlot();

  public Pokemon(String id, Integer baseHealth, Integer health, Integer attack,
      Integer defense, Integer specialAttack, Integer specialDefense,
      Integer speed, Integer level, PokeType type, List<Move> moves) {
    super();
    this.id = id;
    this.baseHealth = baseHealth;
    this.health = health;
    this.attack = attack;
    this.defense = defense;
    this.specialAttack = specialAttack;
    this.specialDefense = specialDefense;
    this.speed = speed;
    this.level = level;
    this.type = type;
    this.moves = moves;
  }

  public Integer getSpeed() {
    return speed;
  }

  public Integer getHealth() {
    return health;
  }

  public EffectSlot getEffectSlot() {
    return effectSlot;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Pokemon [id=" + id + ", health=" + health + ", type=" + type
        + ", moves=" + moves + ", effectSlot=" + effectSlot + "]";
  }

  public void setHealth(int health) {
    if (health < 0) {
      health = 0;
    }

    this.health = health;
  }

  public Integer getBaseHealth() {
    return baseHealth;
  }

  public boolean isKnockedOut() {
    return health == 0;
  }

  /**
   * @return the level
   */
  public Integer getLevel() {
    return level;
  }

  public Integer getAttack() {
    return attack;
  }

  public Integer getSpecialAttack() {
    return specialAttack;
  }

  public Integer getDefense() {
    return defense;
  }

  public Integer getSpecialDefense() {
    return specialDefense;
  }

  public PokeType getType() {
    return type;
  }
}
