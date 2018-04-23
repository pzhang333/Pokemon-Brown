package cs.brown.edu.aelp.networking;

import com.google.gson.JsonObject;
import cs.brown.edu.aelp.networking.PlayerWebSocketHandler.MESSAGE_TYPE;
import cs.brown.edu.aelp.networking.PlayerWebSocketHandler.OP_CODES;
import cs.brown.edu.aelp.pokemmo.data.authentication.User;
import cs.brown.edu.aelp.pokemmo.map.Chunk;
import cs.brown.edu.aelp.pokemon.Main;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
        if (u.getSession() != null && u.getSession().isOpen()) {
          u.getSession().getRemote()
              .sendStringByFuture(Main.GSON().toJson(message));
        }
      }
    }
  }

  public static void sendInitializationPacket(User u) {
    if (u.getSession() != null && u.getSession().isOpen()) {
      JsonObject message = new JsonObject();
      message.addProperty("type", MESSAGE_TYPE.INITIALIZE.ordinal());
      JsonObject values = new JsonObject();
      values.add("location", Main.GSON().toJsonTree(u.getLocation()));
      // TODO: attach other info we need to know about ourselves immediately
      // after connecting
      List<JsonObject> otherPlayerInfo = new ArrayList<>();
      for (User other : u.getLocation().getChunk().getUsers()) {
        if (u != other) {
          otherPlayerInfo.add(buildPlayerEnteredChunkOp(other));
        }
      }
      values.add("ops", Main.GSON().toJsonTree(otherPlayerInfo));
      message.add("payload", values);
      System.out.println("Sending Initialization Packet:");
      System.out.println(Main.GSON().toJson(message));
      u.getSession().getRemote()
          .sendStringByFuture(Main.GSON().toJson(message));
    }
  }

  private static JsonObject buildPlayerEnteredChunkOp(User u) {
    JsonObject message = new JsonObject();
    message.addProperty("code", OP_CODES.ENTERED_CHUNK.ordinal());
    // TODO: attach other info we need to initialize new players entering your
    // chunk, don't need to attach location info
    return message;
  }

  public static void queueOpForChunk(JsonObject op, int chunkId) {
    if (!chunkOps.containsKey(chunkId)) {
      chunkOps.put(chunkId, new ArrayList<>());
    }
    chunkOps.get(chunkId).add(op);
  }

}
