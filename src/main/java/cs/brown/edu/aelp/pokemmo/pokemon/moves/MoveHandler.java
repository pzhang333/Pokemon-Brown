package cs.brown.edu.aelp.pokemmo.pokemon.moves;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton Move Handler.
 */
public class MoveHandler {

  private MoveHandler() {
  }

  private static Map<String, Move.Builder> moveMap = new HashMap<>();

  /**
   * Load a set of moves from a JSON file.
   *
   * @param path
   *          The path of the JSON file.
   */
  public static void loadMovesJSON(String path) {

  }

  /**
   * Add a move the moves handler.
   *
   * @param id
   *          The id of the move to add.
   * @param builder
   *          The Builder we can use as a factory to clone Moves.
   */
  public static void addMove(String id, Move.Builder builder) {
    moveMap.put(id, builder);
  }

  /**
   * Get a move by its id.
   *
   * @param id
   * @return
   */
  public static Move getMoveById(String id) {
    assert moveMap.containsKey(id);
    return moveMap.get(id).build();
  }
}
