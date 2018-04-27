package cs.brown.edu.aelp.pokemmo.data;

import cs.brown.edu.aelp.pokemmo.data.authentication.User;
import cs.brown.edu.aelp.util.Identifiable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

public class Leaderboards {

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

  public static void setTop50(Collection<EloUser> users) {
    assert users.size() <= 50;
    scores.addAll(users);
  }

  public static void tryInsertTop50(User u) {
    EloUser eu = new EloUser(u.getId(), u.getUsername(), u.getElo());
    tryInsertTop50(eu);
  }

  public static void tryInsertTop50(EloUser eu) {
    if (scores.size() < 50 || scores.last().getElo() < eu.getElo()) {
      scores.add(eu);
      scores.remove(scores.last());
    }
  }

  public static SortedSet<EloUser> getTop50() {
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

  }

}
