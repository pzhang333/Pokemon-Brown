package cs.brown.edu.aelp.pokemon;

import java.util.ArrayList;
import java.util.List;

import cs.brown.edu.aelp.pokemon.battle.Move;

/**
 * The Pokemon class.
 */
public class Pokemon {

  public List<Move> moves = new ArrayList<>(4);

  private int hp = 0;

  public boolean hasMove(Move move) {
    return moves.contains(move);
  }

  public boolean isKnockedOut() {
    return (hp == 0);
  }

  public Double getBaseSpeed() {
    return 1.0;
  }

  public Double getSpeed() {
    return 1.0;
  }
}
