package cs.brown.edu.aelp.pokemmo.pokemon.moves;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton Move Handler.
 */
public class MoveHandler {

  private static MoveHandler instance = new MoveHandler();

  private Map<String, Move> moveMap = new HashMap<>();

  /**
   * Load a set of moves from a JSON file.
   *
   * @param path
   *          The path of the JSON file.
   */
  public void loadMovesJSON(String path) {

  }

  /**
   * Add a move the moves handler.
   *
   * @param move
   *          The move to add to the moves handler.
   */
  public void addMove(Move move) {
    moveMap.put(move.getId(), move);
  }
}
