package cs.brown.edu.aelp.pokemon;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import cs.brown.edu.aelp.pokemmo.data.SQLBatchSavable;
import cs.brown.edu.aelp.pokemmo.data.authentication.User;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class Inventory implements SQLBatchSavable {

  // ids:
  // 0 pokeballs
  // 1 great balls
  // 2 ultra balls
  // 3 master balls
  // 4 hyper potions
  // 5 full restore

  private final User owner;
  private final HashMap<Integer, Integer> items = new HashMap<>();
  private boolean changed = false;

  public Inventory(User owner) {
    this.owner = owner;
  }

  public void setItemAmount(int id, int amount) {
    items.put(id, amount);
    this.changed = true;
  }

  public int addItems(int id, int amount) {
    items.put(id, items.getOrDefault(id, 0));
    this.changed = true;
    return items.get(id);
  }

  public int removeItems(int id, int amount) {
    assert items.containsKey(id);
    assert items.get(id) >= amount;
    this.changed = true;
    if (items.get(id) == amount) {
      items.remove(id);
      return 0;
    }
    items.put(id, items.get(id) - amount);
    return items.get(id);
  }

  public int getItemAmount(int id) {
    return items.getOrDefault(id, 0);
  }

  @Override
  public List<String> getUpdatableColumns() {
    return Lists.newArrayList("amount");
  }

  @Override
  public List<String> getIdentifyingColumns() {
    return Lists.newArrayList("user_id", "item_id");
  }

  @Override
  public String getTableName() {
    return "inventories";
  }

  @Override
  public void bindValues(PreparedStatement p) throws SQLException {
    for (int item : this.items.keySet()) {
      p.setInt(1, this.items.get(item));
      p.setInt(2, this.owner.getId());
      p.setInt(3, item);
      p.addBatch();
    }
  }

  @Override
  public boolean hasUpdates() {
    return this.changed;
  }

  @Override
  public void setChanged(boolean b) {
    this.changed = false;
  }

  @Override
  public boolean useUpsert() {
    return true;
  }

  public static class InventoryAdapter implements JsonSerializer<Inventory> {

    @Override
    public JsonElement serialize(Inventory src, Type typeOfSrc,
        JsonSerializationContext ctx) {
      return Main.GSON().toJsonTree(src.items);
    }

  }

}
