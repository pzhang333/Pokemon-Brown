package cs.brown.edu.aelp.pokemon;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cs.brown.edu.aelp.commands.BattleCommand;
import cs.brown.edu.aelp.commands.CoinsCommand;
import cs.brown.edu.aelp.commands.CommandHandler;
import cs.brown.edu.aelp.commands.HealTeam;
import cs.brown.edu.aelp.commands.ItemCommand;
import cs.brown.edu.aelp.commands.TeleportCommand;
import cs.brown.edu.aelp.commands.TournamentCommand;
import cs.brown.edu.aelp.networking.PacketSender;
import cs.brown.edu.aelp.networking.PlayerWebSocketHandler;
import cs.brown.edu.aelp.networking.Trade;
import cs.brown.edu.aelp.pokemmo.data.DataSource;
import cs.brown.edu.aelp.pokemmo.data.DataSource.LoadException;
import cs.brown.edu.aelp.pokemmo.data.DataSource.SaveException;
import cs.brown.edu.aelp.pokemmo.data.Leaderboards;
import cs.brown.edu.aelp.pokemmo.data.SQLDataSource;
import cs.brown.edu.aelp.pokemmo.data.authentication.User;
import cs.brown.edu.aelp.pokemmo.data.authentication.UserManager;
import cs.brown.edu.aelp.pokemmo.map.Location;
import cs.brown.edu.aelp.pokemmo.map.Tournament;
import cs.brown.edu.aelp.pokemmo.map.World;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.Move;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.MoveLoader;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.complex.PoisonMove;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.complex.Wish;
import cs.brown.edu.aelp.pokemmo.server.LoginHandler;
import cs.brown.edu.aelp.pokemmo.server.RegisterHandler;
import cs.brown.edu.aelp.util.JsonFile;
import freemarker.template.Configuration;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import spark.ExceptionHandler;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

/**
 * The Main class entry point.
 */
public final class Main {

  private static World world = new World();
  private static final int DEFAULT_PORT = 4567;
  private static ThreadLocal<DataSource> datasrc;
  private static ThreadLocal<Gson> GSON = new ThreadLocal<Gson>() {
    @Override
    protected Gson initialValue() {
      GsonBuilder b = new GsonBuilder();
      b.registerTypeAdapter(User.class, new User.UserAdapter());
      b.registerTypeAdapter(Location.class, new Location.LocationAdapter());
      b.registerTypeAdapter(Trade.class, new Trade.TradeAdapter());
      b.registerTypeAdapter(Pokemon.class, new Pokemon.PokemonAdapter());
      b.registerTypeAdapter(Inventory.class, new Inventory.InventoryAdapter());
      b.registerTypeAdapter(Move.class, new Move.MoveAdapter());
      b.registerTypeAdapter(Wish.class, new Move.MoveAdapter());
      b.registerTypeAdapter(PoisonMove.class, new Move.MoveAdapter());
      b.registerTypeAdapter(Leaderboards.EloUser.class,
          new Leaderboards.EloUser.EloUserAdapter());
      return b.create();
    }
  };

  /**
   * @param args
   *          Command line arguments
   */
  public static void main(String[] args) {
    new Main().run(args);
  }

  /**
   * Private constructor for main.
   */
  private Main() {
  }

  /**
   * Entry point.
   *
   * @param args
   *          Command line arguments
   */
  private void run(String[] args) {

    // Parse command line arguments
    OptionParser parser = new OptionParser();
    parser.accepts("port").withRequiredArg().ofType(Integer.class)
        .defaultsTo(DEFAULT_PORT);
    OptionSet options = parser.parse(args);

    // ip, port, database, user, pass
    try {
      JsonFile cfg = new JsonFile("config/database_info.json");
      String ip = cfg.getString("ip");
      int port = cfg.getInt("port");
      String db = cfg.getString("database");
      String username = cfg.getString("username");
      String password = cfg.getString("password");
      Main.datasrc = new ThreadLocal<DataSource>() {
        @Override
        protected DataSource initialValue() {
          try {
            return new SQLDataSource(ip, port, db, username, password);
          } catch (SQLException | IOException e) {
            e.printStackTrace();
            System.out.println(
                "ERROR: Something went wrong connecting to the database.");
            return null;
          }
        }
      };

      // triggers table creation, too, if needed
      Main.getDataSource().loadLeaderboards();

    } catch (IOException e) {
      System.out.println(
          "ERROR: Something went wrong reading database_info.json. Check the configuration file.");
      e.printStackTrace();
      return;
    } catch (LoadException e) {
      e.printStackTrace();
      return;
    }

    world.loadChunks();

    // try to load config
    int SAVE_PERIOD = 180; // seconds
    try {
      JsonFile cfg = new JsonFile("config/game_config.json");
      SAVE_PERIOD = cfg.getInt("save_period");
      JsonFile spawn = cfg.getMap("spawn");
      world.setSpawn(new Location(world.getChunk(spawn.getInt("chunk")),
          spawn.getInt("row"), spawn.getInt("col")));
    } catch (IOException e) {
      System.out.println("Something went wrong reading game_config.json");
      e.printStackTrace();
      return;
    }

    // try to start the save-thread if we're backed by SQL
    if (Main.getDataSource() instanceof SQLDataSource) {
      ScheduledExecutorService scheduler = Executors
          .newSingleThreadScheduledExecutor();

      Runnable save = new Runnable() {

        @Override
        public void run() {
          Collection<User> users = UserManager.getAllUsers();
          Collection<Pokemon> pokemon = new ArrayList<>();
          for (User u : users) {
            pokemon.addAll(u.getAllPokemon());
          }
          SQLDataSource data = (SQLDataSource) Main.getDataSource();
          try {
            System.out.printf("Saved %d users.%n", data.save(users));
            System.out.printf("Saved %d pokemon.%n", data.save(pokemon));
            UserManager.purgeDisconnectedUsers();
            users.stream().forEach(user -> user.setChanged(false));
            pokemon.stream().forEach(p1 -> p1.setChanged(false));
          } catch (SaveException e) {
            System.out.println("ERROR: Something went wrong during saving.");
            e.printStackTrace();
          }
        }
      };

      scheduler.scheduleAtFixedRate(save, SAVE_PERIOD, SAVE_PERIOD,
          TimeUnit.SECONDS);

    }

    runSparkServer((int) options.valueOf("port"));

    // sending our packet

    int PACKET_SENDING_PERIOD = 100; // milliseconds

    ScheduledExecutorService packetTimer = Executors
        .newSingleThreadScheduledExecutor();

    Runnable sendPacket = new Runnable() {
      @Override
      public void run() {
        try {
          PacketSender.sendGamePackets();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    };

    packetTimer.scheduleAtFixedRate(sendPacket, PACKET_SENDING_PERIOD,
        PACKET_SENDING_PERIOD, TimeUnit.MILLISECONDS);

    MoveLoader.setupOverrides();
    Tournament.startGenerator();

    CommandHandler ch = new CommandHandler();
    TeleportCommand tc = new TeleportCommand();
    TournamentCommand tnc = new TournamentCommand();
    CoinsCommand cc = new CoinsCommand();
    BattleCommand bc = new BattleCommand();
    HealTeam ht = new HealTeam();
    ItemCommand ic = new ItemCommand();

    ch.registerCommand(tc);
    ch.registerCommand(tnc);
    ch.registerCommand(cc);
    ch.registerCommand(bc);
    ch.registerCommand(ht);
    ch.registerCommand(ic);

    ch.start();

  }

  private void runSparkServer(int port) {
    Spark.webSocket("/game", PlayerWebSocketHandler.class);

    Spark.port(port);
    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.exception(Exception.class, new ExceptionPrinter());

    FreeMarkerEngine freeMarker = createEngine();

    Spark.get("/main", new IndexHandler(), freeMarker);
    Spark.get("/", new IndexHandler(), freeMarker);
    Spark.post("/register", new RegisterHandler());
    Spark.post("/login", new LoginHandler());

    // Setup Spark Routes
  }

  /**
   * Handle requests to the front page of our website.
   */
  private static class IndexHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      return new ModelAndView(ImmutableMap.of(), "index.ftl");
    }
  }

  // Spark set up:

  private static FreeMarkerEngine createEngine() {
    Configuration config = new Configuration();
    File templates = new File("src/main/resources/spark/template/freemarker");
    try {
      config.setDirectoryForTemplateLoading(templates);
    } catch (IOException ioe) {
      System.out.printf("ERROR: Unable use %s for template loading.%n",
          templates);
      System.exit(1);
    }
    return new FreeMarkerEngine(config);
  }

  // Spark error handling:

  /**
   * Display an error page when an exception occurs in the server.
   */

  private static class ExceptionPrinter implements ExceptionHandler<Exception> {
    @Override
    public void handle(Exception e, Request req, Response res) {
      res.status(500);
      StringWriter stacktrace = new StringWriter();
      try (PrintWriter pw = new PrintWriter(stacktrace)) {
        pw.println("<pre>");
        e.printStackTrace(pw);
        pw.println("</pre>");
      }
      res.body(stacktrace.toString());
    }
  }

  public static World getWorld() {
    return Main.world;
  }

  /**
   * Get the DataSource for the current thread. Make sure this is called
   * immediately before trying to use the DataSource! Do not attempt to save the
   * result of this call as a field on an object, or threads may try to use each
   * other's DataSources.
   *
   * @return a DataSource
   */
  public static DataSource getDataSource() {
    return Main.datasrc.get();
  }

  public static Gson GSON() {
    return Main.GSON.get();
  }

  public static Location getSpawn() {
    return getWorld().getSpawn();
  }

}
