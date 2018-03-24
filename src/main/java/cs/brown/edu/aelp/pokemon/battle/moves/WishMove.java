package cs.brown.edu.aelp.pokemon.battle.moves;

import java.util.EnumSet;

import cs.brown.edu.aelp.pokemon.battle.Move;
import cs.brown.edu.aelp.pokemon.battle.PokeType;

public class WishMove extends Move {

  public WishMove(String id, Double number, Boolean accuracy, Double basePower,
      MoveCategory category, String description, String shortDescription,
      String name, Integer pp, Integer priority, MoveTarget target,
      PokeType type, EnumSet<MoveFlag> flags, MoveComplexity complexity) {
    super(id, number, accuracy, basePower, category, description,
        shortDescription, name, pp, priority, target, type, flags, complexity);
  }

}
