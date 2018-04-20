package cs.brown.edu.aelp.pokemon;

import cs.brown.edu.aelp.map.World;

/**
 * The Main class entry point.
 */
public final class Main {

  // TODO: Actually load a world in when the server spins up
  private static World world;

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
    System.out.println("Hello, World!");
  }

  public static World getWorld() {
    return world;
  }

}
