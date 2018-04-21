package cs.brown.edu.aelp.pokemmo.trainer;

import cs.brown.edu.aelp.pokemmo.battle.EffectSlot;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;
import cs.brown.edu.aelp.util.Identifiable;
import java.util.ArrayList;
import java.util.List;

public class Trainer extends Identifiable {

  private List<Pokemon> team = new ArrayList<>();

  private Pokemon activePokemon = null;

  private EffectSlot effectSlot = new EffectSlot();

  public Trainer(Integer id) {
    super(id);
  }

  public Pokemon getActivePokemon() {
    return activePokemon;
  }

  public EffectSlot getEffectSlot() {
    return effectSlot;
  }

  public List<Pokemon> getTeam() {
    return team;
  }

  public boolean allPokemonKnockedOut() {
    for (Pokemon p : getTeam()) {
      if (!p.isKnockedOut()) {
        return false;
      }
    }

    return true;
  }

  public void setActivePokemon(Pokemon pokemonIn) {
    if (!team.contains(pokemonIn)) {
      throw new IllegalArgumentException("Pokemon not in team!");
    }
    activePokemon = pokemonIn;
  }

  public void addPokemonToTeam(Pokemon p) {

    if (team.isEmpty()) {
      activePokemon = p;
    }

    team.add(p);
  }
}
