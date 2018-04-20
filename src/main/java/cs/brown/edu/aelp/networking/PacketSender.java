package cs.brown.edu.aelp.networking;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Queue;
import org.eclipse.jetty.websocket.api.Session;

public final class PacketSender {

  private static Gson gson = new Gson();

  private PacketSender() {
  }

  public static void sendGamePackets(Queue<Session> sessions,
      Map<Session, NetworkUser> sessionToPlayer) {
    sessions.stream().filter(Session::isOpen).forEach(session -> {
      try {
        // System.out.println(sessionToPlayer);
        NetworkUser user = sessionToPlayer.get(session);

        // create our GamePacket object
        GamePacket packet = new GamePacket(user, sessionToPlayer.values());

        // converting this packet to JSON using Gson
        String json = gson.toJson(packet, GamePacket.class);

        JsonObject message = new JsonObject();
        message.addProperty("type", 1);
        JsonObject properties = new JsonObject();
        properties.addProperty("game_packet", json);
        message.add("payload", properties);
        // sending the json
        session.getRemote().sendString(gson.toJson(message));
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

}
