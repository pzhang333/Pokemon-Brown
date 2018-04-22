package cs.brown.edu.aelp.networking;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cs.brown.edu.aelp.pokemmo.data.authentication.User;
import cs.brown.edu.aelp.pokemmo.data.authentication.UserManager;
import java.util.Collection;
import java.util.stream.Collectors;

public final class PacketSender {

  private static Gson gson = new Gson();

  private PacketSender() {
  }

  public static void sendGamePackets() {
    Collection<User> users = UserManager.getAllUsers();
    Collection<NetworkUser> nUsers = users.stream().map(User::toNetworkUser)
        .collect(Collectors.toList());
    try {
      for (User u : users) {
        if (u.getSession() != null && u.getSession().isOpen()) {
          GamePacket packet = new GamePacket(u.getLocation().getChunk().getId(),
              nUsers);
          // converting this packet to JSON using Gson
          JsonElement json = gson.toJsonTree(packet, GamePacket.class);

          JsonObject message = new JsonObject();
          message.addProperty("type", 1);
          JsonObject properties = new JsonObject();
          properties.add("game_packet", json);
          message.add("payload", properties);
          // sending the json
          u.getSession().getRemote().sendString(gson.toJson(message));
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
