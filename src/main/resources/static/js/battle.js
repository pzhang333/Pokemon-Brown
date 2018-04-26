var Battle = {
	id: -1,
	inBattle: false	
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
	
	this.frontPatch = game.add.sprite(game.world.centerX * (17 / 12), game.world.centerY / 2, 'atlas2', 'scenery/patch');
	this.frontPatch.anchor.setTo(0.5, 0.5);
	this.frontPatch.scale.set(2, 2);
	this.frontPatch.visible = true;
	
	this.frontPokemonSprite = game.add.sprite(game.world.centerX * (5 / 12), game.world.centerY * (5 / 6), 'atlas2', 'front/pikachu');
	this.frontPokemonSprite.anchor.setTo(0.5, 0.5);
	this.frontPokemonSprite.scale.set(2, 2);
	this.frontPokemonSprite.visible = true;
	
	this.backPokemonSprite = game.add.sprite(game.world.centerX * (17 / 12), game.world.centerY / 2, 'atlas2', 'back/pikachu');
	this.backPokemonSprite.anchor.setTo(0.5, 0.5);
	this.backPokemonSprite.scale.set(2, 2);
	this.backPokemonSprite.visible = true;
	
	this.stage.visible = true;
};

Battle.drawDefaultMenu = function() {
	
	
}

Battle.start = function() {
	
}

Battle.startBattle = function(packet) {
	
	this.inBattle = true;
	this.id = packet.battleId;

	game.state.start('Battle');
}