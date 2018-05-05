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

class RequestChallengeMessage {
    constructor(id, challengedId) {
      this.type = 12;
      this.payload = {"id": id, "challenged_id": challengedId};
  }
}
// to cancel challenge: 
// send challenge packet with only new id and nothing else

class ChallengeResponseMessage {
    constructor(id, accepted) {
      this.type = 13;
      this.payload = {"id": id, "accepted": accepted};
  }
}