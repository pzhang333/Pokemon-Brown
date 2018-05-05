package cs.brown.edu.aelp.pokemmo.battle.summaries;

import cs.brown.edu.aelp.pokemmo.battle.BattleSummary;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;

public class SwitchSummary extends BattleSummary {

  private final Pokemon pokemonIn;

  private final Pokemon pokemonOut;

  public SwitchSummary(Pokemon pokeIn, Pokemon pokeOut) {
    super(SummaryType.SWITCH,
        String.format("%s (%s) swapped in for %s (%s).", pokeIn.getNickname(),
            pokeIn.getSpecies(), pokeOut.getNickname(), pokeOut.getSpecies()));

    this.pokemonIn = pokeIn.snapshot();
    this.pokemonOut = pokeOut.snapshot();
  }

}
