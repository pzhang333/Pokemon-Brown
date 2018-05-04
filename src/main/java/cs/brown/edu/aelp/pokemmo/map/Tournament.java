package cs.brown.edu.aelp.pokemmo.map;

import cs.brown.edu.aelp.pokemmo.battle.BattleManager;
import cs.brown.edu.aelp.pokemmo.data.authentication.User;
import cs.brown.edu.aelp.pokemmo.data.authentication.UserManager;
import cs.brown.edu.aelp.pokemon.Main;
import cs.brown.edu.aelp.util.JsonFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Tournament {

  private static final String CHUNK_CONFIG = "config/tournament_config.json";
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

  private static long cooldown = 0;

  public Tournament() throws IOException {
    JsonFile f = new JsonFile(CHUNK_CONFIG);
    this.size = f.getInt("size");
    this.cost = f.getInt("cost");
    assert size == 4 || size == 8 || size == 16 || size == 32;
    this.chunk = Main.getWorld().loadChunk(Chunk.getNextDynamicId(),
        CHUNK_FILE);
    JsonFile ent_start = f.getMap("entrance", "location");
    JsonFile ent_goto = f.getMap("entrance", "goto");
    Location entranceLoc = new Location(
        Main.getWorld().getChunk(ent_start.getInt("chunk")),
        ent_start.getInt("row"), ent_start.getInt("col"));
    Location entranceGoto = new Location(this.chunk, ent_goto.getInt("row"),
        ent_goto.getInt("col"));
    Portal entrance = new Portal(entranceLoc, entranceGoto);
    JsonFile exit_start = f.getMap("exit", "location");
    JsonFile exit_goto = f.getMap("exit", "goto");
    Location exitLoc = new Location(this.chunk, exit_start.getInt("row"),
        exit_start.getInt("col"));
    Location exitGoto = new Location(
        Main.getWorld().getChunk(exit_goto.getInt("chunk")),
        exit_goto.getInt("row"), exit_goto.getInt("col"));
    Portal exit = new Portal(exitLoc, exitGoto);
    entrance.getLocation().getChunk().addEntity(entrance);
    this.chunk.addEntity(exit);
    this.entrance = entrance;
    this.exit = exit;
    for (User u : UserManager.getAllUsers()) {
      if (u.isConnected()) {
        u.sendMessage(
            "A tournament is now open! Head to the arena north of spawn to participate.");
      }
    }
    Tournament.cooldown = System.currentTimeMillis() + (15 * 60 * 1000);
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
    u.sendMessage(String
        .format("You have paid %d coins to join the tournament.", this.cost));
    if (this.users.size() == this.size) {
      this.start();
    }
  }

  public void removeUser(User u) {
    this.users.remove(u);
    u.teleportTo(this.exit.getGoTo());
    if (!this.locked) {
      this.pool -= this.cost;
      u.setCurrency(u.getCurrency() + this.cost);
      u.sendMessage(String.format(
          "You have been refunded %d coins for leaving the tournament before it started.",
          this.cost));
    }
    if (this.users.size() == 1 && this.locked) {
      this.end();
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
    new Timer().schedule(new TimerTask() {
      @Override
      public void run() {
        if (!Tournament.this.users.isEmpty()) {
          Tournament.this.startBattles();
        }
      }
    }, delay * 1000);
  }

  public void startBattles() {
    Set<User> inBattle = new HashSet<>();
    for (User u : this.users) {
      if (inBattle.contains(u)) {
        continue;
      }
      if (this.bracket.containsKey(u) && this.bracket.get(u) != null)
        BattleManager.getInstance().createPvPBattle(u, this.bracket.get(u));
      inBattle.add(u);
      inBattle.add(this.bracket.get(u));
    }
  }

  public void setupBracket() {
    System.out.println("Setting up bracket");
    int highestSeed = -1;
    if (this.seeds.isEmpty()) {
      for (int i = 0; i < this.users.size(); i++) {
        this.seeds.put(i + 1, this.users.get(i));
        highestSeed = i + 1;
      }
      System.out.println("Did original ELO seeding");
    } else {
      for (int i = 1; i <= this.size; i++) {
        System.out.println("Processing new seed: " + i);
        if (!this.seeds.containsKey(i)) {
          System.out.println("New seed didn't exist?");
          break;
        }
        User u = this.seeds.get(i);
        System.out.println("Seed used to be: " + u.getUsername());
        if (this.bracket.get(u) != null
            && this.users.contains(this.bracket.get(u))) {
          u = this.bracket.get(u);
        }
        System.out.println("Seed is now: " + u.getUsername());
        this.seeds.put(i, u);
        highestSeed = i;
      }
    }
    assert highestSeed != -1;
    this.bracket.clear();
    for (int seed : this.seeds.keySet()) {
      if (seed > highestSeed) {
        break;
      }
      System.out.printf("Seed %d: %s%n", seed,
          this.seeds.get(seed).getUsername());
      int toPlay = highestSeed + 1 - seed;
      System.out.println("is going to play seed: " + toPlay);
      if (this.seeds.containsKey(toPlay)) {
        this.bracket.put(this.seeds.get(seed), this.seeds.get(toPlay));
      } else {
        this.bracket.put(this.seeds.get(seed), null);
      }
    }
    this.size /= 2;
  }

  public void start() {
    this.locked = true;
    this.users.sort(new EloComparator());
    this.setupBracket();
    this.queueNextRound(30);
  }

  public void logBattleResult(User winner, User loser) {
    this.removeUser(loser);
    loser.sendMessage(
        "You've been eliminated from the tournament, better luck next time!");
    if (!this.users.isEmpty()) {
      winner
          .sendMessage("Congratulations! You have advanced to the next round.");
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
  }

  public boolean isParticipating(User u) {
    return this.users.contains(u);
  }

  public void end() {
    if (this.users.size() == 1 && this.locked) {
      User winner = this.users.get(0);
      winner.setCurrency(winner.getCurrency() + this.pool);
      winner.sendMessage(String.format(
          "Congratulations! You won the tournament and earned the pool of %d coins.",
          this.pool));
    }
    List<User> temp = new ArrayList<>(this.users);
    for (User u : temp) {
      this.removeUser(u);
    }
    this.entrance.remove();
    Main.getWorld().removeChunk(this.chunk);
    Main.getWorld().setTournament(null);
  }

  public static void startGenerator() {
    ScheduledExecutorService timer = Executors
        .newSingleThreadScheduledExecutor();

    Runnable trySpawn = new Runnable() {
      @Override
      public void run() {
        if (Tournament.cooldown > System.currentTimeMillis()) {
          return;
        }
        World w = Main.getWorld();
        if (w.getTournament() != null) {
          return;
        }
        if (new Random().nextDouble() < 0.15) {
          try {
            Tournament t = new Tournament();
            w.setTournament(t);
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    };

    timer.scheduleAtFixedRate(trySpawn, 60 * 1000, 60 * 1000,
        TimeUnit.MILLISECONDS);
  }

  private static class EloComparator implements Comparator<User> {

    @Override
    public int compare(User u1, User u2) {
      int cmp = Integer.compare(u1.getElo(), u2.getElo());
      if (cmp == 0) {
        return Integer.compare(u1.getId(), u2.getId());
      }
      return cmp;
    }
  }

}
