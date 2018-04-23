package cs.brown.edu.aelp.networking;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cs.brown.edu.aelp.pokemmo.data.DataSource.AuthException;
import cs.brown.edu.aelp.pokemmo.data.authentication.User;
import cs.brown.edu.aelp.pokemmo.data.authentication.UserManager;
import cs.brown.edu.aelp.pokemmo.map.Chunk;
import cs.brown.edu.aelp.pokemmo.map.Location;
import cs.brown.edu.aelp.pokemmo.map.Path;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class PlayerWebSocketHandler {

  private static final Gson GSON = new Gson();

  public static enum MESSAGE_TYPE {
    CONNECT, INITIALIZE, GAME_PACKET, PLAYER_REQUEST_PATH, UPDATE_USER, CLIENT_PLAYER_UPDATE, PATH_REQUEST_RESPONSE
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
    int id;

    switch (MESSAGE_TYPES[received.get("type").getAsInt()]) {
    case CONNECT:
      // authenticate this session
      id = payload.get("id").getAsInt();
      String token = payload.get("token").getAsString();
      try {
        User u = UserManager.authenticate(id, token);
        u.setSession(session);
        System.out.println("Authenticated socket by packet: " + message);
        PacketSender.sendInitializationPacket(u);
        // TODO: Inform all other users of their connection?
      } catch (AuthException e1) {
        // their credentials were bad or something went wrong
        System.out.println(e1.getMessage());
        session.close();
      }
      break;

    case CLIENT_PLAYER_UPDATE:
      // we have received an update from the client

      String playerJson = payload.get("player").toString();
      // NetworkUser networkUser = GSON.fromJson(playerJson, NetworkUser.class);

      // TODO: confirm session matches with user session
      int uid = payload.get("id").getAsInt();
      User userToUpdate = UserManager.getUserById(uid);

      // verifying that the session matches the user session
      if (userToUpdate.getSession().equals(session)) {
        // userToUpdate.updateFromNetworkUser(networkUser);
      } else {
        System.out.println("ERROR: Requesting user id does not match session.");
      }

      // updating our player object in our map of sessions to players
      // sessionToPlayer.put(session, player);

      // retrieving opcode value
      String opcodeString = received.getAsJsonObject("payload").get("op")
          .toString();
      int opcode = Integer.parseInt(opcodeString);

      if (opcode == 1) {
        // player switched tiles, requires some initialization information
      }
      break;
    case PLAYER_REQUEST_PATH:
      // TODO: Actually verify the path, maybe?...
      id = payload.get("id").getAsInt();
      User u = UserManager.getUserById(id);
      if (u == null || u.getSession() != session) {
        session.close();
        break;
      }
      JsonArray path = received.getAsJsonObject("payload")
          .getAsJsonArray("path");
      Chunk c = u.getLocation().getChunk();
      List<Location> locs = new ArrayList<>();
      for (JsonElement o : path) {
        JsonObject tile = (JsonObject) o;
        Location loc = new Location(c, tile.get("row").getAsInt(),
            tile.get("col").getAsInt());
        locs.add(loc);
      }
      u.setPath(new Path(locs));
      break;
    default:
      // something went wrong, we got an unknown message type
    }

  }

  // sends the game packets to all open sessions
  public static void sendGamePackets() {
    PacketSender.sendGamePackets();
  }

}