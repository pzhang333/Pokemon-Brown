// class containing necessary information about a player
class PlayerUpdateMessage {
    constructor(player, op) {
      this.type = 4;
      this.payload = {"player": player, "op": op};
  }
}