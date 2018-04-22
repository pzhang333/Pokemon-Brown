package cs.brown.edu.aelp.networking;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import cs.brown.edu.aelp.pokemmo.data.DataSource.AuthException;
import cs.brown.edu.aelp.pokemmo.data.authentication.User;
import cs.brown.edu.aelp.pokemmo.data.authentication.UserManager;
import java.io.IOException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class PlayerWebSocketHandler {

  private static final Gson GSON = new Gson();

  public static enum MESSAGE_TYPE {
    CONNECT, INITIALIZE, GAME_PACKET, UPDATE_USER, CLIENT_PLAYER_UPDATE, PLAYER_REQUEST_PATH, PATH_REQUEST_RESPONSE
  }

  public static enum OP_CODES {
    ENTERED_CHUNK, LEFT_CHUNK
  }

  private static final MESSAGE_TYPE[] MESSAGE_TYPES = MESSAGE_TYPE.values();

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
      // authenticate this session
      int id = payload.get("id").getAsInt();
      String token = payload.get("token").getAsString();
      try {
        User u = UserManager.authenticate(id, token);
        u.setSession(session);
        PacketSender.sendInitializationPacket(u);
        // TODO: Inform all other users of their connection?
      } catch (AuthException e1) {
        // their credentials were bad or something went wrong
        session.close();
      }

    case CLIENT_PLAYER_UPDATE:
      // we have received an update from the client

      String playerJson = received.getAsJsonObject("payload").get("player")
          .toString();
      NetworkUser player = GSON.fromJson(playerJson, NetworkUser.class);

      // updating our player object in our map of sessions to players
      // sessionToPlayer.put(session, player);

      // retrieving opcode value
      String opcodeString = received.getAsJsonObject("payload").get("op")
          .toString();
      int opcode = Integer.parseInt(opcodeString);

      if (opcode == 1) {
        // player switched tiles, requires some initialization information
      }
    case PLAYER_REQUEST_PATH:
      // player is requesting a movement along a path

      // TODO: Actually verify the path
      String path = received.getAsJsonObject("payload").get("path").toString();

      // Building the PATH_REQUEST_RESPONSE message
      JsonObject main = new JsonObject();
      main.addProperty("type", 6);
      JsonObject new_payload = new JsonObject();
      payload.addProperty("path", path);
      payload.addProperty("approved", 1);
      main.add("payload", new_payload);

      try {
        session.getRemote().sendString(GSON.toJson(main));
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    default:
      // something went wrong, we got an unknown message type
    }

  }

  // sends the game packets to all open sessions
  public static void sendGamePackets() {
    PacketSender.sendGamePackets();
  }

}