package cs.brown.edu.aelp.pokemmo.pokemon.moves;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MoveResult {

  public enum MoveOutcome {
    HIT, MISS, EVADE, BLOCKED, NON_ATTACK_SUCCESS, NON_ATTACK_FAIL, NO_EFFECT
  }

  public enum ModifierType {
    TARGET, WEATHER, CRIT, RANDOM, STAB, TYPE, GENERIC
  }

  private Map<ModifierType, Double> modifierMap = new HashMap<>();

  private List<Double> genericModifiers = new LinkedList<>();

  private Double baseDamage = 0.0;

  private Double modifier = 1.0;

  private MoveOutcome outcome = MoveOutcome.NO_EFFECT;

  public MoveResult() {

  }

  /**
   * @return the damage
   */
  public Double getBaseDamage() {
    return baseDamage;
  }

  public Integer getDamage() {
    return (int) Math.round(getBaseDamage() * getModifier());
  }

  public Double getModifier() {
    double modifier = 1;

    for (Double d : modifierMap.values()) {
      modifier *= d;
    }

    for (Double d : genericModifiers) {
      modifier *= d;
    }

    return modifier;
  }

  /**
   * @return the outcome
   */
  public MoveOutcome getOutcome() {
    return outcome;
  }

  /**
   * @param outcome
   *          the outcome to set
   */
  public void setOutcome(MoveOutcome outcome) {
    this.outcome = outcome;
  }

  public void setBaseDamage(double baseDamage) {
    this.baseDamage = baseDamage;
  }

  public void setModifier(ModifierType type, double modifier) {
    modifierMap.put(type, modifier);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "MoveResult [modifierMap=" + modifierMap + ", genericModifiers="
        + genericModifiers + ", baseDamage=" + baseDamage + ", modifier="
        + modifier + ", outcome=" + outcome + ", getDamage()=" + getDamage()
        + "]";
  }

}
