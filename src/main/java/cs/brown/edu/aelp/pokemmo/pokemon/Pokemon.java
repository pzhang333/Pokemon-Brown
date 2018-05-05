package cs.brown.edu.aelp.pokemmo.pokemon;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import cs.brown.edu.aelp.pokemmo.battle.EffectSlot;
import cs.brown.edu.aelp.pokemmo.battle.Item;
import cs.brown.edu.aelp.pokemmo.battle.Item.ItemType;
import cs.brown.edu.aelp.pokemmo.data.SQLBatchSavable;
import cs.brown.edu.aelp.pokemmo.data.authentication.User;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.Move;
import cs.brown.edu.aelp.pokemmo.trainer.Trainer;
import cs.brown.edu.aelp.pokemon.Main;
import cs.brown.edu.aelp.util.Identifiable;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

// TODO: We probably need a status column in our pokemon DB

public class Pokemon extends Identifiable implements SQLBatchSavable {

  private static ImmutableMap<Integer, Double> stageMultipliers = ImmutableMap
      .<Integer, Double>builder().put(-6, 0.25).put(-5, 2.0 / 7.0)
      .put(-4, 2.0 / 6.0).put(-3, 0.4).put(-2, 0.5).put(-1, 2.0 / 3.0)
      .put(0, 1.0).put(1, 1.5).put(2, 2.0).put(3, 2.5).put(4, 3.0).put(5, 3.5)
      .put(6, 4.0).build();

  private static ImmutableMap<Integer, Double> accEvaMultipliers = ImmutableMap
      .<Integer, Double>builder().put(-6, 33.0 / 100.0).put(-5, 36.0 / 100.0)
      .put(-4, 43.0 / 100.0).put(-3, 0.5).put(-2, 0.6).put(-1, 0.75).put(0, 1.0)
      .put(1, 133.0 / 100.0).put(2, 166 / 100.0).put(3, 2.0).put(4, 2.5)
      .put(5, 266.0 / 100.0).put(6, 3.0).build();

  /**
   * Builder for Pokemon class.
   *
   * @author pzhang15
   *
   */
  public static class Builder {
    // Stats that determine move damage and other battle information
    private int currHp;
    private int baseHp;
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
    private Trainer owner;

    private Integer catchRate;
    private Integer expOnDefeat;
    private Integer evolveAt;

    private Integer xOffset;
    private Integer yOffset;
    private Integer fps;

    private Status status = Status.NONE;

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

    public Builder withCurrHp(int currHp) {
      this.currHp = currHp;
      return this;
    }

    public Builder withBaseHp(int baseHp) {
      this.baseHp = baseHp;
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

    public Builder withTypes(List<PokeTypes> typeList) {
      this.typeList.addAll(typeList);
      return this;
    }

    public Builder withMove(Move move) {
      this.moves.add(move);
      return this;
    }

    public Builder withMoves(List<Move> moveList) {
      this.moves.addAll(moveList);
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

    public Builder withStatus(Status status) {
      this.status = status;
      return this;
    }

    public Builder withOwner(Trainer u) {
      this.owner = u;
      return this;
    }

    public Builder withXOffset(Integer xOffset) {
      this.xOffset = xOffset;
      return this;
    }

    public Builder withYOffset(Integer yOffset) {
      this.yOffset = yOffset;
      return this;
    }

    public Builder withFPS(Integer fps) {
      this.fps = fps;
      return this;
    }

    public Builder evolvesAt(Integer evolveAt) {
      this.evolveAt = evolveAt;
      return this;
    }

    public Builder withCatchRate(Integer catchRate) {
      this.catchRate = catchRate;
      return this;
    }

    public Builder withEXPOnDefeat(Integer expOnDefeat) {
      this.expOnDefeat = expOnDefeat;
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

      pokemon.baseHp = this.baseHp;

      int scaledHp = hpScale(this.baseHp, pokemon.lvl);

      pokemon.hp = scaledHp;

      if (this.currHp <= 0) {
        pokemon.currHp = pokemon.hp;
      } else if (this.currHp > scaledHp) {
        pokemon.currHp = pokemon.hp;
      } else {
        pokemon.currHp = this.currHp;
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
      pokemon.moves = this.moves;

      pokemon.owner = this.owner;

      pokemon.xOffset = this.xOffset;
      pokemon.yOffset = this.yOffset;
      pokemon.fps = this.fps;

      pokemon.catchRate = this.catchRate;
      pokemon.expOnDefeat = this.expOnDefeat;
      pokemon.evolveAt = this.evolveAt;

      return pokemon;
    }
  }

  private String nickname;

  private String species;

  private Integer gender;

  private boolean stored;

  private Trainer owner;

  private Integer baseHp;

  private Integer hp;

  private Integer currHp;

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

  private Integer xOffset;

  private Integer yOffset;

  private Integer fps;

  private List<PokeTypes> typeList;

  private Status status = Status.NONE;

  private List<Move> moves;

  private EffectSlot effectSlot = new EffectSlot();

  private boolean changed = false;

  private Integer catchRate;
  private Integer expOnDefeat;
  private Integer evolveAt;

  private Pokemon(Integer id) {
    super(id);
    resetStatStages();
  }

  public void setOwner(Trainer t) {
    this.owner = t;
  }

  public Integer getBaseHp() {
    return baseHp;
  }

  public Integer getMaxHp() {
    return hp;
  }

  public Integer getCurrHp() {
    return currHp;
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

  public Integer getCatchRate() {
    return catchRate;
  }

  public Integer getExpOnDefeat() {
    return expOnDefeat;
  }

  public boolean isKnockedOut() {
    return currHp == 0;
  }

  public void setHealth(int health) {
    if (health <= 0) {
      currHp = 0;
    } else if (health >= getMaxHp()) {
      currHp = getMaxHp();
    } else {
      currHp = health;
    }

    this.setChanged(true);
  }

  public void setStored(boolean stored) {
    this.stored = stored;
    this.setChanged(true);
  }

  public Boolean isStored() {
    return this.stored;
  }

  public void addExp(Integer experience) {
    this.exp += experience;
    this.lvl = calcLevel(this.exp);
    if (this.lvl >= this.evolveAt) {
      // TODO: Inform user that their pokemon evolved!
      evolve();
    }
    this.setChanged(true);
  }

  public void changeNickname(String newName) {
    this.nickname = newName;
    this.setChanged(true);
  }

  public String getSpecies() {
    return this.species;
  }

  public Trainer getOwner() {
    return this.owner;
  }

  public String getNickname() {
    return this.nickname;
  }

  public Integer getGender() {
    return this.gender;
  }

  public Integer getXOffset() {
    return xOffset;
  }

  public Integer getYOffset() {
    return yOffset;
  }

  public Integer getFPS() {
    return fps;
  }

  public void evolve() {
    String evolvedSpecies = PokemonLoader.getEvolutionName(this.species);

    if (!evolvedSpecies.isEmpty()) {
      // We don't care about the level of the Pokemon
      Pokemon temp = PokemonLoader.load(evolvedSpecies, 0);

      this.species = temp.species;
      this.baseHp = temp.baseHp;
      this.attack = temp.attack;
      this.defense = temp.defense;
      this.specialAttack = temp.specialAttack;
      this.specialDefense = temp.specialDefense;
      this.speed = temp.speed;

      this.typeList = temp.typeList;

      this.moves = temp.moves;

      this.catchRate = temp.catchRate;
      this.expOnDefeat = temp.expOnDefeat;
      this.evolveAt = temp.evolveAt;

      this.xOffset = temp.xOffset;
      this.yOffset = temp.yOffset;
      this.fps = temp.fps;

      this.setChanged(true);
    }
  }

  public Double getEffectiveAttack() {
    return statScale(attack, lvl) * stageMultipliers.get(attackStage);
  }

  public Double getEffectiveSpecialAttack() {
    return statScale(specialAttack, lvl)
        * stageMultipliers.get(specialAttackStage);
  }

  public Double getEffectiveDefense() {
    return statScale(defense, lvl) * stageMultipliers.get(defenseStage);
  }

  public Double getEffectiveSpecialDefense() {
    return statScale(specialDefense, lvl)
        * stageMultipliers.get(specialDefenseStage);
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

  public List<Move> getMoves() {
    return this.moves;
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

  public boolean isCaught(Item ball) {

    if (!ball.isPokeball()) {
      return false;
    } else if (ball.getType().equals(ItemType.MASTER_BALL)) {
      return true;
    }

    double ballBonus = 1.0;
    double statusBonus = 1.0;

    if (this.status == Status.SLEEP || this.status == Status.FREEZE) {
      statusBonus = 2.0;
    } else if (this.status == Status.PARALYZE || this.status == Status.BURN
        || this.status == Status.POISON) {
      statusBonus = 1.5;
    }

    double a = ((3 * this.hp - 2 * this.currHp) * this.catchRate * ballBonus
        * statusBonus) / (3 * this.hp);

    if (a >= 255.0) {
      return true;
    }

    Double b = 1048560 / Math.sqrt(Math.sqrt(16711680 / a));

    int baseValue = b.intValue();

    int a1 = ThreadLocalRandom.current().nextInt(0, 65536);
    int a2 = ThreadLocalRandom.current().nextInt(0, 65536);
    int a3 = ThreadLocalRandom.current().nextInt(0, 65536);
    int a4 = ThreadLocalRandom.current().nextInt(0, 65536);

    return a1 < baseValue && a2 < baseValue && a3 < baseValue && a4 < baseValue;
  }

  public void fullRestore() {
    this.currHp = this.hp;
    this.status = Status.NONE;
    this.effectSlot = new EffectSlot();
    this.setChanged(true);
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public void addHealth(Integer addHP) {
    setHealth(this.currHp + addHP);
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

  public Pokemon snapshot() {
    Pokemon pokemon = new Pokemon(getId());

    pokemon.exp = this.exp;
    pokemon.lvl = this.lvl;

    pokemon.baseHp = this.baseHp;

    pokemon.hp = this.hp;
    pokemon.currHp = this.currHp;

    pokemon.attack = this.attack;
    pokemon.defense = this.defense;
    pokemon.specialAttack = this.specialAttack;
    pokemon.specialDefense = this.specialDefense;
    pokemon.speed = this.specialDefense;

    pokemon.attackStage = this.attackStage;
    pokemon.defenseStage = this.defenseStage;
    pokemon.specialAttackStage = this.specialAttackStage;
    pokemon.specialDefenseStage = this.specialDefenseStage;
    pokemon.speedStage = this.speedStage;
    pokemon.accuracyStage = this.accuracyStage;
    pokemon.evasionStage = this.evasionStage;

    pokemon.status = this.status;

    pokemon.typeList = this.typeList;

    pokemon.nickname = this.nickname;
    pokemon.species = this.species;
    pokemon.gender = this.gender;
    pokemon.stored = this.stored;
    pokemon.moves = this.moves;

    pokemon.owner = this.owner;

    pokemon.xOffset = this.xOffset;
    pokemon.yOffset = this.yOffset;
    pokemon.fps = this.fps;

    return pokemon;
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
    return (int) Math.ceil(Math.pow(level - 1, 3) * (4.0 / 5.0));
  }

  public static Integer xpWon(Pokemon winner, Pokemon loser) {
    Double expGained = ((1.0 * loser.getExpOnDefeat() * loser.getLevel()) / 5)
        * ((Math.pow(2 * loser.getLevel() + 10, 2.5))
            / (Math.pow(winner.getLevel() + loser.getLevel() + 10, 2.5)))
        + 1;
    return expGained.intValue();
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  // TODO: Fix toString
  @Override
  public String toString() {
    return getNickname() + " (" + getSpecies() + ")";
  }

  @Override
  public List<String> getUpdatableColumns() {
    return Lists.newArrayList("user_id", "nickname", "gender", "experience",
        "stored", "cur_health", "species", "pp_1", "pp_2", "pp_3", "pp_4");
  }

  @Override
  public String getTableName() {
    return "pokemon";
  }

  @Override
  public void bindValues(PreparedStatement p) throws SQLException {
    assert this.getOwner() instanceof User;
    p.setInt(1, this.getOwner().getId());
    p.setString(2, this.getNickname());
    p.setInt(3, this.getGender());
    p.setInt(4, this.getExp());
    p.setBoolean(5, this.isStored());
    p.setInt(6, this.getCurrHp());
    p.setString(7, this.getSpecies());
    for (int i = 8; i < 12; i++) {
      p.setInt(i, 0);
    }
    if (this.moves.size() > 0) {
      Move m1 = this.moves.get(0);
      p.setInt(8, m1.getPP());
    }
    if (this.moves.size() > 1) {
      Move m2 = this.moves.get(1);
      p.setInt(9, m2.getPP());
    }
    if (this.moves.size() > 2) {
      Move m3 = this.moves.get(2);
      p.setInt(10, m3.getPP());
    }
    if (this.moves.size() > 3) {
      Move m4 = this.moves.get(3);
      p.setInt(11, m4.getPP());
    }
    p.setInt(12, this.getId());
    p.addBatch();
  }

  @Override
  public boolean hasUpdates() {
    return this.changed;
  }

  @Override
  public void setChanged(boolean b) {
    this.changed = b;
  }

  @Override
  public List<String> getIdentifyingColumns() {
    return Lists.newArrayList("id");
  }

  public static class PokemonAdapter implements JsonSerializer<Pokemon> {

    @Override
    public JsonElement serialize(Pokemon src, Type typeOfSrc,
        JsonSerializationContext ctx) {
      JsonObject o = new JsonObject();
      o.addProperty("id", src.getId());
      if (src.getOwner() == null) {
        o.addProperty("owner_id", -1);
      } else {
        o.addProperty("owner_id", src.getOwner().getId());
      }
      o.addProperty("fps", src.getFPS());
      o.addProperty("x_offset", src.getXOffset());
      o.addProperty("y_offset", src.getYOffset());
      o.addProperty("maxHealth", src.getMaxHp());
      o.addProperty("health", src.getCurrHp());
      o.addProperty("currLvlExp", Pokemon.calcXpByLevel(src.getLevel()));
      o.addProperty("currExp", src.getExp());
      o.addProperty("nextExp", Pokemon.calcXpByLevel(src.getLevel() + 1));
      o.addProperty("status", src.getStatus().ordinal());
      o.addProperty("gender", src.getGender());
      o.addProperty("nickname", src.getNickname());
      o.addProperty("species", src.getSpecies());
      o.addProperty("stored", src.isStored());
      o.addProperty("level", src.getLevel());
      o.add("moves", Main.GSON().toJsonTree(src.getMoves()));
      return o;
    }

  }

}
