package cs.brown.edu.aelp.pokemmo.pokemon.moves.complex;

import cs.brown.edu.aelp.pokemmo.battle.Effect;
import cs.brown.edu.aelp.pokemmo.battle.events.AttackEvent;
import cs.brown.edu.aelp.pokemmo.battle.events.EndOfBattleEvent;
import cs.brown.edu.aelp.pokemmo.battle.events.EndOfTurnEvent;
import cs.brown.edu.aelp.pokemmo.battle.summaries.HealthChangeSummary;
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
  }

  private class WishEffect extends Effect {

    private Integer turnsLeft;

    private final Trainer trainer;

    public WishEffect(Trainer trainer, int turnsLeft) {
      this.trainer = trainer;
      this.turnsLeft = turnsLeft;
    }

    @Override
    public void handle(EndOfTurnEvent event) {
      if (turnsLeft == 0) {

        Pokemon p = trainer.getActivePokemon();
        int amt = (int) Math.ceil(p.getMaxHp() / 2);
        p.setHealth(p.getCurrHp() + amt);
        event.getBattle().getPendingBattleUpdate()
            .addSummary(new HealthChangeSummary(p, "Wish came true!"));
        getEffectSlot().deregister(this);
        return;
      }

      turnsLeft--;
    }

    @Override
    public void handle(EndOfBattleEvent event) {
      getEffectSlot().deregister(this);
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
