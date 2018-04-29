class Pokemon {
	
	constructor(id, species) {
		this.id = id;
		
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
	
	Battle.attackAnims = {
		'basic': getAnimArr('basic', range(0, 8, range(0, 4)))
	};
	
	Battle.offsets = {
		'basic': {
			x: 0,
			y: -40,
			xAnchor: .5,
			yAnchor: .5,
			xScale: .75,
			yScale: .75,
			time: .5
		}
	}
};

Battle.getPokemonById = function(id) {
	return (id == Battle.frontPokemon.id) ? Battle.frontPokemon : Battle.backPokemon;
}


Battle.getSpriteById = function(id) {
	return (id == Battle.frontPokemon.id) ? Battle.frontPokemon.sprite : Battle.backPokemon.sprite;
}

Battle.showKO = function(pokemon) {
	let tween = game.add.tween(pokemon.sprite);
	
	tween.to({
		x: pokemon.sprite.x,
		y: pokemon.sprite.y + 100,
		alpha: 0
	}, Phaser.Timer.SECOND * .5);
	
	tween.onComplete.add(function () {
		pokemon.healthbar.backBar.destroy();
		pokemon.healthbar.healthBar.kill();
		pokemon.sprite.kill();
	});
	
	tween.start();
}

Battle.applyDamage = function(pokemon, damage) {
	if (damage >= pokemon.health) {
		pokemon.health = 0;
		
		if (Battle.attack.animations.currentAnim.isPlaying) {
			Battle.attack.animations.currentAnim.onComplete.addOnce(function() {
				Battle.showKO(pokemon);
			});
		} else {
			Battle.showKO(pokemon);
		}
	} else {
		pokemon.health -= damage;
	}
}

// Battle.showAttackPair({defendingId: 1, attack: 'basic', damage: 25}, {defendingId: 2, attack: 'basic', damage: 25}, .75);

Battle.showAttackPair = function(first, second, delay) {
	
	if (delay == undefined) {
		delay = .75;
	}
	
	let defFirst = Battle.getPokemonById(first.defendingId);
	
	let offsetsFirst = Battle.offsets[first.attack];
	Game.time.events.add(Phaser.Timer.SECOND * offsetsFirst.time, function() {
		Battle.applyDamage(defFirst, first.damage);
	});
	
	Battle.showAttack(defFirst.sprite, first.attack, function() {
		
		Game.time.events.add(Phaser.Timer.SECOND * delay, function() {
			if (second != undefined) {
				let defSecond = Battle.getPokemonById(second.defendingId);
				
				let offsetsSecond = Battle.offsets[second.attack];
				Game.time.events.add(Phaser.Timer.SECOND * offsetsSecond.time, function() {
					Battle.applyDamage(defSecond, second.damage);
				});
				
				Battle.showAttack(defSecond.sprite, second.attack);
			}
		}, this);
	});
}

Battle.showAttack = function(pokeSprite, name, cb) {
	
	if (name == undefined) {
		name = 'basic';
	}

	//this.attack.visible = true;
	//this.attack.animations.play(name, 3, true);
	
	if (this.attack != undefined) {
		this.attack.kill();
	}

	let offsets = Battle.offsets[name];
	
	this.attack = game.add.sprite(
			pokeSprite.x + offsets.x,
			pokeSprite.y + offsets.y, 'attacks');
	
	this.attack.anchor.setTo(.5, .5);
	this.attack.scale.setTo(offsets.xScale, offsets.yScale);
	
	for(name in Battle.attackAnims) {
		console.log(name);
		this.attack.animations.add(name, Battle.attackAnims[name]);
	}
	
	this.attack.animations.play(name, 15, false);
	this.attack.animations.currentAnim.onComplete.add(function () {
		Battle.attack.kill();
	}, this);
	
	if (cb != undefined) {
		this.attack.animations.currentAnim.onComplete.add(cb, this);
	}
} 

Battle.update = function() {
};

Battle.preload = function() {
	game.stage.disableVisibilityChange = true;
	game.load.atlasJSONHash('atlas1', 'assets/sprites/pokemon_atlas1.png', 'assets/sprites/pokemon_atlas1.json');
	game.load.atlasJSONHash('atlas2', 'assets/sprites/pokemon_atlas2.png', 'assets/sprites/pokemon_atlas2.json');
	game.load.atlasJSONHash('attacks', 'assets/pokemon/attacks.png', 'assets/pokemon/attacks.json');
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
	
	Battle.frontPokemon = new Pokemon(1, "pikachu");
	Battle.backPokemon = new Pokemon(2, "chandelure");
	
	this.drawDefaultMenu();
	this.drawPokemon(Battle.frontPokemon, Battle.backPokemon);
	
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
	
	if (this.backPokemon.sprite != undefined) {
		this.backPokemon.sprite.kill();
	}
	
	this.backPokemon.sprite = game.add.sprite(game.width * (3 / 4), game.height * (6.25 / 12), key);
	
	this.backPokemon.sprite.animations.add('idle');
	this.backPokemon.sprite.animations.play('idle', 40, true);
	
	this.backPokemon.sprite.anchor.setTo(0.5, 1);
	this.backPokemon.sprite.scale.set(.75, .75);
	
	Battle.drawHealthBox(this.backPokemon);
	
	this.backPokemon.sprite.visible = true;
}

Battle.drawHealthBox = function(pokemon) {
	pokemon.health = 100;
	pokemon.maxHealth = 100;
	
	pokemon.healthbar = this.game.add.plugin(Phaser.Plugin.HealthMeter)
	pokemon.healthbar.bar(pokemon, {
		x: pokemon.sprite.x - (125 / 2),
		y: (pokemon.sprite.y - (pokemon.sprite.height) - 30),
		width: 125,
		height: 8
	});
}

Battle.drawForeground = function(key) {
	
	if (this.frontPokemon.sprite != undefined) {
		this.frontPokemon.sprite.kill();
	}
	
	this.frontPokemon.sprite = game.add.sprite(game.width * (3.5 / 12), game.height * (9 / 12), key);
	
	this.frontPokemon.sprite.animations.add('idle');
	this.frontPokemon.sprite.animations.play('idle', 40, true);
	
	this.frontPokemon.sprite.anchor.setTo(0.5, 1);
	this.frontPokemon.sprite.scale.set(.75, .75);
	
	Battle.drawHealthBox(this.frontPokemon);
	
	this.frontPokemon.sprite.visible = true;
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