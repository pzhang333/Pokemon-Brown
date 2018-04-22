package cs.brown.edu.aelp.pokemmo.data.authentication;

import cs.brown.edu.aelp.pokemmo.data.DataSource;
import cs.brown.edu.aelp.pokemmo.data.DataSource.AuthException;
import cs.brown.edu.aelp.pokemon.Main;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class UserManager {

  private static final Map<String, User> users = new HashMap<>();
  private static DataSource data = Main.getDataSource();

  private UserManager() {
  }

  /**
   * Attempt to authenticate a user with either a password or a token. If a
   * token, we may be able to authenticate them from memory. If this
   * authentication involves a User connecting to the game, be sure to use
   * .setConnected(true) on the returned User.
   *
   * @param username
   *          username
   * @param pass
   *          password or token
   * @return the User
   * @throws AuthException
   *           if something goes wrong
   */
  public static User authenticate(String username, String pass)
      throws AuthException {
    if (users.containsKey(username) && users.get(username).getToken() == pass) {
      return users.get(username);
    } else {
      User user = data.authenticateUser(username, pass);
      // if we still know about them, use their info from memory, not disk!
      if (users.containsKey(username)) {
        return users.get(username);
      }
      users.put(username, user);
      return user;
    }
  }

  /**
   * Remove all disconnected Users from memory. This should never be called
   * unless by the saving thread, immediately after a save!
   */
  public static void purgeDisconnectedUsers() {
    for (String s : users.keySet()) {
      if (!users.get(s).isConnected()) {
        users.remove(s);
      }
    }
  }

  /**
   * Returns a collection of all Users that are either currently connected or
   * have disconnected but not yet been saved.
   *
   * @return an unmodifiable collection of Users
   */
  public static Collection<User> getAllUsers() {
    return Collections.unmodifiableCollection(users.values());
  }

}
