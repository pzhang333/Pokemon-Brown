package cs.brown.edu.aelp.pokemmo.pokemon.moves.complex;

import cs.brown.edu.aelp.pokemmo.battle.Effect;
import cs.brown.edu.aelp.pokemmo.battle.events.AttackEvent;
import cs.brown.edu.aelp.pokemmo.battle.events.EndOfTurnEvent;
import cs.brown.edu.aelp.pokemmo.battle.summaries.HealthChangeSummary;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;
import cs.brown.edu.aelp.pokemmo.pokemon.Status;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.Move;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.MoveResult;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.MoveResult.MoveOutcome;

public class PoisonMove extends Move {

  public PoisonMove(int id) {
    super(id);
  }

  public PoisonMove(Move m) {
    super(m);
  }

  public class PoisonEffect extends Effect {

    private Pokemon p;

    public PoisonEffect(Pokemon p) {
      this.p = p;
    }

    @Override
    public void handle(EndOfTurnEvent evt) {
      System.out.println("Poison effect is processing end of turn");
      if (p.getStatus() == null || p.getStatus() != Status.POISON) {
        p.getEffectSlot().deregister(this);
        return;
      }
      System.out.println("Adding dmg summary for poison.");
      int dmg = (int) p.getMaxHp() / 8;
      evt.getBattle().getPendingBattleUpdate()
          .addSummary(new HealthChangeSummary(p, -dmg,
              String.format("%s took poison damage.", p.toString())));
      p.setHealth(p.getCurrHp() - dmg);
    }

  }

  @Override
  public MoveResult getMoveResult(AttackEvent evt) {
    MoveResult mr = new MoveResult(evt.getAttackingPokemon(),
        evt.getDefendingPokemon(), this, evt.getBattle().getArena());

    Status status = evt.getDefendingPokemon().getStatus();

    if (status != null && status != Status.NONE) {
      System.out.println("Poison failed because status = " + status);
      mr.setOutcome(MoveOutcome.NON_ATTACK_FAIL);
    } else {
      System.out.println("Poison succeeded");
      evt.getDefendingPokemon().setStatus(Status.POISON);
      evt.getDefendingPokemon().getEffectSlot()
          .register(new PoisonEffect(evt.getDefendingPokemon()));
      mr.setOutcome(MoveOutcome.NON_ATTACK_SUCCESS);
    }

    return mr;
  }
}
