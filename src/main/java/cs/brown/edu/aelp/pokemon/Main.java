package cs.brown.edu.aelp.pokemon;

import cs.brown.edu.aelp.map.World;
import java.io.File;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import cs.brown.edu.aelp.networking.PlayerWebSocketHandler;
import freemarker.template.Configuration;
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

  // TODO: Actually load a world in when the server spins up
  private static World world;
  private static final int DEFAULT_PORT = 4567;

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

    if (options.has("gui")) {
      runSparkServer((int) options.valueOf("port"));
    }

    System.out.println("Hello, World!");

    // temporary repl
    int count = 0;
    while (5 != 6) {
      if (count >= 100000) {
        count = 0;
      }
      if (count % 10000 == 0) {
        // this should be in our game loop and execute every tick
        PlayerWebSocketHandler.sendGamePackets();
      }
    }
  }

  private void runSparkServer(int port) {
    Spark.webSocket("/game", PlayerWebSocketHandler.class);
    
    Spark.port(port);
    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.exception(Exception.class, new ExceptionPrinter());
    
    FreeMarkerEngine freeMarker = createEngine();

    Spark.get("/main", new FrontHandler(), freeMarker);

    // Setup Spark Routes
  }
  
  /**
   * Handle requests to the front page of our website.
   */
  private static class FrontHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables = ImmutableMap.of("title",
          "Pokemon", "content", "something", "readmessage", "something", "message", "something");
      return new ModelAndView(variables, "query.ftl");
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
  private static class ExceptionPrinter implements ExceptionHandler {
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
    return world;
  }

}
