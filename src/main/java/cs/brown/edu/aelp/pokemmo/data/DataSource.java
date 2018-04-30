package cs.brown.edu.aelp.pokemmo.data;

import cs.brown.edu.aelp.pokemmo.data.authentication.User;

public interface DataSource {

  /**
   * Authenticates a user by name and password, returning a User fully populated
   * with Pokemon and Inventory data if successful. If successful, this user's
   * old token is expired and a new one is generated. On failure, throws an
   * exception with a front-end appropriate message. Don't call this directly;
   * use UserManager instead.
   *
   * @param username
   *          the username of the user trying to login
   * @param password
   *          the password
   */
  public User authenticateUser(String username, String password)
      throws AuthException;

  /**
   * Authenticates a user by id and token, returning a User fully populated with
   * Pokemon and Inventory data if successful. On failure, throws an exception
   * with a front-end appropriate message. Don't call this directly; use
   * UserManager instead.
   *
   * @param id
   *          the id of the user trying to login
   * @param token
   *          the token
   */
  public User authenticateUser(int id, String token) throws AuthException;

  /**
   * Attempts to register a new user with the given information, returning a
   * User on success. On failure, throws an exception with a front-end
   * appropriate message. Don't call this directly; use UserManager instead.
   * 
   * @param username
   *          the potential user's chosen name
   * @param email
   *          the potential user's email
   * @param password
   *          the potential user's password
   * @return
   */
  public User registerUser(String username, String email, String password,
      String species, String nickname) throws AuthException;

  /**
   * Attempts to load the top 50 elos into the leaderboards.
   */
  public void loadLeaderboards() throws LoadException;

  /**
   * Type of Exception thrown when something goes wrong during authentication.
   * Message should be appropriate for user-facing display.
   *
   * @author Louis Kilfoyle
   */
  class AuthException extends Exception {

    // auto-generated
    private static final long serialVersionUID = -3978874631066975397L;

    public AuthException() {
      super("Something went wrong. Please try again later.");
    }

    public AuthException(String message) {
      super(message);
    }

  }

  class SaveException extends Exception {

    private static final long serialVersionUID = -7811762281433575563L;

    public SaveException() {
      super("ERROR: Something went wrong while saving to the database.");
    }

    public SaveException(String message) {
      super(message);
    }

  }

  class LoadException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 118911357495970128L;

    public LoadException() {
      super("ERROR: Something went wrong while loading from the databases.");
    }

    public LoadException(String message) {
      super(message);
    }

  }

}
