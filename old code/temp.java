package cs.brown.edu.aelp.pokemmo.pokemon.moves;

import cs.brown.edu.aelp.pokemmo.battle.events.AttackEvent;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class temp {

  public void applyStandardModifiers(AttackEvent evt, MoveResult mr) {

    applyEffectivenessModifiers(evt, mr);

    // Target modifier
    mr.setModifier(MoveResult.ModifierType.TARGET,
        (getTarget().equals(Move.MoveTarget.NORMAL)) ? 1 : .75);

    // Technically this is not inclusive on 1
    mr.setModifier(MoveResult.ModifierType.RANDOM,
        ThreadLocalRandom.current().nextDouble(.85, 1));
  }

  public void applyEffectivenessModifiers(AttackEvent evt, MoveResult mr) {

    mr.setModifier(MoveResult.ModifierType.STAB, evt.getAttackingPokemon().getType()
        .getOffensiveEffectiveness(getType()));

    mr.setModifier(MoveResult.ModifierType.TYPE, evt.getDefendingPokemon().getType()
        .getDefensiveEffectiveness(getType()));

    mr.setModifier(MoveResult.ModifierType.WEATHER,
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
        * (evt.getAttackingPokemon().getEffectiveAcc()
        / evt.getAttackingPokemon().getEffectiveEva());

    System.out.println("effective Accuracy: " + effAccuracy);

    return (Math.random() <= effAccuracy);
  }

  public MoveResult getResult(AttackEvent evt) {
    MoveResult mr = new MoveResult(evt.getAttackingPokemon(),
        evt.getDefendingPokemon());

    if (!accuracyCheck(evt)) {
      mr.setOutcome(MoveResult.MoveOutcome.MISS);
      return mr;
    }

    mr.setOutcome(MoveResult.MoveOutcome.HIT);

    boolean isCrit = isCrit();

    double critModifier = isCrit ? getCritModifier() : 1;

    double atkDefRatio = 0;

    if (getCategory().equals(Move.MoveCategory.PHYSICAL)) {
      atkDefRatio = evt.getAttackingPokemon().getEffectiveAttack()
          / evt.getDefendingPokemon().getEffectiveDefense();

    } else if (getCategory().equals(Move.MoveCategory.SPECIAL)) {
      atkDefRatio = evt.getAttackingPokemon().getEffectiveSpecialAttack()
          / evt.getDefendingPokemon().getEffectiveSpecialDefense();
    }

    double baseDamage = ((((((2 * evt.getAttackingPokemon().getLevel()
        * critModifier) / 5) + 2) * getBasePower() * atkDefRatio) / 50) + 2);

    mr.setBaseDamage(baseDamage);

    // Apply general crit modifier
    mr.setModifier(MoveResult.ModifierType.CRIT, critModifier);

    applyStandardModifiers(evt, mr);

    return mr;
  }




  public enum ModifierType {
    TARGET, WEATHER, CRIT, RANDOM, STAB, TYPE, GENERIC
  }

  private Map<ModifierType, Double> modifierMap = new HashMap<>();

  private List<Double> genericModifiers = new LinkedList<>();

  private Double baseDamage = 0.0;

  private Double modifier = 1.0;

  private MoveOutcome outcome = MoveOutcome.NO_EFFECT;

  private Pokemon attackingPokemon;

  private Pokemon defendingPokemon;

  public MoveResult(Pokemon attackingPokemon, Pokemon defendingPokemon) {
    this.attackingPokemon = attackingPokemon;
    this.defendingPokemon = defendingPokemon;
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
    modifier = 1.0;

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

  public Pokemon getAttackingPokemon() {
    return attackingPokemon;
  }

  public Pokemon getDefendingPokemon() {
    return defendingPokemon;
  }



  // Don't use this constructor, use the builder instead,
  // this one doesn't have the correct stat scalings implemented

  public Pokemon(Integer id, String nickname, Integer baseHealth,
      Integer health, Integer attack, Integer defense, Integer specialAttack,
      Integer specialDefense, Integer speed, Integer exp, PokeType type,
      List<Move> moves) {
    super(id);
    this.nickname = nickname;
    this.baseHealth = baseHealth;
    this.health = health;
    this.attack = attack;
    this.defense = defense;
    this.specialAttack = specialAttack;
    this.specialDefense = specialDefense;
    this.speed = speed;
    this.exp = exp;
    this.type = type;
    this.moves = moves;

    resetStatStages();
}
