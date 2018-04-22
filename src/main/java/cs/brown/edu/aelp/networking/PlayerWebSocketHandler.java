package cs.brown.edu.aelp.networking;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.Map;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class PlayerWebSocketHandler {

    private static final Gson GSON = new Gson();
    private static final Queue<Session> sessions = new ConcurrentLinkedQueue<>();
    private static int nextId = 0;
    
    // maps session id's to Player objects
    private static Map<Session, NetworkUser> sessionToPlayer = new ConcurrentHashMap<>();
    
    private static enum MESSAGE_TYPE {
      CONNECT,
      GAME_PACKET,
      UPDATE_USER,
      CLIENT_PLAYER_UPDATE
    }
  
    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception {
      
      sessions.add(session);
      
      // TODO: Update this information on log-in 
      NetworkUser player = new NetworkUser(nextId);
      sessionToPlayer.put(session, player);
      
      // Building the CONNECT message
      JsonObject main = new JsonObject();
      main.addProperty("type", 0);
      JsonObject message = new JsonObject();
      message.addProperty("id", nextId);
      main.add("payload", message);
      
      nextId++;

      // sending connect message
      session.getRemote().sendString(GSON.toJson(main));
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
      // Here we perform any actions we wish to do on a user closing 
      // connection (i.e. saving position to database, etc...)
      sessions.remove(session);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
      // we have received a websocket message
      // this message will be a JSON object (in string form)
      JsonObject received = GSON.fromJson(message, JsonObject.class);
      
      if (received.get("type").getAsInt() == MESSAGE_TYPE.CLIENT_PLAYER_UPDATE.ordinal()) {
        // we have received an update from the client
        
        String playerJson = received.getAsJsonObject("payload").get("player").toString();
        NetworkUser player = GSON.fromJson(playerJson, NetworkUser.class);

        // updating our player object in our map of sessions to players
        sessionToPlayer.put(session, player);
        
        // retrieving opcode value 
        String opcodeString = received.getAsJsonObject("payload").get("op").toString();
        int opcode = Integer.parseInt(opcodeString);
        
        if (opcode == 1) {
          // player switched tiles, requires some intialization information
        }
      }
    }
    
    // sends the game packets to all open sessions
    public static void sendGamePackets() {
      PacketSender.sendGamePackets(sessions, sessionToPlayer);
    }

}