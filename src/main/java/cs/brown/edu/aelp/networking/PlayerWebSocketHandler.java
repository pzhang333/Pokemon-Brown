package cs.brown.edu.aelp.networking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cs.brown.edu.aelp.networking.Trade.TRADE_STATUS;
import cs.brown.edu.aelp.pokemmo.battle.Battle;
import cs.brown.edu.aelp.pokemmo.battle.BattleManager;
import cs.brown.edu.aelp.pokemmo.battle.Battle.BattleState;
import cs.brown.edu.aelp.pokemmo.battle.action.FightTurn;
import cs.brown.edu.aelp.pokemmo.battle.action.Turn;
import cs.brown.edu.aelp.pokemmo.data.DataSource.AuthException;
import cs.brown.edu.aelp.pokemmo.data.authentication.User;
import cs.brown.edu.aelp.pokemmo.data.authentication.UserManager;
import cs.brown.edu.aelp.pokemmo.map.Chunk;
import cs.brown.edu.aelp.pokemmo.map.Location;
import cs.brown.edu.aelp.pokemmo.map.Path;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.Move;

@WebSocket
public class PlayerWebSocketHandler {

  private static final Gson GSON = new Gson();

  public static enum MESSAGE_TYPE {
    CONNECT, INITIALIZE, GAME_PACKET, PLAYER_REQUEST_PATH, ENCOUNTERED_POKEMON,
    TRADE, START_BATTLE, END_BATTLE, BATTLE_TURN_UPDATE, CLIENT_BATTLE_UPDATE,
    CHAT, SERVER_MESSAGE
  }

  public static enum OP_CODES {
    ENTERED_CHUNK, LEFT_CHUNK, ENTERED_BATTLE, LEFT_BATTLE, CHAT
  }

  // used for battle moves

  public static enum ACTION_TYPE {
    RUN, SWITCH, USE_ITEM, FIGHT
  }

  private static final MESSAGE_TYPE[] MESSAGE_TYPES = MESSAGE_TYPE.values();
  private static final ACTION_TYPE[] ACTION_TYPES = ACTION_TYPE.values();

  private static Map<Integer, Trade> trades = new HashMap<>();

  @OnWebSocketConnect
  public void onConnect(Session session) throws Exception {
    // do we actually need to do anything here?
  }

  @OnWebSocketClose
  public void onClose(Session session, int statusCode, String reason) {
    // Here we perform any actions we wish to do on a user closing
    // connection (i.e. saving position to database, etc...)

  }

  @OnWebSocketMessage
  public void onMessage(Session session, String message) {
    // we have received a websocket message
    // this message will be a JSON object (in string form)
    JsonObject received = GSON.fromJson(message, JsonObject.class);
    JsonObject payload = received.getAsJsonObject("payload");

    switch (MESSAGE_TYPES[received.get("type").getAsInt()]) {
    case CONNECT:
      handleConnect(session, payload);
      break;
    case PLAYER_REQUEST_PATH:
      handlePath(session, payload);
      break;
    case TRADE:
      handleTrade(session, payload);
      break;
    case CLIENT_BATTLE_UPDATE:
      handleClientBattleUpdate(session, payload);
      break;
    case CHAT:
      handleChat(session, payload);
      break;
    default:
      // something went wrong, we got an unknown message type
    }

  }

  private static void handleConnect(Session session, JsonObject payload) {
    // authenticate this session
    int id = payload.get("id").getAsInt();
    String token = payload.get("token").getAsString();
    try {
      User u = UserManager.authenticate(id, token);
      u.setSession(session);
      PacketSender.sendInitializationPacket(u);
      System.out.println(u.getUsername() + " connected.");
      // TODO: Inform all other users of their connection?
    } catch (AuthException e1) {
      // their credentials were bad or something went wrong
      session.close();
    }
  }

  private static void handlePath(Session session, JsonObject payload) {
    // TODO: Actually verify the path, maybe?...
    int id = payload.get("id").getAsInt();
    User u = UserManager.getUserById(id);
    if (u == null || u.getSession() != session) {
      session.close();
      return;
    }
    JsonArray path = payload.getAsJsonArray("path");
    Chunk c = u.getLocation().getChunk();
    List<Location> locs = new ArrayList<>();
    for (JsonElement o : path) {
      JsonObject tile = (JsonObject) o;
      Location loc = new Location(c, tile.get("row").getAsInt(),
          tile.get("col").getAsInt());
      locs.add(loc);
    }
    u.setPath(new Path(locs, locs.get(0).getChunk().getEntities(u)));
  }

  private static void handleTrade(Session session, JsonObject payload) {
    int me_id = payload.get("me_id").getAsInt();
    int other_id = payload.get("other_id").getAsInt();
    boolean isUser1 = payload.get("starter").getAsBoolean();
    User me = UserManager.getUserById(me_id);
    User other = UserManager.getUserById(other_id);
    if (me == null || me.getSession() != session) {
      session.close();
      return;
    }
    if (other == null || !other.isConnected()) {
      Trade t = trades.remove(me_id);
      if (t == null) {
        // dummy trade just to say CANCELED
        t = new Trade(me, me);
      }
      t.setStatus(TRADE_STATUS.CANCELED);
      PacketSender.sendTradePacket(me, t);
      return;
    }
    Trade t = trades.get(me_id);
    if (trades.containsKey(other_id) && !trades.get(other_id).involves(me)) {
      // other is busy, tell me with dummy trade
      t = new Trade(me, other);
      t.setStatus(TRADE_STATUS.BUSY);
      PacketSender.sendTradePacket(me, t);
      return;
    }

    if (t == null) {
      t = new Trade(me, other);
    }
    boolean me_accepted = payload.get("me_accepted").getAsBoolean();
    int me_curr = payload.get("me_currency").getAsInt();
    Map<Integer, Integer> me_items = new HashMap<>();
    JsonObject items = payload.get("me_items").getAsJsonObject();
    for (String key : items.keySet()) {
      int item_id = Integer.parseInt(key);
      me_items.put(item_id, items.get(key).getAsInt());
    }
    Set<Integer> me_pokemon = new HashSet<>();
    JsonArray pokemon = payload.get("me_pokemon").getAsJsonArray();
    for (JsonElement o : pokemon) {
      me_pokemon.add(o.getAsInt());
    }
    if (!t.setCurrency(me_curr, isUser1) || !t.setItems(me_items, isUser1)
        || !t.setPokemon(me_pokemon, isUser1)) {
      t.setStatus(TRADE_STATUS.CANCELED);
      session.close();
      System.out.println(
          "WARNING: User %d tried to trade items, pokemon, or currency that they don't have.");
    } else {
      trades.put(other_id, t);
      trades.put(me_id, t);
    }
    if (me_accepted && t.isSameTrade(payload, isUser1)) {
      t.setAccepted(isUser1);
    }
    PacketSender.sendTradePacket(me, t);
    PacketSender.sendTradePacket(other, t);
  }

  private static void handleClientBattleUpdate(Session session,
      JsonObject payload) {

    int turnId = payload.get("turn_id").getAsInt();

    int id = payload.get("id").getAsInt();
    User user = UserManager.getUserById(id);
    if (!user.isConnected() || !user.getSession().equals(session)) {
      System.err.println("Bad Session");
      session.close();
    }

    Turn t = null;
    switch (ACTION_TYPES[payload.get("action").getAsInt()]) {
    case RUN:
      // TODO: run
      System.out.println("User ran away");
      break;
    case SWITCH:
      // TODO: switch
      break;
    case USE_ITEM:
      // TODO: use item
      break;
    case FIGHT:

      Integer moveId = payload.get("moveId").getAsInt();
      System.out.println("Move ID: " + moveId);
      List<Move> moves = user.getActivePokemon().getMoves();
      for (Move m : moves) {
        // System.out.println(m);
        if (m.getId() == moveId) {
          t = new FightTurn(user, m);
          break;
        }
      }

      break;
    default:
      System.out.println("ERROR: Invalid packet sent to battle handler.");
      session.close();
    }

    Battle battle = user.getCurrentBattle();

    System.out.println(battle.getBattleState());

    synchronized (battle) {

      if (t == null || !battle.getBattleState().equals(BattleState.WAITING)) {
        System.err.println("Not waiting");
        session.close();
      }

      if (!battle.setTurn(t)) {
        System.err.println("Bad Turn");
        session.close();
      }

      if (battle.getBattleState().equals(BattleState.READY)) {
        battle.evaluate();

        System.out.println(battle.dbgStatus());
      }
      
      if (battle.getBattleState().equals(BattleState.DONE)) {
    	  BattleManager.getInstance().endBattle(battle);
      }
    }
  }

  private static void handleChat(Session session, JsonObject payload) {
    int id = payload.get("id").getAsInt();
    User u = UserManager.getUserById(id);
    if (u == null || u.getSession() != session) {
      session.close();
      return;
    }
    String msg = payload.get("message").getAsString();
    JsonObject chat = PacketSender.buildPlayerOpMessage(u, OP_CODES.CHAT);
    chat.addProperty("message", msg);
    PacketSender.queueOpForChunk(chat, u.getLocation().getChunk());
  }
}
