package cs.brown.edu.aelp.pokemmo.battle;

public class Item {

  public static enum ItemType {
    POKEBALL, MASTER_BALL, OVERLOAD, FULL_RESTORE
  }

  private final ItemType type;

  public Item(int id) {

    ItemType[] itemTypes = ItemType.values();

    if (id < 0 || id >= itemTypes.length) {
      throw new IllegalArgumentException("Invalid item id");
    }

    this.type = itemTypes[id];
  }

  public boolean isPokeball() {
    return (type == ItemType.POKEBALL) && (type == ItemType.MASTER_BALL);
  }

  public ItemType getType() {
    return type;
  }

}
