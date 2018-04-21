package cs.brown.edu.aelp.pokemon;

import com.google.common.collect.ImmutableMap;
import cs.brown.edu.aelp.networking.PlayerWebSocketHandler;
import cs.brown.edu.aelp.pokemmo.data.DataSource;
import cs.brown.edu.aelp.pokemmo.data.SQLDataSource;
import cs.brown.edu.aelp.pokemmo.map.Location;
import cs.brown.edu.aelp.pokemmo.map.World;
import cs.brown.edu.aelp.pokemmo.server.RegisterHandler;
import cs.brown.edu.aelp.util.JsonFile;
import freemarker.template.Configuration;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
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

  private static World world;
  private static final int DEFAULT_PORT = 4567;
  private static ThreadLocal<DataSource> datasrc;
  private static Location spawn;

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
    parser.accepts("gui");
    parser.accepts("port").withRequiredArg().ofType(Integer.class)
        .defaultsTo(DEFAULT_PORT);
    OptionSet options = parser.parse(args);

    // try to connect to database
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

    } catch (IOException e) {
      System.out.println(
          "ERROR: Something went wrong reading database_info.json. Check the configuration file.");
      e.printStackTrace();
      return;
    }

    // try to load config
    try {
      JsonFile cfg = new JsonFile("config/game_config.json");
      // get cfg values as needed, e.g:
      int i = cfg.getInt("example_int");
      String s = cfg.getString("example_string");
      double inner_d = cfg.getDouble("example_object", "inner_double");
      String inner_s = cfg.getString("example_object", "inner_string");
    } catch (IOException e) {
      System.out.println("Something went wrong reading game_config.json");
      e.printStackTrace();
      return;
    }

    // TODO: Load a world into Main.world

    if (options.has("gui")) {
      runSparkServer((int) options.valueOf("port"));
    }

    System.out.println("Hello, World!");

    // temporary repl
    long sleepTime = 1000;
    while (5 != 6) {
      PlayerWebSocketHandler.sendGamePackets();
      try {
        Thread.sleep(sleepTime);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  private void runSparkServer(int port) {
    Spark.webSocket("/game", PlayerWebSocketHandler.class);

    Spark.port(port);
    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.exception(Exception.class, new ExceptionPrinter());

    FreeMarkerEngine freeMarker = createEngine();

    Spark.get("/main", new IndexHandler(), freeMarker);
    Spark.get("/", new IndexHandler(), freeMarker);
    Spark.post("/register", new RegisterHandler(Main.getDataSource()));

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

  public static DataSource getDataSource() {
    return Main.datasrc.get();
  }

  public static Location getSpawn() {
    return Main.spawn;
  }

}
