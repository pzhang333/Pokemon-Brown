package cs.brown.edu.aelp.pokemon.trainer;

import java.util.ArrayList;
import java.util.List;

import cs.brown.edu.aelp.pokemon.Pokemon;

public class Trainer {

  public List<Pokemon> pokemon = new ArrayList<>(6);

  private Inventory inventory = new Inventory();

  public Pokemon activePokemon = null;

  public Pokemon getActivePokemon() {
    return activePokemon;
  }

  public Inventory getInventory() {
    return inventory;
  }
}
