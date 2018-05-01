package cs.brown.edu.aelp.networking;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.websocket.api.WebSocketException;

import com.google.gson.JsonObject;

import cs.brown.edu.aelp.networking.PlayerWebSocketHandler.MESSAGE_TYPE;
import cs.brown.edu.aelp.networking.PlayerWebSocketHandler.OP_CODES;
import cs.brown.edu.aelp.pokemmo.battle.Battle;
import cs.brown.edu.aelp.pokemmo.battle.BattleManager;
import cs.brown.edu.aelp.pokemmo.battle.BattleUpdate;
import cs.brown.edu.aelp.pokemmo.battle.impl.PvPBattle;
import cs.brown.edu.aelp.pokemmo.battle.impl.WildBattle;
import cs.brown.edu.aelp.pokemmo.data.authentication.User;
import cs.brown.edu.aelp.pokemmo.data.authentication.UserManager;
import cs.brown.edu.aelp.pokemmo.map.Chunk;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;
import cs.brown.edu.aelp.pokemmo.trainer.Trainer;
import cs.brown.edu.aelp.pokemon.Main;

public final class PacketSender {

  private static Map<Integer, List<JsonObject>> chunkOps = new ConcurrentHashMap<>();

  private PacketSender() {
  }

  public static void sendGamePackets() {
    for (Chunk c : Main.getWorld().getAllChunks()) {
      JsonObject message = new JsonObject();
      // set the type
      message.addProperty("type", MESSAGE_TYPE.GAME_PACKET.ordinal());
      JsonObject payload = new JsonObject();
      // add data on all users from this chunk
      payload.add("users", Main.GSON().toJsonTree(c.getUsers()));
      // add any additional op codes
      if (chunkOps.containsKey(c.getId())) {
        payload.add("ops", Main.GSON().toJsonTree(chunkOps.get(c.getId())));
        chunkOps.remove(c.getId());
      }
      message.add("payload", payload);
      // send to each user that has an open session
      for (User u : c.getUsers()) {
        sendPacket(u, message);
      }
    }
  }

  public static JsonObject buildPlayerOpMessage(User u, OP_CODES code) {
    JsonObject message = new JsonObject();
    message.addProperty("code", code.ordinal());
    message.addProperty("id", u.getId());
    if (code == OP_CODES.ENTERED_CHUNK) {
      message.addProperty("username", u.getUsername());
    } else if (code == OP_CODES.LEFT_CHUNK) {
      // ...
    } else if (code == OP_CODES.ENTERED_BATTLE) {
      // ...
    } else if (code == OP_CODES.LEFT_BATTLE) {
      // ...
    }
    return message;
  }

  public static void queueOpForChunk(JsonObject op, Chunk c) {
    if (!chunkOps.containsKey(c.getId())) {
      chunkOps.put(c.getId(), new ArrayList<>());
    }
    chunkOps.get(c.getId()).add(op);
  }

  public static void sendInitializationPacket(User u) {
    JsonObject message = new JsonObject();
    message.addProperty("type", MESSAGE_TYPE.INITIALIZE.ordinal());
    JsonObject values = new JsonObject();
    values.add("location", Main.GSON().toJsonTree(u.getLocation()));
    List<JsonObject> otherPlayerInfo = new ArrayList<>();
    for (User other : u.getLocation().getChunk().getUsers()) {
      if (u.isConnected()) {
        JsonObject user_data = new JsonObject();
        user_data.addProperty("id", other.getId());
        user_data.addProperty("username", other.getUsername());
        otherPlayerInfo.add(user_data);
      }
    }
    values.add("players", Main.GSON().toJsonTree(otherPlayerInfo));
    message.add("payload", values);
    queueOpForChunk(buildPlayerOpMessage(u, OP_CODES.ENTERED_CHUNK),
        u.getLocation().getChunk());
    sendPacket(u, message);
  }

  private static JsonObject buildStartBattlePacket(Battle b, User u,
      Pokemon opp, boolean pvp, String bg) {
    JsonObject packet = new JsonObject();
    packet.addProperty("type", MESSAGE_TYPE.START_BATTLE.ordinal());
    JsonObject payload = new JsonObject();
    payload.add("pokemon_a", Main.GSON().toJsonTree(u.getActivePokemon()));
    payload.add("pokemon_team", Main.GSON().toJsonTree(u.getTeam()));
    payload.add("pokemon_b", Main.GSON().toJsonTree(opp));
    payload.addProperty("battle_id", b.getId());
    payload.addProperty("pvp", pvp);
    payload.addProperty("background_name", bg);
    payload.add("location", Main.GSON().toJsonTree(u.getLocation()));
    packet.add("payload", payload);
    return packet;
  }

  public static void sendEncounterPacket(User u) {
    WildBattle b = BattleManager.getInstance().createWildBattle(u);
    JsonObject packet = buildStartBattlePacket(b, u, b.getWildPokemon(), false,
        "bg-meadow");
    sendPacket(b.getUser(), packet);
    queueOpForChunk(buildPlayerOpMessage(u, OP_CODES.ENTERED_BATTLE),
        u.getLocation().getChunk());
  }

  public static void sendPvPPacket(User u1, User u2) {
    PvPBattle b = BattleManager.getInstance().createPvPBattle(u1, u2);
    JsonObject p1 = buildStartBattlePacket(b, u1, u2.getActivePokemon(), true,
        "bg-meadow");
    JsonObject p2 = buildStartBattlePacket(b, u2, u1.getActivePokemon(), true,
        "bg-meadow");
    sendPacket(u1, p1);
    sendPacket(u2, p2);
    queueOpForChunk(buildPlayerOpMessage(u1, OP_CODES.ENTERED_BATTLE),
        u1.getLocation().getChunk());
    queueOpForChunk(buildPlayerOpMessage(u2, OP_CODES.ENTERED_BATTLE),
        u2.getLocation().getChunk());
  }

  public static void sendTradePacket(User u, Trade t) {
    JsonObject packet = new JsonObject();
    packet.addProperty("type", MESSAGE_TYPE.TRADE.ordinal());
    packet.add("payload", Main.GSON().toJsonTree(t));
    sendPacket(u, packet);
  }

  public static void sendEndBattlePacket(int battleId, int winnerId,
      int loserId, int winnerCurrencyWon, int loserCurrencyLost) {

    JsonObject message = new JsonObject();

    // set the type
    message.addProperty("type", MESSAGE_TYPE.END_BATTLE.ordinal());

    // configure the payload
    JsonObject payload = new JsonObject();
    payload.addProperty("winner_id", winnerId);
    payload.addProperty("loser_id", loserId);
    payload.addProperty("battle_id", battleId);
    payload.addProperty("winner_currency_won", winnerCurrencyWon);
    payload.addProperty("loser_currency_lost", loserCurrencyLost);

    // adding the payload to the message
    message.add("payload", payload);

    if (winnerId != -1) {
      User usr = UserManager.getUserById(winnerId);
      sendPacket(usr, message);
      queueOpForChunk(buildPlayerOpMessage(usr, OP_CODES.LEFT_BATTLE),
          usr.getLocation().getChunk());
    }

    if (loserId != -1) {
      User usr = UserManager.getUserById(loserId);
      sendPacket(usr, message);
      queueOpForChunk(buildPlayerOpMessage(usr, OP_CODES.LEFT_BATTLE),
          usr.getLocation().getChunk());
    }
  }

  public static void sendBattleTurnPacket(int battleId, Trainer to,
      BattleUpdate eventDescription, Pokemon a, Pokemon b, int gameState) {

    JsonObject message = new JsonObject();

    // set the type
    message.addProperty("type", MESSAGE_TYPE.BATTLE_TURN_UPDATE.ordinal());

    // configure the payload
    JsonObject payload = new JsonObject();
    payload.add("pokemon_a", Main.GSON().toJsonTree(a));
    payload.add("pokemon_b", Main.GSON().toJsonTree(b));
    payload.addProperty("battle_id", battleId);
    payload.add("update", Main.GSON().toJsonTree(eventDescription));
    payload.addProperty("game_state", gameState);
    payload.add("pokemon_team", Main.GSON().toJsonTree(to.getTeam()));

    // adding the payload to the message
    message.add("payload", payload);

    if (User.class.isInstance(to)) {
      sendPacket((User) to, message);
    }

    /*
     * if (User.class.isInstance(b.getOwner())) { User usr = (User)
     * b.getOwner(); sendPacket(usr, message); }
     */
  }

  private static void sendPacket(User u, JsonObject message) {
    if (u.isConnected()) {
      try {
        u.getSession().getRemote()
            .sendStringByFuture(Main.GSON().toJson(message));
      } catch (WebSocketException e) {
        System.out.println(
            "WARNING: Tried to send packet to user but socket exception occurred.");
      }
    }
  }

  public static void sendServerMessagePacket(User u, String msg) {
    JsonObject packet = new JsonObject();
    packet.addProperty("type", MESSAGE_TYPE.SERVER_MESSAGE.ordinal());
    JsonObject payload = new JsonObject();
    payload.addProperty("message", msg);
    packet.add("payload", payload);
    sendPacket(u, packet);
  }

}
