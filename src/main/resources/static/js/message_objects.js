// class containing necessary information about a player
class PlayerUpdateMessage {
    constructor(player, op) {
      this.type = 4;
      this.payload = {"player": player, "op": op};
  }
}

// class containing necessary information for requesting
// player movement
class RequestPathMessage {
    constructor(path) {
      this.type = 5;
      this.payload = {"path": path};
  }
}