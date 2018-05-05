package cs.brown.edu.aelp.pokemmo.trainer;

import cs.brown.edu.aelp.pokemmo.battle.Battle;
import cs.brown.edu.aelp.pokemmo.battle.Battle.BattleState;
import cs.brown.edu.aelp.pokemmo.battle.EffectSlot;
import cs.brown.edu.aelp.pokemmo.data.authentication.User;
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
    return this.activePokemon;
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

  public Pokemon getPokemonById(int id) {
    if (pokemonMap.containsKey(id)) {
      return pokemonMap.get(id);
    }
    return null;
  }

  public void setActivePokemon(Pokemon pokemonIn) {
    activePokemon = pokemonIn;
  }

  public void addPokemonToTeam(Pokemon p) {

    if (pokemonMap.isEmpty()) {
      this.setActivePokemon(p);
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
    for (Pokemon p : this.pokemonMap.values()) {
      p.setStored(true);
      if (this instanceof User) {
        ((User) this).addInactivePokemon(p);
      }
    }
    this.pokemonMap.clear();
  }

}
