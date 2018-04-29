package cs.brown.edu.aelp.pokemmo.pokemon.moves.complex;

import cs.brown.edu.aelp.pokemmo.battle.Effect;
import cs.brown.edu.aelp.pokemmo.battle.events.AttackEvent;
import cs.brown.edu.aelp.pokemmo.battle.events.EndOfTurnEvent;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.Move;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.MoveResult;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.MoveResult.MoveOutcome;

public abstract class PosionMove extends Move {

  protected PosionMove(int id) {
    super(id);
  }

  public class PoisonEffect extends Effect {

    private Pokemon p;

    public PoisonEffect(Pokemon p) {
      this.p = p;
    }

    @Override
    public void handle(EndOfTurnEvent evt) {
      int dmg = (int) p.getMaxHp() / 8;

      p.setHealth(p.getCurrHp() - dmg);
    }

  }

  @Override
  public MoveResult getMoveResult(AttackEvent evt) {
    MoveResult mr = new MoveResult(evt.getAttackingPokemon(),
        evt.getDefendingPokemon(), this, evt.getBattle().getArena());

    evt.getAttackingTrainer().getEffectSlot()
        .register(new PoisonEffect(evt.getDefendingPokemon()));

    mr.setOutcome(MoveOutcome.HIT);

    return mr;
  }
}
