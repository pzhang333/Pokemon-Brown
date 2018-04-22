package cs.brown.edu.aelp.pokemmo.data;

import java.util.Map;

public interface BatchSavable {

  /**
   * Get all changes to be saved for this object. This method MUST wait for the
   * lock on the changes map and clear the map before releasing it.
   * 
   * @return the map of changes to be saved
   */
  public Map<String, Object> getChangesForSaving();

}
