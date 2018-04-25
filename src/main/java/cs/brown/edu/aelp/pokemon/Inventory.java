package cs.brown.edu.aelp.pokemon;

import cs.brown.edu.aelp.pokemmo.data.SQLBatchSavable;
import cs.brown.edu.aelp.pokemmo.data.authentication.User;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class Inventory implements SQLBatchSavable {

  private final User owner;
  private final HashMap<Integer, Integer> items = new HashMap<>();

  public Inventory(User owner) {
    this.owner = owner;
  }

  public void setItemAmount(int id, int amount) {
    items.put(id, amount);
  }

  public int addItems(int id, int amount) {
    items.put(id, items.getOrDefault(id, 0));
    return items.get(id);
  }

  public int removeItems(int id, int amount) {
    assert items.containsKey(id);
    assert items.get(id) >= amount;
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
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<String> getIdentifyingColumns() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getTableName() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void bindValues(PreparedStatement p) throws SQLException {
    // TODO Auto-generated method stub

  }

  @Override
  public boolean hasUpdates() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void setChanged(boolean b) {
    // TODO Auto-generated method stub

  }

}
