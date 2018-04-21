package cs.brown.edu.aelp.pokemmo.data;

import java.util.Map;

public interface BatchSavable {

  public Map<String, Object> getChanges();

  public void clearChanges();

}
