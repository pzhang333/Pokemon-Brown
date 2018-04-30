package cs.brown.edu.aelp.pokemmo.battle.impl;

import cs.brown.edu.aelp.pokemmo.battle.Arena;
import cs.brown.edu.aelp.pokemmo.battle.Battle;
import cs.brown.edu.aelp.pokemmo.data.authentication.User;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;

public class WildBattle extends Battle {

  private final Pokemon wild;
  private final User user;

  public WildBattle(Integer id, Arena arena, User user, Pokemon wild) {
    super(id, arena);
    this.wild = wild;
    this.user = user;
  }

  public User getUser() {
    return this.user;
  }

  public Pokemon getWildPokemon() {
    return wild;
  }

}
