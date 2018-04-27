package cs.brown.edu.aelp.pokemmo.map;

import cs.brown.edu.aelp.pokemmo.data.authentication.User;
import cs.brown.edu.aelp.pokemon.Main;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tournament {

  private static final String PATH = World.DEFAULT_CHUNK_PATH
      + "/tournament.json";

  private final int size;
  private final Chunk chunk;
  private final List<User> users = new ArrayList<>();
  private boolean locked = false;
  private final int cost;
  private int pool = 0;
  private Map<User, User> bracket = new HashMap<>();
  private Portal portal;
  private final Location exit;

  public Tournament(int size, int cost, Location exit) throws IOException {
    this.size = size;
    this.cost = cost;
    this.exit = exit;
    this.chunk = Main.getWorld().loadChunk(Chunk.getNextDynamicId(),
        new File(PATH));
  }

  public void setPortal(Portal p) {
    this.portal = p;
  }

  public void joinUser(User u) {
    assert this.users.size() < this.size;
    u.setCurrency(u.getCurrency() - this.cost);
    this.pool += this.cost;
    this.users.add(u);
  }

  public void removeUser(User u) {
    this.users.remove(u);
    u.teleportTo(this.exit);
  }

  public Chunk getChunk() {
    return this.chunk;
  }

  public boolean canJoin(User u) {
    return !this.locked && this.users.size() < this.size
        && u.getCurrency() >= this.cost;
  }

  public void start() {
    this.locked = true;
    this.users.sort(new EloComparator());
    int byes = (int) Math.pow(2,
        Math.ceil(Math.log(this.users.size()) / Math.log(2)));
    int i = 0;
    for (User u : this.users) {
      if (byes > 0) {
        this.bracket.put(u, null);
        byes--;
      } else {
        User u1 = this.users.get(i);
        User u2 = this.users.get(this.users.size() - 1 - i + byes);
        this.bracket.put(u1, u2);
      }
      i++;
    }
    // TODO: Put people into battles
  }

  public void logBattleResult(User winner, User loser) {
    this.bracket.remove(winner);
    this.bracket.remove(loser);
    this.removeUser(loser);
    if (this.users.size() == 1) {
      winner.setCurrency(winner.getCurrency() + this.pool);
      this.removeUser(winner);
      return;
    }
    if (this.bracket.isEmpty()) {
      // TODO: Start the next round
    }
  }

  public void end() {
    for (User u : this.users) {
      this.removeUser(u);
    }
    this.portal.getLocation().getChunk().removeEntity(this.portal);
  }

  private static class EloComparator implements Comparator<User> {

    @Override
    public int compare(User u1, User u2) {
      int cmp = (-1) * Integer.compare(u1.getElo(), u2.getElo());
      if (cmp == 0) {
        return Integer.compare(u1.getId(), u2.getId());
      }
      return cmp;
    }
  }

}
