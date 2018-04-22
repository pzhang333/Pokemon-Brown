package cs.brown.edu.aelp.pokemmo.pokemon.moves;

import java.util.EnumSet;

import cs.brown.edu.aelp.pokemmo.battle.Effect;
import cs.brown.edu.aelp.pokemmo.battle.events.AttackEvent;
import cs.brown.edu.aelp.pokemmo.battle.events.StartOfTurnEvent;
import cs.brown.edu.aelp.pokemmo.pokemon.PokeType;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.MoveResult.MoveOutcome;
import cs.brown.edu.aelp.pokemmo.trainer.Trainer;

public class WishMove extends Move {

  public WishMove(String id, Integer number, Double accuracy, Double basePower,
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

        System.out.println("Wish came true!");
        return;
      }

      turnsLeft--;
      System.out.println("Wish Turns left: " + turnsLeft);
    }

  }

  @Override
  public MoveResult getResult(AttackEvent evt) {
    MoveResult mr = new MoveResult(evt.getAttackingPokemon(),
        evt.getDefendingPokemon());

    mr.setOutcome(MoveOutcome.NON_ATTACK_SUCCESS);

    evt.getAttackingTrainer().getEffectSlot()
        .register(new WishEffect(evt.getAttackingTrainer(), 1));

    return mr;
  }
}
