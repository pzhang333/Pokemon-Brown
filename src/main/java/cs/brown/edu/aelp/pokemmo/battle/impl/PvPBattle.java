package cs.brown.edu.aelp.pokemmo.battle.impl;

import cs.brown.edu.aelp.networking.PacketSender;
import cs.brown.edu.aelp.networking.PlayerWebSocketHandler.TURN_STATE;
import cs.brown.edu.aelp.pokemmo.battle.Arena;
import cs.brown.edu.aelp.pokemmo.battle.Battle;
import cs.brown.edu.aelp.pokemmo.battle.Item;
import cs.brown.edu.aelp.pokemmo.battle.Item.ItemType;
import cs.brown.edu.aelp.pokemmo.battle.LassoEffect;
import cs.brown.edu.aelp.pokemmo.battle.action.FightTurn;
import cs.brown.edu.aelp.pokemmo.battle.action.ItemTurn;
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
import cs.brown.edu.aelp.pokemmo.battle.summaries.HealthChangeSummary;
import cs.brown.edu.aelp.pokemmo.battle.summaries.ItemSummary;
import cs.brown.edu.aelp.pokemmo.battle.summaries.SwitchSummary;
import cs.brown.edu.aelp.pokemmo.data.authentication.User;
import cs.brown.edu.aelp.pokemmo.map.Location;
import cs.brown.edu.aelp.pokemmo.map.Tournament;
import cs.brown.edu.aelp.pokemmo.map.World;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.Move;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.MoveResult;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.MoveResult.MoveOutcome;
import cs.brown.edu.aelp.pokemmo.trainer.Trainer;
import cs.brown.edu.aelp.pokemon.Main;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PvPBattle extends Battle {

  private final User a;

  private final User b;

  private Trainer winner;

  private Trainer loser;

  private Map<Trainer, Turn> turnsMap = new HashMap<>();

  public PvPBattle(Integer id, Arena arena, User a, User b) {
    super(id, arena);

    this.a = a;
    this.b = b;

    a.setCurrentBattle(this);
    b.setCurrentBattle(this);

    sendInitPackets();

    setBattleState(BattleState.WAITING);
  }

  private void sendInitPackets() {
    PacketSender.sendPvPPacket(this, a, b);
  }

  @Override
  public void evaluate() {

    System.out.println("evaluate()");

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
      } else if (turn instanceof ItemTurn) {
        handleTurn((ItemTurn) turn);
      } else {
        handleTurn(turn);
      }

      if (getBattleState().equals(BattleState.DONE)) {
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

    this.setLastBattleUpdate(this.getPendingBattleUpdate());
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

  private void handleTurn(ItemTurn turn) {

    User u = (User) turn.getTrainer();
    Item item = turn.getItem();
    item.removeFromInventory(u.getInventory());

    // TODO: Add messages

    if (item.isPokeball()) {
      getPendingBattleUpdate().addSummary(new ItemSummary(item, false,
          other(turn.getTrainer()).getActivePokemon(),
          "Pokeballs don't work in a PvP battle!"));
      return;
    }

    if (item.getType() == ItemType.LASSO) {
      if (new Random().nextDouble() > 0.3) {
        Pokemon p = other(turn.getTrainer()).getActivePokemon();
        p.getEffectSlot().register(new LassoEffect());
        getPendingBattleUpdate()
            .addSummary(new ItemSummary(item, true, String.format(
                "%s has been lasso'd and cannot switch out.", p.toString())));
      } else {
        getPendingBattleUpdate().addSummary(
            new ItemSummary(item, false, "A lasso was thrown, but it missed."));
      }
      return;
    }

    super.handleNonPokeballItem(turn);
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

    if (!switchOutEvent.isAllowed()) {
      getPendingBattleUpdate()
          .addSummary(new SwitchSummary(turn.getPokemonOut(),
              turn.getPokemonOut(), turn.getPokemonOut().toString()
                  + " tried to switch out but couldn't."));
      return;
    }

    // Do switch
    trainer.setActivePokemon(turn.getPokemonIn());

    // Broadcast switch in
    trainer.getEffectSlot().handle(switchInEvent);
    trainer.getActivePokemon().getEffectSlot().handle(switchInEvent);

    this.getPendingBattleUpdate().addSummary(
        new SwitchSummary(turn.getPokemonIn(), turn.getPokemonOut()));

    turn.getPokemonOut().resetStatStages();
  }

  public void handleTurn(FightTurn turn) {
    System.out.println("Fight turn");

    turn.getMove().setPP(turn.getMove().getCurrPP() - 1);

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
        msg = String.format("%s used %s, but it failed!", atkPokemon.toString(),
            turn.getMove().getName());
      }

      this.getPendingBattleUpdate()
          .addSummary(new FightSummary(atkPokemon, defPokemon, msg, ""));

    } else {

      // TODO: ADD IN MOVE_RESULT
      // MoveResult result = new MoveResult(atkEvent.getAttackingPokemon(),
      // atkEvent.getDefendingPokemon(), turn.getMove(), getArena());

      MoveResult result = turn.getMove().getMoveResult(atkEvent);
      result.evaluate();

      // Todo: defending events
      // System.out.println(result);

      Pokemon atkPokemon = atkTrainer.getActivePokemon();

      StringBuilder base = new StringBuilder(atkPokemon.toString())
          .append(" used ").append(turn.getMove().getName());

      if (result.getOutcome().equals(MoveOutcome.HIT)) {

        double t = result.calcType();
        if (t == 0.0) {
          base.append(", but it had no effect.");
        } else if (t > 0.0 && t < 1.0) {
          base.append(", but it's not very effective.");
        } else if (t > 1.0) {
          base.append(", it's super effective!");
        } else {
          base.append(".");
        }

        // Other events...

        // System.out.println("The attack was effective or perhaps not...");

        Pokemon defendingPokemon = result.getDefendingPokemon();

        defendingPokemon
            .setHealth(defendingPokemon.getCurrHp() - result.getDamage());

        System.out.println(defendingPokemon.getCurrHp());

        this.getPendingBattleUpdate().addSummary(new FightSummary(atkPokemon,
            defendingPokemon, base.toString(), "basic"));

        // recoil check
        if (turn.getMove().getFlags().contains(Move.Flags.RECOIL)) {
          int dmg = (int) (turn.getMove().getRecoilPercent()
              * result.getDamage());
          atkPokemon.setHealth(atkPokemon.getCurrHp() - dmg);
          this.getPendingBattleUpdate().addSummary(new HealthChangeSummary(
              atkPokemon,
              String.format("%s took recoil damage.", atkPokemon.toString())));
        }

        if (defendingPokemon.isKnockedOut()) {
          // System.out.println("K.O.!");
          defendingPokemon.getEffectSlot().handle(new KnockedOutEvent(this,
              result.getAttackingPokemon(), defendingPokemon));

          if (defTrainer.allPokemonKnockedOut()) {
            victory(atkTrainer);
            return;
          }
        }

        if (atkPokemon.isKnockedOut()) {
          // System.out.println("K.O.!");
          atkPokemon.getEffectSlot().handle(new KnockedOutEvent(this,
              result.getAttackingPokemon(), atkPokemon));

          if (atkTrainer.allPokemonKnockedOut()) {
            victory(defTrainer);
            return;
          }
        }

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

        this.getPendingBattleUpdate().addSummary(
            new FightSummary(atkPokemon, defPokemon, base.toString(), anim));
      }
    }
  }

  public void victory(Trainer t) {
    System.out.println("Victory for: " + t.getId());

    this.setLastBattleUpdate(this.getPendingBattleUpdate());

    setBattleState(BattleState.DONE);
    sendBattleUpdate();

    winner = t;
    loser = other(t);

    winner.resetAllStatStages();
    loser.resetAllStatStages();

    PacketSender.sendEndBattlePacket(this.getId(), this.getWinner().getId(),
        this.getLoser().getId(), 0, 0);

    updateXp(winner, loser);

    User w = (User) winner;
    User l = (User) loser;

    int oldWinnerElo = w.getElo();
    w.updateElo(true, l.getElo());
    l.updateElo(false, oldWinnerElo);

    Random r = new Random();
    int neg = r.nextBoolean() ? 1 : -1;
    int currency = 50 + (neg * r.nextInt(26));
    if (currency > l.getCurrency()) {
      currency = l.getCurrency();
    }
    l.setCurrency(l.getCurrency() - currency);
    w.setCurrency(w.getCurrency() + currency);
    l.sendMessage(
        String.format("You lost %d coins to %s for losing the battle.%n",
            currency, w.getUsername()));
    w.sendMessage(
        String.format("You gained %d coins from %s for winning the battle.%n",
            currency, l.getUsername()));

    World world = Main.getWorld();
    if (world.getTournament() != null) {
      Tournament tourn = world.getTournament();
      if (tourn.isParticipating(w)) {
        w.getTeam().forEach(p -> {
          p.fullRestore();
          for (Move m : p.getMoves()) {
            m.setPP(m.getPP());
          }
        });
        tourn.logBattleResult(w, l);
      }
    }
    for (User u : Arrays.asList(w, l)) {
      boolean needsHeal = true;
      for (Pokemon p : u.getTeam()) {
        if (p.getCurrHp() != 0) {
          needsHeal = false;
          break;
        }
      }
      if (needsHeal) {
        u.teleportTo(new Location(Main.getWorld().getChunk(2), 30, 31));
      } else if (u.getActivePokemon().isKnockedOut()) {
        for (Pokemon p : u.getTeam()) {
          if (!p.isKnockedOut()) {
            u.setActivePokemon(p);
            break;
          }
        }
      }
    }
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
    if (turnsMap.size() == 2) {
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

  @Override
  public String dbgStatus() {
    StringBuilder sb = new StringBuilder();

    sb.append(a.getActivePokemon()).append(b.getActivePokemon());

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

    PacketSender.sendBattleTurnPacket(getId(), t, this.getLastBattleUpdate(),
        t.getActivePokemon(), other(t).getActivePokemon(),
        ((t.getActivePokemon().isKnockedOut()) ? TURN_STATE.MUST_SWITCH
            : TURN_STATE.NORMAL).ordinal());
  }

  private void sendBattleUpdate() {
    sendBattleUpdateTo(a);
    sendBattleUpdateTo(b);
  }

  @Override
  public void updateXp(Trainer winner, Trainer loser) {
    for (Pokemon winnerP : winner.getTeam()) {
      Double expWon = 0.0;
      if (!winnerP.isKnockedOut()) {
        for (Pokemon loserP : loser.getTeam()) {
          expWon += Pokemon.xpWon(winnerP, loserP);
        }
        expWon *= 1.5;
      }
      winnerP.addExp(expWon.intValue());
    }
  }
}
