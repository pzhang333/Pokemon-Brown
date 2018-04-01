package cs.brown.edu.aelp.pokemon.data;

public interface DataSource {

  // TO DO: Returns a "User" type, but that class doesn't exist yet.
  /**
   * Authenticates user credentials, returning a User fully populated with
   * Pokemon and Inventory data if successful. On failure, throws an exception
   * with a front-end appropriate message.
   *
   * @param email
   *          the email of the user trying to login
   * @param password
   *          the password they gave
   */
  public void authenticateUser(String email, String password) throws Exception;

  // TO DO: Returns a "User" type.
  /**
   * Attempts to register a new user with the given information, returning a
   * User on success. On failure, throws an exception with a front-end
   * appropriate message.
   * 
   * @param username
   *          the potential user's chosen name
   * @param email
   *          the potential user's email
   * @param password
   *          the potential user's password
   * @return
   */
  public void registerUser(String username, String email, String password)
      throws Exception;

}
