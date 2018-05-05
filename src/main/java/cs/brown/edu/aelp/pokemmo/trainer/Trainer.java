package cs.brown.edu.aelp.pokemmo.trainer;

import cs.brown.edu.aelp.pokemmo.battle.Battle;
import cs.brown.edu.aelp.pokemmo.battle.Battle.BattleState;
import cs.brown.edu.aelp.pokemmo.battle.EffectSlot;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;
import cs.brown.edu.aelp.util.Identifiable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Trainer extends Identifiable {

  private final Map<Integer, Pokemon> pokemonMap = new HashMap<>();

  private Pokemon activePokemon = null;

  private EffectSlot effectSlot = new EffectSlot();

  private Battle currentBattle = null;

  public Trainer(Integer id) {
    super(id);
  }

  public Pokemon getActivePokemon() {
    // Returns a Pokemon that isn't knocked out
    if (activePokemon.isKnockedOut()) {
      for (Pokemon p : pokemonMap.values()) {
        if (!p.isKnockedOut()) {
          activePokemon = p;
        }
      }
    }
    return activePokemon;
  }

  public EffectSlot getEffectSlot() {
    return effectSlot;
  }

  public List<Pokemon> getTeam() {
    return new ArrayList<>(pokemonMap.values());
  }

  public boolean allPokemonKnockedOut() {
    for (Pokemon p : getTeam()) {
      if (!p.isKnockedOut()) {
        return false;
      }
    }

    return true;
  }

  public Pokemon getPokemonById(Integer id) {
    return pokemonMap.get(id);
  }

  public void setActivePokemon(Pokemon pokemonIn) {
    if (!pokemonMap.containsKey(pokemonIn.getId())) {
      throw new IllegalArgumentException("Pokemon not in team!");
    }
    activePokemon = pokemonIn;
  }

  public void addPokemonToTeam(Pokemon p) {

    if (pokemonMap.isEmpty()) {
      activePokemon = p;
    }

    pokemonMap.put(p.getId(), p);
  }

  public boolean isInBattle() {
    return (currentBattle != null)
        && !(currentBattle.getBattleState().equals(BattleState.DONE));
  }

  public Battle getCurrentBattle() {
    return currentBattle;
  }

  public void setCurrentBattle(Battle currentBattle) {
    this.currentBattle = currentBattle;
  }

  public void emptyTeam() {
    this.pokemonMap.clear();
  }

}
