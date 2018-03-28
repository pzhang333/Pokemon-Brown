package cs.brown.edu.aelp.networking;

import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;

import cs.brown.edu.aelp.Player.Player;
import cs.brown.edu.aelp.pokemon.Main;

@WebSocket
public class PlayerWebSocketHandler {

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
      String uuid = "uid:" + Main.sessionNumber;
      Main.sessionNumber++;
      // here we should do some login checks
      
      Main.sessionToPlayer.put(user, new Player(uuid));
          
     // Additional stuff to do on connect
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
      // Here we perform any actions we wish to do on a user closing 
      // connection (i.e. saving position to database, etc...)
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
        // stuff to do on websocket message
    }

}