// class containing necessary information about a player
class PlayerUpdateMessage {
    constructor(player) {
      this.type = 4;
      this.payload = player;
  }
}