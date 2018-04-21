
var Game = {

}

Game.init = function() {
	
	//Game.easystar = new EasyStar.js();
	Game.cursors = game.input.keyboard.createCursorKeys();
};

Game.update = function() {

	// We're in a different chunk.
	if (net.getCurrentChunkId() != Game.chunkId) {
		this.loadCurrentChunk();
		return;
	}

	if (this.cursors == undefined) {
		return;
	}

	if (this.cursors.left.isDown) {
		this.player.step('left', (this.cursors.left.shiftKey) ? 2 : 1);
	} else if (this.cursors.right.isDown) {
		this.player.step('right', (this.cursors.right.shiftKey) ? 2 : 1);
	} else if (this.cursors.up.isDown) {
		this.player.step('up', (this.cursors.up.shiftKey) ? 2 : 1)
	} else if (this.cursors.down.isDown) {
		this.player.step('down', (this.cursors.down.shiftKey) ? 2 : 1);
	}
};

Game.preload = function() {
	game.load.image('tileset', 'assets/tilesets/tileset.png');

	game.load.atlasJSONHash('atlas1', 'assets/sprites/pokemon_atlas1.png', 'assets/sprites/pokemon_atlas1.json');
};


Game.create = function() {
	this.loadCurrentChunk();
};

Game.drawLayers = function() {

	/* TODO: Cleanup */
	Game.layerNames = ['Base', 'Walk', 'Walkable', 'Collision', 'Top'];

	Game.map.gameLayers = {};

	for(idx in Game.layerNames) {

		let layerName = Game.layerNames[idx];

		Game.map.gameLayers[layerName] = Game.map.createLayer(layerName);
		Game.map.gameLayers[layerName].resizeWorld();
	}

	Game.map.gameLayers['Base'].inputEnabled = true;
	Game.map.gameLayers['Base'].events.onInputUp.add(Game.handleMapClick, this);

	//Game.objMap = new SparseMap();
	//Game.objMap.add(20, 29, 'Ayy Lmao');

	if (Game.chunkId == 1) {
		Game.doors = new SparseMap();

		Game.doors.add(20, 29, {
			chunk: 2,
			x: 1,
			y: 1
		});

		Game.doors.add(14, 29, {
			chunk: 2,
			x: 5,
			y: 5
		});
	}
};

Game.handleMapClick = function(layer, pointer) {
	let coords = Game.computeTileCoords(pointer.worldX, pointer.worldY + 16);

	/* Hack due to offset */
	if (coords.y == Game.map.height) {
		coords.y--;
	}

	Game.player.prepareMovement(coords);
};

Game.computeTileCoords = function(x, y){
	let layer = Game.map.gameLayers['Base'];
	return new Phaser.Point(layer.getTileX(x), layer.getTileY(y));
};

Game.calculateCollisionMatrix = function(map) {

	/* Hack */

	let collisionMatrix = [];

	collisionMatrix.push(Array(map.height).fill().map(() => 1))

	/* Dank Hack */
	for(let row = 0; row < map.height - 1; row++) {

		let collsionRow = [];

		for(let col = 0; col < map.width; col++) {
			let tile = map.getTile(col, row, 'Collision', true);
			collsionRow.push(tile.index);
		}

		collisionMatrix.push(collsionRow);
	}

	return collisionMatrix;
};

Game.getMapElement = function(x, y, map) {
	if (map == undefined) {
		map = Game.map;
	}

	let coords = Game.computeTileCoords(x, y);
	return map.getFirst(coords.x, coords.y);
};

Game.loadCurrentChunk = function() {

	if (Game.map != undefined) {
		for(idx in Game.layerNames) {
			Game.map.gameLayers[Game.layerNames[idx]].destroy();
		}

		Game.map.destroy();
		game.world.removeAll();
	}

	let self = this;

	net.getChunk(function(chunk) {

		
		console.log(chunk)
		Game.chunkId = chunk.id;

		game.cache.addTilemap(chunk.id, null, chunk.data, Phaser.Tilemap.TILED_JSON);

		Game.map = game.add.tilemap(chunk.id, 16, 16);
		Game.map.addTilesetImage('tileset', 'tileset', 16, 16);
		
		game.world.setBounds(0, 0, Game.map.widthInPixels, Game.map.heightInPixels);

		self.drawLayers();

		Game.collisionMatrix = self.calculateCollisionMatrix(Game.map);
		Game.easystar = new EasyStar.js(); 
		Game.easystar.setGrid(Game.collisionMatrix);
    		Game.easystar.setAcceptableTiles([-1]);
		
		// Hack
		Game.player.initSprite();
		Game.player.setVisible();
		Game.player.setCameraFocus(Game.camera);
	});
};

