package cs.brown.edu.aelp.pokemmo.battle;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cs.brown.edu.aelp.networking.PacketSender;
import cs.brown.edu.aelp.pokemmo.battle.impl.PvPBattle;
import cs.brown.edu.aelp.pokemmo.battle.impl.WildBattle;
import cs.brown.edu.aelp.pokemmo.data.authentication.User;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;
import cs.brown.edu.aelp.pokemmo.pokemon.PokemonLoader;
import cs.brown.edu.aelp.pokemmo.trainer.Trainer;

public class BattleManager {

  private static BattleManager instance = new BattleManager();

  private Integer battleIdCounter = 0;

  private Map<Integer, Battle> battleMap = new ConcurrentHashMap<>();

  public static BattleManager getInstance() {
    return instance;
  }

  private synchronized Integer getNewId() {
    int id = battleIdCounter++;

    return id;
  }

  private Pokemon getWildPokemon(Trainer t) {
    return PokemonLoader.load("bulbasaur", Pokemon.calcXpByLevel(3));
  }

  public WildBattle createWildBattle(User u) {
    int id = getNewId();

    WildBattle battle = new WildBattle(id, new Arena(), u, getWildPokemon(u));

    battleMap.put(id, battle);

    u.setCurrentBattle(battle);

    return battle;
  }

  public PvPBattle createPvPBattle(User u1, User u2) {
    return new PvPBattle(getNewId(), new Arena(), u1, u2);
  }

  public void endBattle(Battle battle) {
	PacketSender.sendEndBattlePacket(battle.getId(), battle.getWinner().getId(), battle.getLoser().getId(), 0, 0);
  }
}
