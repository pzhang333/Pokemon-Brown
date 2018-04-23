package cs.brown.edu.aelp.pokemmo.data;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public interface SQLBatchSavable {

  // It makes more sense for these first 2 methods to be static, but that's not
  // really possible with interfaces like this.

  /**
   * Get a list of column names that can be updated for an object of this type.
   * 
   * @return a list of Strings
   */
  public List<String> getUpdatableColumns();

  /**
   * Return the name of the table this object should be saved to.
   *
   * @return the table name
   */
  public String getTableName();

  /**
   * Bind the appropriate values to the given PreparedStatement for an update.
   *
   * @param p
   *          the PreparedStatement to bind values to
   */
  public void bindValues(PreparedStatement p) throws SQLException;

  /**
   * Get whether or not this object needs to be updated.
   *
   * @return a boolean
   */
  public boolean hasUpdates();

}
