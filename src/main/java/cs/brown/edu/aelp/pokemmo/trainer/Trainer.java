package cs.brown.edu.aelp.pokemmo.trainer;

import java.util.ArrayList;
import java.util.List;

import cs.brown.edu.aelp.pokemmo.battle.EffectSlot;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;

public class Trainer {

  private final String id;

  private List<Pokemon> team = new ArrayList<>();

  private Pokemon activePokemon = null;

  private EffectSlot effectSlot = new EffectSlot();

  public Trainer(String id) {
    this.id = id;
  }

  public boolean addPokemonToTeam(Pokemon pokemon) {
    if (team.size() >= 6) {
      return false;
    }

    if (team.isEmpty()) {
      activePokemon = pokemon;
    }

    team.add(pokemon);
    return true;
  }

  public Pokemon getActivePokemon() {
    return activePokemon;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Trainer other = (Trainer) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    return true;
  }

  public String getId() {
    return id;
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

}
