class Pokemon {
	
	constructor(species) {
		if (species == undefined) {
			species = "pikachu";
		}
		this.species = species;
	}
	
}

var Battle = {
	id: -1,
	inBattle: false,
	frontBaseURL: "/assets/pokemon/front/",
	backBaseURL: "/assets/pokemon/back/"
};


Battle.init = function() {
	game.scale.pageAlignHorizontally = true;
};

Battle.update = function() {

};

Battle.preload = function() {
	game.stage.disableVisibilityChange = true;
	game.load.atlasJSONHash('atlas1', 'assets/sprites/pokemon_atlas1.png', 'assets/sprites/pokemon_atlas1.json');
	game.load.atlasJSONHash('atlas2', 'assets/sprites/pokemon_atlas2.png', 'assets/sprites/pokemon_atlas2.json');
};


Battle.create = function() {
	this.stage = game.add.sprite(game.width / 2, game.height / 2, 'atlas1', 'bg-meadow');
	this.stage.anchor.setTo(.5, .5);
	this.stage.width = game.width;
	this.stage.height = game.height;
	
	this.backPatch = game.add.sprite(game.width * (3 / 4), game.height * (6.25 / 12), 'atlas2', 'scenery/patch');
	this.backPatch.anchor.setTo(0.5, 0.5);
	this.backPatch.scale.set(1.25, 1.25);
	this.backPatch.visible = true;
	
	this.frontPatch = game.add.sprite(game.width * (3.5 / 12), game.height * (9 / 12), 'atlas2', 'scenery/patch');
	this.frontPatch.anchor.setTo(0.5, 0.5);
	this.frontPatch.scale.set(1.5, 1.5);
	this.frontPatch.visible = true;
	
	let p1 = new Pokemon("pikachu");
	let p2 = new Pokemon("chandelure");
	
	this.drawDefaultMenu();
	this.drawPokemon(p1, p2);
	
	/*this.frontPokemonSprite = game.add.sprite(game.world.centerX * (4.5 / 12), game.world.centerY * (4.5 / 6), 'atlas2', 'front/pikachu');
	this.frontPokemonSprite.anchor.setTo(0.5, 0.5);
	this.frontPokemonSprite.scale.set(2, 2);
	this.frontPokemonSprite.visible = true;
	
	this.backPokemonSprite = game.add.sprite(game.world.centerX * (17 / 12), game.world.centerY / 2, 'atlas2', 'back/pikachu');
	this.backPokemonSprite.anchor.setTo(0.5, 0.5);
	this.backPokemonSprite.scale.set(2, 2);
	this.backPokemonSprite.visible = true;*/
	
	this.stage.visible = true;
};

Battle.drawBackground = function(key) {
	this.backPokemonSprite = game.add.sprite(game.width * (3 / 4), game.height * (6.25 / 12), key);
	
	this.backPokemonSprite.animations.add('idle');
	this.backPokemonSprite.animations.play('idle', 40, true);
	
	this.backPokemonSprite.anchor.setTo(0.5, 1);
	this.backPokemonSprite.scale.set(.75, .75);
	this.backPokemonSprite.visible = true;
}

Battle.drawForeground = function(key) {
	this.frontPokemonSprite = game.add.sprite(game.width * (3.5 / 12), game.height * (9 / 12), key);
	
	this.frontPokemonSprite.animations.add('idle');
	this.frontPokemonSprite.animations.play('idle', 40, true);
	
	this.frontPokemonSprite.anchor.setTo(0.5, 1);
	this.frontPokemonSprite.scale.set(.75, .75);
	this.frontPokemonSprite.visible = true;
}

Battle.drawPokemon = function(fore, bg) {
	
	if (this.frontPokemonSprite != undefined) {
		this.frontPokemonSprite.destroy();	
	}
	
	if (this.backPokemonSprite != undefined) {
		this.backPokemonSprite.destroy();	
	}
	
	//this.drawFront(front.species);
	//this.drawFront(front.species);
	
	let frontKey = 'front-' + bg.species;
	let backKey = 'back-' + fore.species;
	
	game.load.atlasJSONHash(backKey, '/assets/sprites/pokemon/' + backKey + '.png', '/assets/sprites/pokemon/' + backKey + '.json');
	game.load.atlasJSONHash(frontKey, '/assets/sprites/pokemon/' + frontKey + '.png', '/assets/sprites/pokemon/' + frontKey + '.json');
	game.load.start();
	
	game.load.onFileComplete.add(function(progress, cacheKey, success, totalLoaded, totalFiles) {
		 
		if (cacheKey == frontKey) {
			this.drawBackground(cacheKey);
		} else if (cacheKey == backKey) {
			this.drawForeground(cacheKey);
		}
		 
	}, this);
	
}

Battle.drawDefaultMenu = function() {
	
	
}

Battle.start = function() {
	
}

Battle.startBattle = function(packet) {
	
	this.inBattle = true;
	this.id = packet.battleId;

	game.state.start('Battle');
}