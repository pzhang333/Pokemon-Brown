package cs.brown.edu.aelp.pokemon.data;

import cs.brown.edu.aelp.pokemon.data.authentication.User;

public interface DataSource {

  /**
   * Authenticates a user by name, returning a User fully populated with Pokemon
   * and Inventory data if successful. On failure, throws an exception with a
   * front-end appropriate message.
   *
   * @param email
   *          the email of the user trying to login
   * @param password
   *          either a password or session token
   */
  public User authenticateUser(String username, String password)
      throws AuthException;

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
  public User registerUser(String username, String email, String password)
      throws AuthException;

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

}
