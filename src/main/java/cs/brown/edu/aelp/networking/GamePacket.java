package cs.brown.edu.aelp.networking;

import cs.brown.edu.aelp.pokemmo.data.authentication.User;
import cs.brown.edu.aelp.pokemmo.map.Location;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class GamePacket {

  private Collection<User> otherPlayers;
  private User player;

  public GamePacket(User player, Collection<User> otherPlayers,
      Map<Location, Integer> backgroundMap) {

    this.setOtherPlayers(localize(player.getLocation(), otherPlayers));
    this.setPlayer(player);
  }

  // Helper functions:

  private Collection<User> localize(Location playerPosition,
      Collection<User> players) {
    return players.stream()
        .filter(p -> p.getLocation().getChunk() == playerPosition.getChunk())
        .collect(Collectors.toList());
  }

  // Getters and Setters for Package Properties:

  public Collection<User> getOtherPlayers() {
    return otherPlayers;
  }

  public void setOtherPlayers(Collection<User> otherPlayers) {
    this.otherPlayers = otherPlayers;
  }

  public User getPlayer() {
    return player;
  }

  public void setPlayer(User player) {
    this.player = player;
  }

}
