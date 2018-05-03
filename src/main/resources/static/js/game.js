
let toDrawLeaderboard = false;
let toDrawBackpack = false;
let coinNumber = 100;

var Game = {
	players: {}
}

Game.init = function() {

	Game.ready = false;
	//Game.easystar = new EasyStar.js();
	

};

Game.shutdown = function() {
	console.log('test!');
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

	if (this.cursors == undefined || Battle.inBattle) {
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
    game.load.image('pokeball', 'assets/HUD/pokeball.png');

    // loading font assets
    game.load.bitmapFont('carrier_command', 'assets/fonts/carrier_command.png', 'assets/fonts/carrier_command.xml');
	
	Battle.preload();
};


Game.create = function() {
	this.loadCurrentChunk(true);

	Game.ready = true;
	game.camera.roundPx = true;
};

Game.moveGroupTo = function(parent,group,endPos){
    // parent is the Phaser Group that contains the group to move (default: world)
    // group is the Phaser Group to be moved
    // endPos is the position (integer) at which to move it
    // if endPos is some group's z value, the moved group will be right below (visually) that group
    // This manipulation is needed because the rendering order and visual overlap of the sprites depend of the order of their groups
    var startPos = group.z-1;
    var diff = startPos-endPos;
    if(diff > 0){
        for(diff; diff > 0; diff--){
            parent.moveDown(group);
        }
    }else if(diff < 0){
        for(diff; diff < 0; diff++){
            parent.moveUp(group);
        }
    }
};


Game.drawLayers = function() {

	/* TODO: Cleanup */
	Game.layerNames = ['Base', 'Walk', 'Walkable', 'Collision', 'Top'];//, 'Bush'];
	Game.map.gameLayers = {};

	for(idx in Game.layerNames) {

		let layerName = Game.layerNames[idx];

		let group = (layerName != 'Top') ? Game.groundMapLayers : Game.highMapLayers;
		
		Game.map.gameLayers[layerName] = Game.map.createLayer(layerName, 0, 0, group);
		Game.map.gameLayers[layerName].resizeWorld();
	}

	Game.map.gameLayers['Base'].inputEnabled = true;
	Game.map.gameLayers['Base'].events.onInputUp.add(Game.handleMapClick, this);
	Game.drawHud();
	
	/*Game.moveGroupTo(game.world, Game.groundMapLayers, 0);
	Game.moveGroupTo(game.world, Game.entities, 1);
	Game.moveGroupTo(game.world, Game.highMapLayers, 2);
	Game.moveGroupTo(game.world, Game.HUD, 200);*/
	
	//game.world.sendToBack(Game.entities);
	//game.world.sendToBack(Game.highMapLayers);
	//game.world.sendToBack(Game.groundMapLayers);
	/*game.world.bringToTop(Game.entities);
	
	game.world.bringToTop(Game.highMapLayers);
	game.world.bringToTop(Game.HUD);*/
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
		
		if (Game.groundMapLayers != undefined) {
			Game.groundMapLayers.destroy();
		}
		Game.groundMapLayers = game.add.group();
		

		if (Game.entities != undefined) {
			Game.entities.destroy();
		}
		Game.entities = game.add.group();
		
		if (Game.highMapLayers != undefined) {
			Game.highMapLayers.destroy();
		}
		Game.highMapLayers = game.add.group();
		
		
		if (Game.HUD != undefined) {
			Game.HUD.destroy();
		}
		Game.HUD = game.add.group();
		
		
		
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

	    // loading necessary libraries for drawing hud menu
	    Game.slickUI = game.plugins.add(Phaser.Plugin.SlickUI);
		Game.slickUI.load('ui/kenney/kenney.json');
	});
};

Game.drawHud = function() {

	if (toDrawLeaderboard) {
		drawLeadboard();
	} else if (toDrawBackpack) {
		drawBackpack();
	}

	// hud grey bar
	completionSprite = game.add.graphics(0, 0);
	completionSprite.beginFill(0x3d3d3d, 1);
	completionSprite.drawRect(Game.map.widthInPixels/1.5, Game.map.heightInPixels-0.51*Game.map.heightInPixels, Game.map.widthInPixels, Game.map.heightInPixels/10.4);
	completionSprite.boundsPadding = 0;
    completionSprite.fixedToCamera = true;

	// trophy icon
	let trophyIcon = game.add.sprite(Game.map.widthInPixels-Game.map.widthInPixels/7, Game.map.heightInPixels-Game.map.heightInPixels/2.032, "trophy");
    trophyIcon.inputEnabled = true;
    trophyIcon.fixedToCamera = true;
    trophyIcon.events.onInputDown.add(queueLeaderboard, this);

	// backpack icon
	let backpackIcon = game.add.sprite(Game.map.widthInPixels-Game.map.widthInPixels/4.60, Game.map.heightInPixels-Game.map.heightInPixels/2.032, "backpack");
    backpackIcon.inputEnabled = true;
    backpackIcon.fixedToCamera = true;
    backpackIcon.events.onInputDown.add(queueBackpack, this);

    Game.backpackIcon = backpackIcon;
    
    // coin icon
    let coinIcon = game.add.sprite(Game.map.widthInPixels-Game.map.widthInPixels/3.45, Game.map.heightInPixels-Game.map.heightInPixels/2.032, "coin");
    coinIcon.inputEnabled = false;
    coinIcon.fixedToCamera = true;

    // coin text
    let coinText = game.add.bitmapText(Game.map.widthInPixels-Game.map.widthInPixels/3.58, Game.map.heightInPixels-Game.map.heightInPixels/2.2, 'carrier_command','x'+coinNumber,7.5);
    coinText.inputEnabled = false;
    coinText.fixedToCamera = true;
}

function drawLeadboard() {
	// leaderboard panel
	Game.panel = new SlickUI.Element.Panel(Game.map.widthInPixels/1.5, Game.map.heightInPixels/4.15, Game.map.widthInPixels/2, Game.map.heightInPixels/4);
	Game.slickUI.add(Game.panel);

	let header = new SlickUI.Element.Text(Game.panel.width/2 - 100 , 20, "Leaderboard:");

	let player1 = new SlickUI.Element.Text(Game.panel.width/2 - 100 , 65, "1. top_user_id");
	let player2 = new SlickUI.Element.Text(Game.panel.width/2 - 100 , 95, "2. top_user_id");
	let player3 = new SlickUI.Element.Text(Game.panel.width/2 - 100 , 125, "3. top_user_id");
	let player4 = new SlickUI.Element.Text(Game.panel.width/2 - 100 , 155, "4. top_user_id");
	let player5 = new SlickUI.Element.Text(Game.panel.width/2 - 100 , 185, "5. top_user_id");

	Game.panel.add(header);
	Game.panel.add(player1);
	Game.panel.add(player2);
	Game.panel.add(player3);
	Game.panel.add(player4);
	Game.panel.add(player5);
}

function drawBackpack() {
	// leaderboard panel
	
	console.log("ADASDAGSDSASFDSGF");
	
	Game.panel = new SlickUI.Element.Panel(Game.map.widthInPixels/1.5, Game.map.heightInPixels/4.15, Game.map.widthInPixels/2, Game.map.heightInPixels/4);
	Game.slickUI.add(Game.panel);

	let header = new SlickUI.Element.Text(Game.panel.width/2 - 100 , 20, "Backpack:");

	// let player1 = new SlickUI.Element.Text(Game.panel.width/2 - 100 , 65, "empty");

	let pokeball1 = game.add.sprite(0,0,'pokeball');
    pokeball1.anchor.setTo(0.5);
    let pokeball1text = new SlickUI.Element.Text(Game.panel.width/3.8 , 58, "x0");
    pokeball1text.size = 9.5;

    Game.panel.add(new SlickUI.Element.DisplayObject(Game.panel.width/5, Game.panel.height/2.75, pokeball1));
	Game.panel.add(header);
	Game.panel.add(pokeball1text);
}
function queueLeaderboard() {
	if (toDrawLeaderboard || toDrawBackpack) {
		if (Game.panel.container != undefined) {
			Game.panel.destroy();
		}
	} else {
		drawLeadboard();
	}
	toDrawLeaderboard = !toDrawLeaderboard;
}

function queueBackpack() {
	if (toDrawBackpack || toDrawBackpack) {
		if (Game.panel.container != undefined) {
			Game.panel.destroy();
		}
	} else {
		drawBackpack();
	}
	toDrawBackpack = !toDrawBackpack;
}