package cs.brown.edu.aelp.pokemmo.pokemon;

import java.util.List;

import cs.brown.edu.aelp.pokemmo.battle.EffectSlot;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.Move;

public class Pokemon {

  private final String id;

  private Integer baseHealth;

  private Integer health;

  private Integer attackStage;

  private Integer attack;

  private Integer defenseStage;

  private Integer defense;

  private Integer specialAttackStage;

  private Integer specialAttack;

  private Integer specialDefenseStage;

  private Integer specialDefense;

  private Integer speedStage;

  private Integer speed;

  private Integer accuracyStage = 3;

  private Integer evasionStage = 3;

  private Integer level;

  private PokeType type;

  private List<Move> moves;

  private EffectSlot effectSlot = new EffectSlot();

  public Pokemon(String id, Integer baseHealth, Integer health, Integer attack, Integer defense,
      Integer specialAttack, Integer specialDefense, Integer speed, Integer level, PokeType type,
      List<Move> moves) {
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

    resetStatStages();
  }

  public void resetStatStages() {
    attackStage = 3;
    defenseStage = 3;
    specialAttackStage = 3;
    specialDefenseStage = 3;
    speedStage = 3;
    accuracyStage = 3;
    evasionStage = 3;
  }

  public Integer getSpeed() {
    return speed;
  }

  public Integer getEffectiveSpeed() {
    return (int) Math.round(((1.0 / 3) * speedStage) * getSpeed());
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
    return "Pokemon [id=" + id + ", health=" + health + ", type=" + type + ", moves=" + moves
        + ", effectSlot=" + effectSlot + "]";
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

  private int calcStage(int curStage, int dif) {
    int stage = curStage + dif;

    if (stage > 6) {
      stage = 6;
    } else if (stage < -6) {
      stage = -6;
    }

    return stage;
  }

  /**
   * @return the level
   */
  public Integer getLevel() {
    return level;
  }

  public Integer getEffectiveAttack() {
    return (int) Math.round(((1.0 / 2) * attackStage) * getAttack());
  }

  public void modifyAttackStage(int dif) {
    attackStage = calcStage(attackStage, dif);
  }

  public Integer getAttack() {
    return attack;
  }

  public void modifySpecialAttackStage(int dif) {
    specialAttackStage = calcStage(specialAttackStage, dif);
  }

  public Integer getEffectiveSpecialAttack() {
    return (int) Math.round(((1.0 / 2) * specialAttackStage) * getSpecialAttack());
  }

  public Integer getSpecialAttack() {
    return specialAttack;
  }

  public void modifyDefenseStage(int dif) {
    defenseStage = calcStage(defenseStage, dif);
  }

  public Integer getEffectiveDefense() {
    return (int) Math.round(((1.0 / 2) * defenseStage) * getDefense());
  }

  public Integer getDefense() {
    return defense;
  }

  public void modifySpecialDefenseStage(int dif) {
    specialDefenseStage = calcStage(specialDefenseStage, dif);
  }

  public Integer getEffectiveSpecialDefense() {
    return (int) Math.round(((1.0 / 2) * specialDefenseStage) * getSpecialDefense());
  }

  public Integer getSpecialDefense() {
    return specialDefense;
  }

  public PokeType getType() {
    return type;
  }

  public void modifyEvasionStage(int dif) {
    evasionStage = calcStage(evasionStage, dif);
  }

  public Integer getEvasion() {
    return evasionStage;
  }

  public void modifyAccuracyStage(int dif) {
    accuracyStage = calcStage(accuracyStage, dif);
  }

  public Integer getAccuracy() {
    return accuracyStage;
  }

  public Integer getId() {
    return 1;
  }
}
