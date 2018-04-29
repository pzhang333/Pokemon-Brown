package cs.brown.edu.aelp.pokemmo.battle.impl;

import cs.brown.edu.aelp.pokemmo.battle.Arena;
import cs.brown.edu.aelp.pokemmo.battle.Battle;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;
import cs.brown.edu.aelp.pokemmo.trainer.Trainer;

public class WildBattle extends Battle {

  private final Pokemon wild;

  public WildBattle(Integer id, Arena arena, Trainer trainer, Pokemon wild) {
    super(id, arena);

    this.wild = wild;
  }

  public Pokemon getWildPokemon() {
    return wild;
  }

  @Override
  public BattleType getBattleType() {
    return BattleType.WILD_BATTLE;
  }
}
