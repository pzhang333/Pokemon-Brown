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

  public static int getCost(ItemType t) {
    if (t == ItemType.POKEBALL) {
      return 50;
    } else if (t == ItemType.MASTER_BALL) {
      return 750;
    } else if (t == ItemType.FULL_RESTORE) {
      return 300;
    } else if (t == ItemType.ETHER) {
      return 200;
    } else if (t == ItemType.OVERLOAD) {
      return 150;
    } else if (t == ItemType.LASSO) {
      return 250;
    }
    throw new IllegalArgumentException("Unknown item");
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
