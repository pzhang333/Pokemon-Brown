package cs.brown.edu.aelp.pokemmo.data.authentication;

import java.util.HashMap;
import java.util.Map;

import cs.brown.edu.aelp.util.Pair;

public final class UserManager {

  private static final Map<Pair<String, String>, User> users = new HashMap<>();

  private static final Map<Integer, User> userIdMap = new HashMap<>();

  private UserManager() {
  }

  public User authenticateWithToken(String username, String token) {
    Pair<String, String> pair = new Pair<>(username, token);
    if (users.containsKey(pair)) {
      return users.get(pair);
    } else {
      // TODO: Lookup this username/token pair in database
    }
    return null;
  }

  public User authenticateWithPassword(String username, String pass) {
    // TODO: Lookup this username/pass pair in database
    // TODO: Generate token for this user and store in database
    return null;
  }

  public void addUser(User user) {
    userIdMap.put(user.getId(), user);
  }

}
