package cs.brown.edu.aelp.pokemmo.pokemon;

import com.google.common.collect.ImmutableMap;
import cs.brown.edu.aelp.pokemmo.battle.EffectSlot;
import cs.brown.edu.aelp.pokemmo.data.BatchSavable;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.Move;
import cs.brown.edu.aelp.util.Identifiable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: We probably need a status column in our pokemon DB

public class Pokemon extends Identifiable implements BatchSavable {

  private static ImmutableMap<Integer, Double> stageMultipliers = ImmutableMap
      .<Integer, Double>builder()
      .put(-6, 0.25).put(-5, 2.0 / 7.0)
      .put(-4, 2.0 / 6.0)
      .put(-3, 0.4)
      .put(-2, 0.5)
      .put(-1, 2.0 / 3.0)
      .put(0, 1.0)
      .put(1, 1.5)
      .put(2, 2.0)
      .put(3, 2.5)
      .put(4, 3.0)
      .put(5, 3.5)
      .put(6, 4.0)
      .build();

  private static ImmutableMap<Integer, Double> accEvaMultipliers = ImmutableMap
      .<Integer, Double>builder()
      .put(-6, 33.0 / 100.0)
      .put(-5, 36.0 / 100.0)
      .put(-4, 43.0 / 100.0)
      .put(-3, 0.5)
      .put(-2, 0.6)
      .put(-1, 0.75)
      .put(0, 1.0)
      .put(1, 133.0 / 100.0)
      .put(2, 166 / 100.0)
      .put(3, 2.0)
      .put(4, 2.5)
      .put(5, 266.0 / 100.0)
      .put(6, 3.0)
      .build();

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
    private int exp;

    // Some Pokemon have 2 types, some have only 1 type
    private List<PokeTypes> typeList = new ArrayList<>();

    // Pokemon's moves. Some moves can be null
    private List<Move> moves = new ArrayList<>();

    private String nickname; // Captured Pokemon can have a nickname
    private String species; // The actual species of Pokemon
    private Integer id;
    private Integer gender;
    private boolean stored;

    private Status status;

    public Builder(Integer id) {
      this.id = id;
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

    public Builder withType(PokeTypes type) {
      this.typeList.add(type);
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

    public Builder withStatus(Status status){
      this.status = status;
      return this;
    }

    /**
     * Builds the Pokemon class and returns the built Pokemon class.
     *
     * @return the build Pokemon class
     */
    public Pokemon build() {
      Pokemon pokemon = new Pokemon(this.id);

      pokemon.exp = this.exp;
      pokemon.lvl = calcLevel(this.exp);

      pokemon.baseHealth = hpScale(this.maxHp, pokemon.lvl);

      if (this.currHp == 0) {
        pokemon.health = pokemon.baseHealth;
      } else {
        pokemon.health = this.currHp;
      }

      pokemon.attack = this.atk;
      pokemon.defense = this.def;
      pokemon.specialAttack = this.specAtk;
      pokemon.specialDefense = this.specDef;
      pokemon.speed = this.spd;

      pokemon.status = this.status;

      pokemon.typeList = this.typeList;

      pokemon.nickname = this.nickname;
      pokemon.species = this.species;
      pokemon.gender = this.gender;
      pokemon.stored = this.stored;

      return pokemon;
    }
  }

  private String nickname;

  private String species;

  private Integer gender;

  private boolean stored;

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

  private Integer accuracyStage;

  private Integer evasionStage;

  private Integer exp;

  private Integer lvl;

  private List<PokeTypes> typeList;

  private Status status;

  private List<Move> moves;

  private EffectSlot effectSlot = new EffectSlot();

  private Map<String, Object> changes = new HashMap<>();

  private Pokemon(Integer id) {
    super(id);
    resetStatStages();
  }

  public Integer getBaseHealth() {
    return baseHealth;
  }

  public Integer getHealth() {
    return health;
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

  public Integer getExp() {
    return exp;
  }

  public Integer getLevel() {
    return lvl;
  }

  public Integer getSpeed() {
    return speed;
  }

  public List<PokeTypes> getType() {
    return typeList;
  }

  public Status getStatus() {
    return status;
  }

  public EffectSlot getEffectSlot() {
    return effectSlot;
  }

  public boolean isKnockedOut() {
    return health == 0;
  }

  public void setHealth(int health) {
    if (health < 0) {
      health = 0;
    } else if (health > baseHealth) {
      health = baseHealth;
    }

    this.health = health;
    this.addChange("cur_health", health);
  }

  public void setStored(boolean stored) {
    this.stored = stored;
    this.addChange("stored", stored);
  }

  public void addExp(Integer experience) {
    this.exp += experience;
    this.addChange("experience", exp);
  }

  public void changeNickname(String newName) {
    this.nickname = newName;
    this.addChange("nickname", nickname);
  }

  // TODO: Better evolution system
  public void evolve(String evolvedSpecies) {
    this.species = evolvedSpecies;
    this.addChange("species", species);
  }

  public Double getEffectiveAttack() {
    return statScale(attack, lvl) * stageMultipliers.get(attackStage);
  }

  public Double getEffectiveSpecialAttack() {
    return statScale(specialAttack, lvl) * stageMultipliers.get(specialAttackStage);
  }

  public Double getEffectiveDefense() {
    return statScale(defense, lvl) * stageMultipliers.get(defenseStage);
  }

  public Double getEffectiveSpecialDefense() {
    return statScale(specialDefense, lvl) * stageMultipliers.get(specialDefenseStage);
  }

  public Double getEffectiveSpeed() {
    return statScale(speed, lvl) * stageMultipliers.get(speedStage);
  }

  public Double getEffectiveAcc() {
    return accEvaMultipliers.get(accuracyStage);
  }

  public Double getEffectiveEva() {
    return accEvaMultipliers.get(evasionStage);
  }

  public void modifyAttackStage(int dif) {
    attackStage = calcStage(attackStage, dif);
  }

  public void modifySpecialAttackStage(int dif) {
    specialAttackStage = calcStage(specialAttackStage, dif);
  }

  public void modifyDefenseStage(int dif) {
    defenseStage = calcStage(defenseStage, dif);
  }

  public void modifySpecialDefenseStage(int dif) {
    specialDefenseStage = calcStage(specialDefenseStage, dif);
  }

  public void modifySpeedStage(int dif) {
    speedStage = calcStage(speedStage, dif);
  }

  public void modifyAccuracyStage(int dif) {
    accuracyStage = calcStage(accuracyStage, dif);
  }

  public void modifyEvasionStage(int dif) {
    evasionStage = calcStage(evasionStage, dif);
  }

  public void resetStatStages() {
    attackStage = 0;
    defenseStage = 0;
    specialAttackStage = 0;
    specialDefenseStage = 0;
    speedStage = 0;
    accuracyStage = 0;
    evasionStage = 0;
  }

  private static int calcStage(int curStage, int dif) {
    int stage = curStage + dif;

    if (stage > 6) {
      stage = 6;
    } else if (stage < -6) {
      stage = -6;
    }

    return stage;
  }

  private static Integer hpScale(int baseHp, int level) {
    Double scaledHp = Math.floor((2.0 * baseHp * level) / 100.0) + level + 10;
    return scaledHp.intValue();
  }

  private static Integer statScale(int baseStat, int level) {
    Double scaledStat = Math.floor((2.0 * baseStat * level) / 100.0) + 5;
    return scaledStat.intValue();
  }

  private static Integer calcLevel(int exp) {
    Double calcLevel = Math.floor(Math.pow((5.0 / 4.0) * exp, (1.0 / 3.0))) + 1;

    // Cap the max level.
    if (calcLevel > 100.0) {
      calcLevel = 100.0;
    }

    return calcLevel.intValue();
  }

  public static Integer calcXpByLevel(int level) {
    Double calcLevel = Math.ceil((5.0 / 4.0) * Math.pow(level, 3));
    return calcLevel.intValue();
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  // TODO: Fix toString
  @Override
  public String toString() {
    return "Pokemon [id=" + this.getId() + ", health=" + health + ", type="
        + typeList.get(0) + ", moves=" + moves + ", effectSlot=" + effectSlot + "]";
  }

  private void addChange(String key, Object o) {
    synchronized (this.changes) {
      this.changes.put(key, o);
    }
  }

  @Override
  public Map<String, Object> getChangesForSaving() {
    synchronized (this.changes) {
      Map<String, Object> toSave = new HashMap<>(this.changes);
      this.changes.clear();
      return toSave;
    }
  }
}
