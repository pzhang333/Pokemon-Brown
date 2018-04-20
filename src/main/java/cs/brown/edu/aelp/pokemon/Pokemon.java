package cs.brown.edu.aelp.pokemon;

import cs.brown.edu.aelp.pokemon.battle.Move;

/**
 * The Pokemon class.
 */
public final class Pokemon {

  // TODO: Better Javadocs

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
    private PokeTypes type;
    private PokeTypes type2;

    // Pokemon's moves. Some moves can be null
    private Move move1;
    private Move move2;
    private Move move3;
    private Move move4;

    private String nickname; // Captured Pokemon can have a nickname
    private String species; // The actual species of Pokemon
    private int id;
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

    public Builder withId(int id) {
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

    public Builder type(PokeTypes type) {
      this.type = type;
      return this;
    }

    public Builder type2(PokeTypes type2) {
      this.type2 = type2;
      return this;
    }

    public Builder withMove1(Move move1) {
      this.move1 = move1;
      return this;
    }

    public Builder withMove2(Move move2) {
      this.move2 = move2;
      return this;
    }

    public Builder withMove3(Move move3) {
      this.move3 = move3;
      return this;
    }

    public Builder withMove4(Move move4) {
      this.move4 = move4;
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
      Pokemon pokemon = new Pokemon();

      pokemon.currHp = this.currHp;
      pokemon.maxHp = this.maxHp;
      pokemon.atk = this.atk;
      pokemon.def = this.def;
      pokemon.specAtk = this.specAtk;
      pokemon.specDef = this.specDef;
      pokemon.spd = this.spd;
      pokemon.eva = this.eva;
      pokemon.acc = this.acc;

      pokemon.exp = this.exp;

      pokemon.type = this.type;
      pokemon.type2 = this.type2;

      pokemon.move1 = this.move1;
      pokemon.move2 = this.move2;
      pokemon.move3 = this.move3;
      pokemon.move4 = this.move4;

      pokemon.nickname = this.nickname;
      pokemon.species = this.species;
      pokemon.id = this.id;
      pokemon.gender = this.gender;
      pokemon.stored = this.stored;

      return pokemon;
    }
  }

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
  private PokeTypes type;
  private PokeTypes type2;

  // Pokemon's moves. Some moves can be null
  private Move move1;
  private Move move2;
  private Move move3;
  private Move move4;

  private String nickname; // Captured Pokemon can have a nickname
  private String species; // The actual species of Pokemon
  private int id;
  private int gender;
  private boolean stored;

  // We want to only make Pokemon using the builder
  private Pokemon() {
  }

  public int getId() {
    return this.id;
  }

  /**
   * @return the currHp
   */
  public int getCurrHp() {
    return currHp;
  }

  /**
   * @param currHp
   *          the currHp to set
   */
  public void setCurrHp(int currHp) {
    this.currHp = currHp;
  }

  /**
   * @return the maxHp
   */
  public int getMaxHp() {
    return maxHp;
  }

  /**
   * @param maxHp
   *          the maxHp to set
   */
  public void setMaxHp(int maxHp) {
    this.maxHp = maxHp;
  }

  /**
   * @return the atk
   */
  public int getAtk() {
    return atk;
  }

  /**
   * @param atk
   *          the atk to set
   */
  public void setAtk(int atk) {
    this.atk = atk;
  }

  /**
   * @return the def
   */
  public int getDef() {
    return def;
  }

  /**
   * @param def
   *          the def to set
   */
  public void setDef(int def) {
    this.def = def;
  }

  /**
   * @return the specAtk
   */
  public int getSpecAtk() {
    return specAtk;
  }

  /**
   * @param specAtk
   *          the specAtk to set
   */
  public void setSpecAtk(int specAtk) {
    this.specAtk = specAtk;
  }

  /**
   * @return the specDef
   */
  public int getSpecDef() {
    return specDef;
  }

  /**
   * @param specDef
   *          the specDef to set
   */
  public void setSpecDef(int specDef) {
    this.specDef = specDef;
  }

  /**
   * @return the spd
   */
  public int getSpd() {
    return spd;
  }

  /**
   * @param spd
   *          the spd to set
   */
  public void setSpd(int spd) {
    this.spd = spd;
  }

  /**
   * @return the eva
   */
  public int getEva() {
    return eva;
  }

  /**
   * @param eva
   *          the eva to set
   */
  public void setEva(int eva) {
    this.eva = eva;
  }

  /**
   * @return the acc
   */
  public int getAcc() {
    return acc;
  }

  /**
   * @param acc
   *          the acc to set
   */
  public void setAcc(int acc) {
    this.acc = acc;
  }

  /**
   * @return the type
   */
  public PokeTypes getType() {
    return type;
  }

  /**
   * @param type
   *          the type to set
   */
  public void setType(PokeTypes type) {
    this.type = type;
  }

  /**
   * @return the type2
   */
  public PokeTypes getType2() {
    return type2;
  }

  /**
   * @param type2
   *          the type2 to set
   */
  public void setType2(PokeTypes type2) {
    this.type2 = type2;
  }

  /**
   * @return the move1
   */
  public Move getMove1() {
    return move1;
  }

  /**
   * @param move1
   *          the move1 to set
   */
  public void setMove1(Move move1) {
    this.move1 = move1;
  }

  /**
   * @return the move2
   */
  public Move getMove2() {
    return move2;
  }

  /**
   * @param move2
   *          the move2 to set
   */
  public void setMove2(Move move2) {
    this.move2 = move2;
  }

  /**
   * @return the move3
   */
  public Move getMove3() {
    return move3;
  }

  /**
   * @param move3
   *          the move3 to set
   */
  public void setMove3(Move move3) {
    this.move3 = move3;
  }

  /**
   * @return the move4
   */
  public Move getMove4() {
    return move4;
  }

  /**
   * @param move4
   *          the move4 to set
   */
  public void setMove4(Move move4) {
    this.move4 = move4;
  }

  /**
   * @return the nickname
   */
  public String getNickname() {
    return nickname;
  }

  /**
   * @param nickname
   *          the nickname to set
   */
  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  /**
   * @return the species
   */
  public String getSpecies() {
    return species;
  }

  /**
   * @param species
   *          the species to set
   */
  public void setSpecies(String species) {
    this.species = species;
  }

}
