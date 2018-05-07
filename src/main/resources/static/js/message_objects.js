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

class UpdateTeamMessage {
	constructor(id, team) {
		this.type = 15;
		this.payload = {"id": id, "pokemon": team};
	}
}

// Trading
class RequestTradeMessage {
	constructor(id, other_id) {
		this.type = 5;
		this.payload = {"id": id, "other_id": other_id, "starter": true};
	}
}

class UpdateTradeMessage {
	constructor(id, other_id, me_accepted, me_currency, me_pokemon, other_currency, other_pokemon) {
		this.type = 5;
		// note: me_pokemon and other_pokemon are ids, not objects
		this.payload = {"id": id, "other_id": other_id, "me_accepted": me_accepted, "me_currency": me_currency, "me_pokemon": me_pokemon, "other_currency": other_currency, "other_pokemon": other_pokemon};
	}
}

// Item Purchaing

class ItemPurchaseMessage {
	constructor(id, item_id, quantity) {
		this.type = 17;
		this.payload = {"id": id, "item_id": item_id, "quantity": quantity};
	}
}