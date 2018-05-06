package cs.brown.edu.aelp.pokemmo.battle;

import cs.brown.edu.aelp.pokemmo.battle.impl.PvPBattle;
import cs.brown.edu.aelp.pokemmo.battle.impl.WildBattle;
import cs.brown.edu.aelp.pokemmo.data.authentication.User;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;
import cs.brown.edu.aelp.pokemmo.pokemon.RandomWildPokemon;
import cs.brown.edu.aelp.pokemmo.trainer.Trainer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    Double levels = 0.0;
    for (Pokemon p : t.getTeam()) {
      levels += 1.0 * p.getLevel();
    }
    levels = levels / t.getTeam().size();
    int avgLvl = levels.intValue();

    return RandomWildPokemon.generateWildPokemon(avgLvl);
  }

  public WildBattle createWildBattle(User u) {
    if (u.isInBattle()) {
      return null;
    }
    int id = getNewId();

    WildBattle battle = new WildBattle(id, new Arena(), u, getWildPokemon(u));

    battleMap.put(id, battle);

    return battle;
  }

  public PvPBattle createPvPBattle(User u1, User u2) {
    if (u1.isInBattle() || u2.isInBattle()) {
      return null;
    }
    PvPBattle b = new PvPBattle(getNewId(), new Arena(), u1, u2);

    battleMap.put(b.getId(), b);

    return b;
  }
}
