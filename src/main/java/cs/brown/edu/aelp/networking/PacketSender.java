package cs.brown.edu.aelp.networking;

import java.util.Collections;

import org.eclipse.jetty.websocket.api.Session;

import com.google.gson.Gson;

import cs.brown.edu.aelp.Player.Player;
import cs.brown.edu.aelp.pokemon.Main;

public final class PacketSender {

  private PacketSender() {
  }

  public static void sendGamePackets() {
    Main.sessionToPlayer.keySet().stream().filter(Session::isOpen).forEach(session -> {
      try {
        Player player = Main.sessionToPlayer.get(session);

        // create our GamePacket object
        // TODO: Figure out background stuff (currently just passing empty map)
        GamePacket packet = new GamePacket(player.getPosition(), 
            Main.playerPositionMap, player.getUserState(), Collections.emptyMap());
        
        // converting this packet to JSON using Gson
        Gson gson = new Gson();
        String json = gson.toJson(packet, GamePacket.class);
        
        // sending the json
        session.getRemote().sendString(json);

      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

}
