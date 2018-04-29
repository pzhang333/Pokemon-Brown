
var Game = {
	players: {}
}

Game.init = function() {
	
	Game.ready = false;
	//Game.easystar = new EasyStar.js();
	
};

Game.shutdown = function() {
	game.world.removeAll();
	Game.chunkId = false;
	Game.player.sprite = undefined;
	Game.ready = false;
}

Game.update = function() {

	// We're in a different chunk.
	let chunkId = net.getCurrentChunkId();
	if (chunkId != Game.chunkId) {
		this.loadCurrentChunk(true);
		//Game.chunkId = net.chunkId;
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
	game.stage.disableVisibilityChange = true;
	game.load.image('tileset', 'assets/tilesets/tileset.png');
	game.load.atlasJSONHash('atlas1', 'assets/sprites/pokemon_atlas1.png', 'assets/sprites/pokemon_atlas1.json');

	game.load.atlasJSONHash('atlas1', 'assets/sprites/pokemon_atlas1.png', 'assets/sprites/pokemon_atlas1.json');
	game.load.atlasJSONHash('atlas2', 'assets/sprites/pokemon_atlas2.png', 'assets/sprites/pokemon_atlas2.json');
	game.load.atlasJSONHash('attacks', 'assets/pokemon/attacks.png', 'assets/pokemon/attacks.json');
	
	game.load.audio('battle', ['assets/audio/battle.mp3']);
	
	// loading hud images
    game.load.image('backpack', 'assets/HUD/backpack.png');
    game.load.image('trophy', 'assets/HUD/trophy.png');
    game.load.image('coin', 'assets/HUD/coin.png');
};


Game.create = function() {
	this.loadCurrentChunk(true);
	
	Game.ready = true;
	game.camera.roundPx = true;
};

Game.drawLayers = function() {

	/* TODO: Cleanup */
	Game.layerNames = ['Base', 'Walk', 'Collision', 'Top', 'Bush'];

	Game.map.gameLayers = {};

	for(idx in Game.layerNames) {

		let layerName = Game.layerNames[idx];

		Game.map.gameLayers[layerName] = Game.map.createLayer(layerName);
		Game.map.gameLayers[layerName].resizeWorld();
	}

	Game.map.gameLayers['Base'].inputEnabled = true;
	Game.map.gameLayers['Base'].events.onInputUp.add(Game.handleMapClick, this);
	self.drawHud();
};

Game.handleMapClick = function(layer, pointer) {
	
	let coords = Game.computeTileCoords(pointer.worldX, Math.ceil((pointer.worldY - 16) / 16) * 16);

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

	//collisionMatrix.push(Array(map.height).fill().map(() => 1))

	/* Dank Hack */
	for(let row = 0; row < map.height; row++) {

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

Game.clearPlayers = function() {
	for (var key in Game.players) {
	    if (Game.players.hasOwnProperty(key)) {      
	    	
	    	// Hack
	    	let id = parseInt(key);
	    	let player = Game.players[id]
	    	
	    	if (player != undefined) {
	    		player.del();
	    	}
	    	

    		//Game.players[id] = undefined;
	    }
	}
}

Game.loadCurrentChunk = function(clear) {

	Game.chunkId = false;
	
	if (Game.map != undefined) {
		for(idx in Game.layerNames) {
			Game.map.gameLayers[Game.layerNames[idx]].destroy();
		}
		

		Game.map.destroy();
		game.world.removeAll();
	}

	let self = this;

	Game.clearPlayers();
	
	net.getChunk(function(chunk) {
		Game.clearPlayers();
		
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
		Game.clearPlayers();
		
		Game.cursors = game.input.keyboard.createCursorKeys();
		
		Game.chunkId = net.chunkId;
		
	});
};

function drawHud() {
	// hud grey bar
	//floor = new Phaser.Rectangle(0, 550, Game.map.widthInPixels, Game.map.heightInPixels);
    //game.debug.geom(floor,'#0fffff');


	// backpack icon
	let backpackIcon = game.add.sprite(Game.map.widthInPixels-Game.map.widthInPixels/6.5, Game.map.heightInPixels-Game.map.heightInPixels/2, "trophy");
    backpackIcon.inputEnabled = true;
    backpackIcon.fixedToCamera = true;

    // trophy icon
   	let trophyIcon = game.add.sprite(Game.map.widthInPixels-Game.map.widthInPixels/4, Game.map.heightInPixels-Game.map.heightInPixels/2, "backpack");
    trophyIcon.inputEnabled = true;
    trophyIcon.fixedToCamera = true;

    // coin icon
    let coinIcon = game.add.sprite(Game.map.widthInPixels-Game.map.widthInPixels/2.85, Game.map.heightInPixels-Game.map.heightInPixels/2, "coin");
    coinIcon.inputEnabled = true;
    coinIcon.fixedToCamera = true;
}
