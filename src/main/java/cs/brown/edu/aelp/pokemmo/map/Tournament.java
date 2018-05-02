package cs.brown.edu.aelp.pokemmo.map;

import cs.brown.edu.aelp.pokemmo.data.authentication.User;
import cs.brown.edu.aelp.pokemon.Main;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tournament {

  private static final String CHUNK_FILE = "tournament.json";

  private int size;
  private final Chunk chunk;
  private final List<User> users = new ArrayList<>();
  private boolean locked = false;
  private final int cost;
  private int pool = 0;
  private Map<User, User> bracket = new HashMap<>();
  private Map<Integer, User> seeds = new HashMap<>();
  private Portal entrance;
  private Portal exit;
  private int round = 1;

  public Tournament(int size, int cost, Location entrance, Location exit)
      throws IOException {
    assert size == 4 || size == 8 || size == 16 || size == 32;
    this.size = size;
    this.cost = cost;
    this.chunk = Main.getWorld().loadChunk(Chunk.getNextDynamicId(),
        CHUNK_FILE);
    // TODO: align these with the actual chunk
    Portal p = new Portal(entrance, new Location(this.chunk, 2, 2));
    Portal p1 = new Portal(new Location(this.chunk, 4, 0), exit);
    entrance.getChunk().addEntity(p);
    this.chunk.addEntity(p1);
    this.entrance = p;
    this.exit = p1;
  }

  public Portal getExit() {
    return this.exit;
  }

  public Portal getEntrance() {
    return this.entrance;
  }

  public void addUser(User u) {
    assert this.users.size() < this.size;
    u.setCurrency(u.getCurrency() - this.cost);
    this.pool += this.cost;
    this.users.add(u);
  }

  public void removeUser(User u) {
    this.users.remove(u);
    if (!this.locked) {
      this.pool -= this.cost;
      u.setCurrency(u.getCurrency() + this.cost);
    }
  }

  public Chunk getChunk() {
    return this.chunk;
  }

  public boolean canJoin(User u) {
    return !this.locked && this.users.size() < this.size
        && u.getCurrency() >= this.cost;
  }

  public String whyCantJoin(User u) {
    if (this.locked) {
      return "The tournament has already started.";
    } else if (u.getCurrency() < this.cost) {
      return String.format(
          "You need at least %d coins to enter the tournament.", this.cost);
    }
    return "You cannot join the tournament right now.";
  }

  public void queueNextRound(int delay) {
    for (User u : this.users) {
      if (this.bracket.get(u) != null) {
        User opp = this.bracket.get(u);
        u.sendMessage(String.format(
            "Round %d will begin in %d seconds. Your opponent is %s with an elo of %d.",
            this.round, delay, opp.getUsername(), opp.getElo()));
      } else {
        u.sendMessage(String.format(
            "Round %d will begin in %d seconds, but your opponent already forfeited. "
                + "The next round will begin when all battles are complete.",
            this.round, delay));
      }
    }
    // TODO: Start a delayed task that will put people into battles
  }

  public void setupBracket() {
    if (this.seeds.isEmpty()) {
      for (int i = 0; i < this.users.size(); i++) {
        this.seeds.put(i + 1, this.users.get(i));
      }
    } else {
      for (int i = 1; i <= this.size / 2; i++) {
        if (!this.seeds.containsKey(i)) {
          break;
        }
        User u = this.seeds.get(i);
        if (this.bracket.get(u) != null
            && this.users.contains(this.bracket.get(u))) {
          u = this.bracket.get(u);
        }
        this.seeds.put(i, u);
      }
    }
    this.size /= 2;
    this.bracket.clear();
    for (int seed : this.seeds.keySet()) {
      int toPlay = this.size + 1 - seed;
      if (this.seeds.containsKey(toPlay)) {
        this.bracket.put(this.seeds.get(seed), this.seeds.get(toPlay));
      } else {
        this.bracket.put(this.seeds.get(seed), null);
      }
    }
  }

  public void start() {
    this.locked = true;
    this.users.sort(new EloComparator());
    this.setupBracket();
    this.queueNextRound(30);
  }

  public void logBattleResult(User winner, User loser) {
    this.removeUser(loser);
    if (this.users.size() == 1) {
      winner.setCurrency(winner.getCurrency() + this.pool);
      winner.sendMessage(String.format(
          "Congratulations! You won the tournament and earned the pool of %d coins.",
          this.pool));
      this.end();
      return;
    }
    boolean done = true;
    for (User u : this.users) {
      if (this.bracket.containsKey(u)
          && this.users.contains(this.bracket.get(u))) {
        done = false;
      }
    }
    if (done) {
      this.setupBracket();
      this.round++;
      this.queueNextRound(60);
    }
  }

  public void end() {
    for (User u : this.users) {
      this.removeUser(u);
      u.teleportTo(this.exit.getGoTo());
    }
    this.entrance.remove();
    Main.getWorld().removeChunk(this.chunk);
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
