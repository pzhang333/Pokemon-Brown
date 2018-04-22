// class containing necessary information about a player
class NetworkPlayer {
    constructor(uuid, position, playerState, orientation) {
      this.uuid = uuid;
      this.position = position;
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