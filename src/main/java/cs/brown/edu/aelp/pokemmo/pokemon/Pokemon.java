package cs.brown.edu.aelp.pokemmo.pokemon;

import java.util.ArrayList;
import java.util.List;

import cs.brown.edu.aelp.pokemmo.battle.EffectSlot;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.Move;

public class Pokemon {

  /**
   * Builder for Pokemon class.
   *
   * @author pzhang15
   *
   */
  public static class Builder {
    // Stats that determine move damage and other battle information
    private int currHp;
    private int maxHp;
    private int atk;
    private int def;
    private int specAtk;
    private int specDef;
    private int spd;
    private int eva;
    private int acc;

    private int exp;

    // Some Pokemon have 2 types, some have only 1 type
    private PokeType type;

    // Pokemon's moves. Some moves can be null
    private List<Move> moves = new ArrayList<>();

    private String nickname; // Captured Pokemon can have a nickname
    private String species; // The actual species of Pokemon
    private Integer id;
    private int gender;
    private boolean stored;

    /**
     * Constructs the builder.
     */
    public Builder() {
    }

    public Builder asStored(boolean stored) {
      this.stored = stored;
      return this;
    }

    public Builder withExp(int exp) {
      this.exp = exp;
      return this;
    }

    public Builder withGender(int gender) {
      this.gender = gender;
      return this;
    }

    public Builder withId(Integer id) {
      this.id = id;
      return this;
    }

    public Builder withHp(int hp) {
      this.currHp = hp;
      return this;
    }

    public Builder withMaxHp(int hp) {
      this.maxHp = hp;
      return this;
    }

    public Builder withAtk(int atk) {
      this.atk = atk;
      return this;
    }

    public Builder withDef(int def) {
      this.def = def;
      return this;
    }

    public Builder withSpecAtk(int specAtk) {
      this.specAtk = specAtk;
      return this;
    }

    public Builder withSpecDef(int specDef) {
      this.specDef = specDef;
      return this;
    }

    public Builder withSpd(int spd) {
      this.spd = spd;
      return this;
    }

    public Builder withEva(int eva) {
      this.eva = eva;
      return this;
    }

    public Builder withAcc(int acc) {
      this.acc = acc;
      return this;
    }

    public Builder withType(PokeType type) {
      this.type = type;
      return this;
    }

    public Builder withMove(Move move) {
      this.moves.add(move);
      return this;
    }

    public Builder withNickName(String nickname) {
      this.nickname = nickname;
      return this;
    }

    public Builder ofSpecies(String species) {
      this.species = species;
      return this;
    }

    /**
     * Builds the Pokemon class and returns the built Pokemon class.
     *
     * @return the build Pokemon class
     */
    public Pokemon build() {
      // TODO: Use asserts to ensure mandatory fields are filled out
      Pokemon pokemon = new Pokemon(this.id);

      pokemon.health = this.currHp;
      pokemon.baseHealth = this.maxHp;
      pokemon.attack = this.atk;
      pokemon.defense = this.def;
      pokemon.specialAttack = this.specAtk;
      pokemon.specialDefense = this.specDef;
      pokemon.speed = this.spd;
      pokemon.accuracyStage = this.acc;

      pokemon.exp = this.exp;

      pokemon.type = this.type;

      pokemon.nickname = this.nickname;
      pokemon.species = this.species;
      // pokemon.id = this.id;
      pokemon.gender = this.gender;
      pokemon.stored = this.stored;

      return pokemon;
    }
  }

  private String nickname;

  private String species;

  private Integer gender;

  private boolean stored;

  private final Integer id;

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

  private Integer exp;

  private PokeType type;

  private List<Move> moves;

  private EffectSlot effectSlot = new EffectSlot();

  public Pokemon(Integer id, String nickname, Integer baseHealth, Integer health, Integer attack,
      Integer defense, Integer specialAttack, Integer specialDefense, Integer speed, Integer exp,
      PokeType type, List<Move> moves) {
    super();
    this.nickname = nickname;
    this.id = id;
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

  public Pokemon(Integer id) {
    super();
    this.id = id;
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
