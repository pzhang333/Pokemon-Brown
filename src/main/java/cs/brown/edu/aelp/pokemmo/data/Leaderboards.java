package cs.brown.edu.aelp.pokemmo.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import cs.brown.edu.aelp.pokemmo.data.authentication.User;
import cs.brown.edu.aelp.util.Identifiable;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

public class Leaderboards {

  private static boolean changed = false;

  private static final SortedSet<EloUser> scores = new TreeSet<>(
      new Comparator<EloUser>() {
        @Override
        public int compare(EloUser u1, EloUser u2) {
          int cmp = (-1) * Integer.compare(u1.getElo(), u2.getElo());
          if (cmp == 0) {
            return Integer.compare(u1.getId(), u2.getId());
          }
          return cmp;
        }
      });

  public static void setTop5(Collection<EloUser> users) {
    assert users.size() <= 5;
    scores.addAll(users);
    changed = true;
  }

  public static void tryInsertTop5(User u) {
    EloUser eu = new EloUser(u.getId(), u.getUsername(), u.getElo());
    tryInsertTop5(eu);
  }

  public static boolean isChanged() {
    if (changed) {
      changed = false;
      return true;
    }
    return false;
  }

  public static void tryInsertTop5(EloUser eu) {
    if (scores.size() < 5 || scores.last().getElo() < eu.getElo()) {
      scores.add(eu);
      scores.remove(scores.last());
      changed = true;
    }
  }

  public static SortedSet<EloUser> getTop5() {
    return Collections.unmodifiableSortedSet(scores);
  }

  public static class EloUser extends Identifiable {

    private final String username;
    private final int elo;

    public EloUser(int id, String username, int elo) {
      super(id);
      this.username = username;
      this.elo = elo;
    }

    public int getElo() {
      return this.elo;
    }

    public String getUsername() {
      return this.username;
    }

    public static class EloUserAdapter implements JsonSerializer<EloUser> {

      @Override
      public JsonElement serialize(EloUser src, Type typeOfSrc,
          JsonSerializationContext ctx) {
        JsonObject o = new JsonObject();
        o.addProperty("id", src.getId());
        o.addProperty("username", src.getUsername());
        o.addProperty("elo", src.getElo());
        return o;
      }

    }

  }

}
