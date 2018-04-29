package cs.brown.edu.aelp.pokemmo.pokemon.moves.complex;

import cs.brown.edu.aelp.pokemmo.battle.Effect;
import cs.brown.edu.aelp.pokemmo.battle.events.AttackEvent;
import cs.brown.edu.aelp.pokemmo.battle.events.StartOfTurnEvent;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.Move;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.MoveResult;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.MoveResult.MoveOutcome;
import cs.brown.edu.aelp.pokemmo.trainer.Trainer;

public class Wish extends Move {

  public Wish() {
    super(273);
  }

  public Wish(Move m) {
    super(m);

    System.out.println("AADSASDD");
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

        p.setHealth(p.getCurrHp() + (int) Math.ceil(p.getMaxHp() / 2));

        getEffectSlot().deregister(this);

        System.out.println("Wish came true!");
        return;
      }

      turnsLeft--;
      System.out.println("Wish Turns left: " + turnsLeft);
    }

  }

  @Override
  public MoveResult getMoveResult(AttackEvent evt) {
    MoveResult mr = new MoveResult(evt.getAttackingPokemon(),
        evt.getDefendingPokemon(), this, evt.getBattle().getArena());

    evt.getAttackingTrainer().getEffectSlot()
        .register(new WishEffect(evt.getAttackingTrainer(), 1));

    mr.setOutcome(MoveOutcome.NON_ATTACK_SUCCESS);

    return mr;
  }
}
