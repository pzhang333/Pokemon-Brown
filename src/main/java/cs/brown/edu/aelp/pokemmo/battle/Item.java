package cs.brown.edu.aelp.pokemmo.battle;

import cs.brown.edu.aelp.pokemon.Inventory;

public class Item {

  public static enum ItemType {
    POKEBALL,
    MASTER_BALL,
    OVERLOAD,
    FULL_RESTORE,
    ETHER,
    LASSO
  }

  private final int id;

  public Item(int id) {
    this.id = id;
  }

  public boolean isPokeball() {
    ItemType type = getType();

    return (type == ItemType.POKEBALL) || (type == ItemType.MASTER_BALL);
  }

  public ItemType getType() {
    return ItemType.values()[id];
  }

  public int getId() {
    return id;
  }

  public void removeFromInventory(Inventory inventory) {
    int amt = inventory.getItemAmount(getId());
    if (amt > 0) {
      inventory.setItemAmount(getId(), amt - 1);
    }
  }

}
