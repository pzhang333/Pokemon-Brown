// class containing necessary information about a player
class Player {
    constructor(uuid, position, playerState) {
      this.uuid = uuid;
      this.position = position;
      this.playerState = playerState;
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