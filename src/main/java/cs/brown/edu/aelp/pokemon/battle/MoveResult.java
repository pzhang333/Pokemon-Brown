package cs.brown.edu.aelp.pokemon.battle;

public class MoveResult {

  public enum MoveOutcome {
    HIT, MISS, EVADE, BLOCKED, NON_ATTACK_SUCCESS, NON_ATTACK_FAIL, NO_EFFECT
  }

  private Double damage = 0.0;

  private MoveOutcome outcome = MoveOutcome.NO_EFFECT;

  public MoveResult() {

  }

  /**
   * @return the damage
   */
  public Double getDamage() {
    return damage;
  }

  /**
   * @param damage
   *          the damage to set
   */
  public void setDamage(Double damage) {
    this.damage = damage;
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

}
