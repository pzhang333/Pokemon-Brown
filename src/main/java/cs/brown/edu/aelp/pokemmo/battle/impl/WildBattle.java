package cs.brown.edu.aelp.pokemmo.battle.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cs.brown.edu.aelp.networking.PacketSender;
import cs.brown.edu.aelp.networking.PlayerWebSocketHandler.TURN_STATE;
import cs.brown.edu.aelp.pokemmo.battle.Arena;
import cs.brown.edu.aelp.pokemmo.battle.Battle;
import cs.brown.edu.aelp.pokemmo.battle.BattleUpdate;
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
import cs.brown.edu.aelp.pokemmo.battle.summaries.FightSummary;
import cs.brown.edu.aelp.pokemmo.battle.summaries.SwitchSummary;
import cs.brown.edu.aelp.pokemmo.data.authentication.User;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.Move;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.MoveResult;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.MoveResult.MoveOutcome;
import cs.brown.edu.aelp.pokemmo.trainer.Trainer;

public class WildBattle extends Battle {

  private final Pokemon wild;
  private final Trainer a;

  private Trainer winner;

  private Trainer loser;

  private Map<Trainer, Turn> turnsMap = new HashMap<>();
  private Trainer b;

  private BattleUpdate lastBattleUpdate = null;

  private BattleUpdate pendingBattleUpdate = null;

  public WildBattle(Integer id, Arena arena, User user, Pokemon wild) {
    super(id, arena);
    this.wild = wild;
    this.a = user;

    this.b = new Trainer(-1);
    b.addPokemonToTeam(wild);
    this.b.setCurrentBattle(this);

    setBattleState(BattleState.WAITING);
  }

  public Pokemon getWildPokemon() {
    return wild;
  }

  private Turn genTrainerTurn() {

    Random rand = new Random();
    Move m = wild.getMoves().get(rand.nextInt(wild.getMoves().size()));

    return new FightTurn(b, m);
  }

  @Override
  public void evaluate() {

    System.out.println("evaluate()");
    turnsMap.put(b, genTrainerTurn());

    if (!getBattleState().equals(BattleState.READY)) {
      throw new RuntimeException("Not in ready state!");
    }

    List<Turn> turns = new ArrayList<>(turnsMap.values());
    turns.sort(this::turnComparator);

    pendingBattleUpdate = new BattleUpdate();

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

      if (getBattleState().equals(BattleState.DONE)) {

        sendBattleUpdate();

        // TODO: Add end of Battle event.
        return;
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

    lastBattleUpdate = pendingBattleUpdate;

    sendBattleUpdate();

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

    pendingBattleUpdate.addSummary(
        new SwitchSummary(turn.getPokemonIn(), turn.getPokemonOut()));
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

    if (atkEvent.isPrevented()) {

      Pokemon atkPokemon = atkTrainer.getActivePokemon();
      Pokemon defPokemon = defTrainer.getActivePokemon();

      String msg = atkEvent.getPreventedMsg();
      if (msg.isEmpty()) {
        msg = String.format("%s used %s, but it failed!",
            atkPokemon.getSpecies(), turn.getMove().getName());
      }

      pendingBattleUpdate
          .addSummary(new FightSummary(atkPokemon, defPokemon, msg, ""));

    } else {

      // TODO: ADD IN MOVE_RESULT
      // MoveResult result = new MoveResult(atkEvent.getAttackingPokemon(),
      // atkEvent.getDefendingPokemon(), turn.getMove(), getArena());

      MoveResult result = turn.getMove().getMoveResult(atkEvent);
      result.evaluate();

      // Todo: defending events
      // System.out.println(result);

      // TODO: Check if the player knocked themselves out or...
      // Basically just make sure the self-destruct isn't broken... or really
      // maybe it doesn't matter

      Pokemon atkPokemon = atkTrainer.getActivePokemon();

      StringBuilder base = new StringBuilder(atkPokemon.getSpecies())
          .append(" used ").append(turn.getMove().getName());

      if (result.getOutcome().equals(MoveOutcome.HIT)) {

        base.append(".");
        // Other events...

        // System.out.println("The attack was effective or perhaps not...");

        Pokemon defendingPokemon = result.getDefendingPokemon();

        defendingPokemon
            .setHealth(defendingPokemon.getCurrHp() - result.getDamage());

        System.out.println(defendingPokemon.getCurrHp());

        if (defendingPokemon.isKnockedOut()) {
          // System.out.println("K.O.!");
          defendingPokemon.getEffectSlot().handle(new KnockedOutEvent(this,
              result.getAttackingPokemon(), defendingPokemon));

          if (defTrainer.allPokemonKnockedOut()) {
            victory(atkTrainer);
          }
        }

        pendingBattleUpdate.addSummary(new FightSummary(atkPokemon,
            defendingPokemon, base.toString(), "basic"));

      } else {

        String anim = "basic";

        if (result.getOutcome().equals(MoveOutcome.MISS)) {

          System.out.println("The attack missed");

          anim = "basic-miss";

          base.append(", but it missed.");

        } else if (result.getOutcome().equals(MoveOutcome.BLOCKED)) {
          System.out.println("The attack was blocked");

          base.append(", but it was blocked.");

        } else if (result.getOutcome().equals(MoveOutcome.NO_EFFECT)) {
          System.out.println("The attack had no effect");

          base.append(", but it had no effect.");

        } else if (result.getOutcome().equals(MoveOutcome.NON_ATTACK_SUCCESS)) {
          System.out.println("The move succeded");

          base.append(".");
        } else if (result.getOutcome().equals(MoveOutcome.NON_ATTACK_FAIL)) {
          System.out.println("The move failed");

          base.append(", but it failed.");
        }

        Pokemon defPokemon = defTrainer.getActivePokemon();

        pendingBattleUpdate.addSummary(
            new FightSummary(atkPokemon, defPokemon, base.toString(), anim));
      }
    }
  }

  public void victory(Trainer t) {
    System.out.println("Victory for: " + t.getId());

    winner = t;
    loser = other(t);

    lastBattleUpdate = pendingBattleUpdate;

    setBattleState(BattleState.DONE);
  }

  public boolean setTurn(Turn t) {

    // TODO: Check move logical validity...

    if (!getBattleState().equals(BattleState.WAITING)) {
      // throw new RuntimeException("Not in waiting state!");
      return false;
    }

    // This is ugly but (probably) works.
    // After a having a pokemon KO'd, the trainer must replace the pokemon.
    // During this time the KO'ing player CANNOT move.

    if (t.getTrainer().getActivePokemon().isKnockedOut()) {

      if (!(t instanceof SwitchTurn)) {
        return false;
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

    System.out.println("TM : " + turnsMap.size());
    if (turnsMap.size() >= 1) {
      setBattleState(BattleState.READY);
    }

    return true;
  }

  private Trainer other(Trainer t) {
    if (t.equals(a)) {
      return b;
    }
    return a;
  }

  public User getUser() {
    return (User) a;
  }

  @Override
  public String dbgStatus() {
    StringBuilder sb = new StringBuilder();

    sb.append(a.getActivePokemon()).append(wild);

    return sb.toString();
  }

  @Override
  public Trainer getLoser() {
    return loser;
  }

  @Override
  public Trainer getWinner() {
    return winner;
  }

  @Override
  public void forfeit(Trainer t) {
    victory(other(t));
  }

  private void sendBattleUpdateTo(Trainer t) {
    if (t.getId() == -1) {
      return;
    }

    PacketSender.sendBattleTurnPacket(getId(), t, lastBattleUpdate,
        t.getActivePokemon(), other(t).getActivePokemon(),
        ((t.getActivePokemon().isKnockedOut()) ? TURN_STATE.MUST_SWITCH
            : TURN_STATE.NORMAL).ordinal());
  }

  private void sendBattleUpdate() {
    sendBattleUpdateTo(a);
  }
}
