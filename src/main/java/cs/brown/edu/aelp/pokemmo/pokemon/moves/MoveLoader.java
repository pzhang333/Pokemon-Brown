package cs.brown.edu.aelp.pokemmo.pokemon.moves;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton Move Handler.
 */
public class MoveLoader {

  private MoveLoader() {
  }

  private static Map<Integer, Move.Builder> moveMap = new HashMap<>();

  /**
   * Load a set of moves from a JSON file.
   *
   * @param path
   *          The path of the JSON file.
   */
  public static void loadMovesJSON(String path) {
    // TODO: Implement
  }

  /**
   * Add a move the moves handler.
   *
   * @param id
   *          The id of the move to add.
   * @param builder
   *          The Builder we can use as a factory to clone Moves.
   */
  public static void addMove(int id, Move.Builder builder) {
    moveMap.put(id, builder);
  }

  /**
   * Get a move by its id.
   *
   * @param id
   *          the id of the move to get
   * @return a move with the given id
   */
  public static Move getMoveById(int id) {
    assert moveMap.containsKey(id);
    return moveMap.get(id).build();
  }
}
