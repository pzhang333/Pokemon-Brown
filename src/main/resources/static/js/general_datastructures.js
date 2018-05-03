// class containing necessary information about a player
class NetworkUser {
    constructor(uid, location, playerState, orientation) {
      this.uid = uid;
      this.location = location;
      this.playerState = playerState;
      this.orientation = orientation;
  }
}

// class describing a three-dimensional point
class Point3 {
    constructor(x, y, z) {
      this.x = x;
      this.y = y;
      this.z = z;
  }
}

class Chunk {
	constructor(id, data) {
		this.id = id;
		this.data = data;
	}
}

class EloUser {
  constructor(name, elo) {
    this.name = name;
    this.elo = elo;
  }
}