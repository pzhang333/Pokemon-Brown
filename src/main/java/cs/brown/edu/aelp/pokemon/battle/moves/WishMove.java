package cs.brown.edu.aelp.pokemon.battle.moves;

import java.util.EnumSet;

import cs.brown.edu.aelp.pokemon.battle.Effect;
import cs.brown.edu.aelp.pokemon.battle.Move;
import cs.brown.edu.aelp.pokemon.battle.MoveResult;
import cs.brown.edu.aelp.pokemon.battle.MoveResult.MoveOutcome;
import cs.brown.edu.aelp.pokemon.battle.PokeType;
import cs.brown.edu.aelp.pokemon.battle.events.AttackEvent;
import cs.brown.edu.aelp.pokemon.battle.events.StartOfTurnEvent;
import cs.brown.edu.aelp.pokemon.trainer.Pokemon;
import cs.brown.edu.aelp.pokemon.trainer.Trainer;

public class WishMove extends Move {

  public WishMove(String id, Double number, Double accuracy, Double basePower,
      MoveCategory category, String description, String shortDescription,
      String name, Integer pp, Integer priority, MoveTarget target,
      PokeType type, EnumSet<MoveFlag> flags, MoveComplexity complexity) {
    super(id, number, accuracy, basePower, category, description,
        shortDescription, name, pp, priority, target, type, flags, complexity);
  }

  private class WishEffect extends Effect {

    private Integer turnsLeft;

    private final Trainer trainer;

    public WishEffect(Trainer trainer, int turnsLeft) {
      this.trainer = trainer;
      this.turnsLeft = turnsLeft;
    }

    @Override
    public void handle(StartOfTurnEvent event) {
      if (turnsLeft == 0) {

        Pokemon p = trainer.getActivePokemon();

        p.setHealth(p.getBaseHealth());

        getEffectSlot().deregister(this);
        return;
      }

      turnsLeft--;
      System.out.println("Wish Turns left: " + turnsLeft);
    }

  }

  @Override
  public MoveResult getResult(AttackEvent evt) {
    MoveResult mr = new MoveResult();

    mr.setOutcome(MoveOutcome.NON_ATTACK_SUCCESS);

    evt.getAttackingTrainer().getEffectSlot()
        .register(new WishEffect(evt.getAttackingTrainer(), 1));

    return mr;
  }
}
