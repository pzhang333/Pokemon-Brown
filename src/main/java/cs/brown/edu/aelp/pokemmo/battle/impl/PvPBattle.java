package cs.brown.edu.aelp.pokemmo.battle.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cs.brown.edu.aelp.pokemmo.battle.Arena;
import cs.brown.edu.aelp.pokemmo.battle.Battle;
import cs.brown.edu.aelp.pokemmo.battle.action.FightTurn;
import cs.brown.edu.aelp.pokemmo.battle.action.NullTurn;
import cs.brown.edu.aelp.pokemmo.battle.action.SwitchTurn;
import cs.brown.edu.aelp.pokemmo.battle.action.Turn;
import cs.brown.edu.aelp.pokemmo.battle.events.AttackEvent;
import cs.brown.edu.aelp.pokemmo.battle.events.EndOfTurnEvent;
import cs.brown.edu.aelp.pokemmo.battle.events.KnockedOutEvent;
import cs.brown.edu.aelp.pokemmo.battle.events.StartOfTurnEvent;
import cs.brown.edu.aelp.pokemmo.battle.events.SwitchInEvent;
import cs.brown.edu.aelp.pokemmo.battle.events.SwitchOutEvent;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.MoveResult;
import cs.brown.edu.aelp.pokemmo.trainer.Trainer;

public class PvPBattle extends Battle {

  private final Trainer a;

  private final Trainer b;

  private Map<Trainer, Turn> turnsMap = new HashMap<>();

  public PvPBattle(Arena arena, Trainer a, Trainer b) {
    super(arena);
    this.a = a;
    this.b = b;

    setBattleState(BattleState.WAITING);
  }

  public void evaluate() {

    System.out.println(turnsMap);

    if (!getBattleState().equals(BattleState.READY)) {
      throw new RuntimeException("Not in ready state!");
    }

    List<Turn> turns = new ArrayList<>(turnsMap.values());
    turns.sort(this::turnComparator);

    boolean stop = false;

    for (Turn turn : turns) {
      // start of turn

      StartOfTurnEvent startEvent = new StartOfTurnEvent(this, turn);

      turn.getTrainer().getEffectSlot().handle(startEvent);
      turn.getTrainer().getActivePokemon().getEffectSlot().handle(startEvent);
    }

    for (Turn turn : turns) {

      System.out.println(turn);

      if (turn instanceof NullTurn) {
        handleTurn((NullTurn) turn);
      } else if (turn instanceof FightTurn) {
        handleTurn((FightTurn) turn);
      } else if (turn instanceof SwitchTurn) {
        handleTurn((SwitchTurn) turn);
      } else {
        handleTurn(turn);
      }

      // If the other pokemon is knocked out we can't continue...
      if (other(turn.getTrainer()).getActivePokemon().isKnockedOut()) {
        break;
      }
    }

    for (Turn turn : turns) {
      // end of turn

      // Clean this up:

      EndOfTurnEvent endEvent = new EndOfTurnEvent(this, turn);

      turn.getTrainer().getEffectSlot().handle(endEvent);
      turn.getTrainer().getActivePokemon().getEffectSlot().handle(endEvent);
    }

    turnsMap.clear();

    setBattleState(BattleState.WAITING);
  }

  private void handleTurn(Turn turn) {
    throw new IllegalArgumentException("No turn handler!");
  }

  private void handleTurn(NullTurn turn) {
    System.out.println("Null turn");

    // Trainer failed to switch in a pokemon after a knockout. Trigger loss.
    if (turn.getTrainer().getActivePokemon().isKnockedOut()) {
      victory(other(turn.getTrainer()));
    }
  }

  private void handleTurn(SwitchTurn turn) {
    Trainer trainer = turn.getTrainer();

    // Events...
    SwitchInEvent switchInEvent = new SwitchInEvent(this, turn.getPokemonIn(),
        turn.getPokemonOut());
    SwitchOutEvent switchOutEvent = new SwitchOutEvent(this,
        turn.getPokemonIn(), turn.getPokemonOut());

    // Broadcast switch out
    trainer.getEffectSlot().handle(switchOutEvent);
    trainer.getActivePokemon().getEffectSlot().handle(switchOutEvent);

    // Do switch
    trainer.setActivePokemon(turn.getPokemonIn());

    // Broadcast switch in
    trainer.getEffectSlot().handle(switchInEvent);
    trainer.getActivePokemon().getEffectSlot().handle(switchInEvent);
  }

  public void handleTurn(FightTurn turn) {
    System.out.println("Fight turn");

    Trainer atkTrainer = turn.getTrainer();
    Trainer defTrainer = other(atkTrainer);

    AttackEvent atkEvent = new AttackEvent(this, atkTrainer,
        atkTrainer.getActivePokemon(), defTrainer,
        defTrainer.getActivePokemon());

    // Attacking event
    atkTrainer.getEffectSlot().handle(atkEvent);
    atkTrainer.getActivePokemon().getEffectSlot().handle(atkEvent);

    MoveResult result = turn.getMove().getResult(atkEvent);
    // Todo: defending events

    System.out.println(result);

    // TODO: Check if the player knocked themselves out or...
    // Basically just make sure the self-destruct isn't broken... or really
    // maybe it doesn't matter

    if (result.getOutcome().equals(MoveResult.MoveOutcome.HIT)) {
      // Other events...

      Pokemon defendingPokemon = result.getDefendingPokemon();

      defendingPokemon
          .setHealth(defendingPokemon.getHealth() - result.getDamage());

      if (defendingPokemon.isKnockedOut()) {
        System.out.println("KO!");
        defendingPokemon.getEffectSlot().handle(new KnockedOutEvent(this,
            result.getAttackingPokemon(), defendingPokemon));

        if (defTrainer.allPokemonKnockedOut()) {
          victory(atkTrainer);
        }

      }
    }
  }

  public void victory(Trainer t) {
    System.out.println("Victory for: " + t.getId());

    setBattleState(BattleState.DONE);
  }

  public void setTurn(Turn t) {

    // TODO: Check move logical validity...

    if (!getBattleState().equals(BattleState.WAITING)) {
      throw new RuntimeException("Not in waiting state!");
    }

    // This is ugly but (probably) works.
    // After a having a pokemon KO'd, the trainer must replace the pokemon.
    // During this time the KO'ing player CANNOT move.

    if (t.getTrainer().getActivePokemon().isKnockedOut()) {

      if (!(t instanceof SwitchTurn)) {
        return;
      }

      turnsMap.put(t.getTrainer(), t);

      // If the other trainer's pokemon isn't knocked out skip their current
      // turn.
      if (!other(t.getTrainer()).getActivePokemon().isKnockedOut()) {
        turnsMap.put(other(t.getTrainer()),
            new NullTurn(other(t.getTrainer())));
      }

    } else if (other(t.getTrainer()).getActivePokemon().isKnockedOut()) {
      turnsMap.put(t.getTrainer(), new NullTurn(t.getTrainer()));
    } else {
      turnsMap.put(t.getTrainer(), t);
    }

    if (turnsMap.size() == 2) {
      setBattleState(BattleState.READY);
    }
  }

  private Trainer other(Trainer t) {
    if (t.equals(a)) {
      return b;
    }

    return a;
  }
}
