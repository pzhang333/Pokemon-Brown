package cs.brown.edu.aelp.pokemon.trainer;

import java.util.List;

import cs.brown.edu.aelp.pokemon.battle.EffectSlot;
import cs.brown.edu.aelp.pokemon.battle.Move;
import cs.brown.edu.aelp.pokemon.battle.PokeType;

public class Pokemon {

  private final String id;

  private Double baseHealth;

  private Double health;

  private PokeType type;

  private List<Move> moves;

  private EffectSlot effectSlot = new EffectSlot();

  public Pokemon(String id, Double baseHealth, Double health, PokeType type,
      List<Move> moves) {
    this.id = id;
    this.baseHealth = baseHealth;
    this.health = health;
    this.type = type;
    this.moves = moves;
  }

  public Double getSpeed() {
    return 1.0;
  }

  public Double getHealth() {
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

  public void setHealth(double health) {
    this.health = health;
  }

  public Double getBaseHealth() {
    return baseHealth;
  }
}
