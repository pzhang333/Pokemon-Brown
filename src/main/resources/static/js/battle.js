class Pokemon {
	
	constructor(id, species, moves) {
		this.id = id;
		
		if (species == undefined) {
			species = "pikachu";
		}
		this.species = species;
		
		if (moves == undefined) {
			moves = [];
		}
		this.moves = moves;
	}
	
}

var Battle = {
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
	
	console.log(pokemon.id);
	
	tween.to({
		x: pokemon.sprite.x,
		y: pokemon.sprite.y + 150,
		alpha: 0
	}, Phaser.Timer.SECOND * .5);
	
	tween.onComplete.add(function () {
		pokemon.healthbar.backBar.destroy();
		pokemon.healthbar.healthBar.destroy();
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

// Battle.showAttackPair({defendingId: 1, attack: 'basic', damage: 75}, {defendingId: 2, attack: 'basic', damage: 250}, 1);

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

// Battle.doSwitch(Battle.backPokemon, new Pokemon(123, 'chandelure'));
Battle.doSwitch = function(pOut, pIn, cb) {
	
	let delay = .75;
	
	let fore = (pOut.id == Battle.frontPokemon.id) ? pIn : undefined;
	let bg = (pOut.id == Battle.backPokemon.id) ? pIn : undefined;
	
	Battle.switchOut(pOut, function() {
		Game.time.events.add(Phaser.Timer.SECOND * delay, function() {
			
			if (fore != undefined) {
				Battle.frontPokemon = pIn;
			} else {
				Battle.backPokemon = pIn;
			}
			
			Battle.drawPokemon(fore, bg);
		});
	});
}

Battle.switchIn = function(pokemon, cb) {
	
	console.log('Switch in: ', pokemon);
	
	let tween = game.add.tween(pokemon.sprite);
	
	pokemon.sprite.alpha = 0;
	
	let w = pokemon.sprite.width;
	let h = pokemon.sprite.height;
	
	pokemon.sprite.width = 0;
	pokemon.sprite.height = 0;
	
	pokemon.sprite.visible = true;
	
	tween.to({
		x: pokemon.sprite.x,
		y: pokemon.sprite.y,
		width: w,
		height: h,
		alpha: 1
	}, Phaser.Timer.SECOND * .5);
	
	tween.onComplete.add(function() {
		Battle.drawHealthBox(pokemon);
		pokemon.sprite.animations.add('idle');
		pokemon.sprite.animations.play('idle', 40, true);	
		
		if (cb != undefined) {
			cb();
		}
	});
	
	tween.start();
	
}

Battle.switchOut = function(pokemon, cb) {
	
	let tween = game.add.tween(pokemon.sprite);
	
	tween.to({
		x: pokemon.sprite.x,
		y: pokemon.sprite.y,
		alpha: 0,
		width: 0,
		height: 0
	}, Phaser.Timer.SECOND * .5);
	
	tween.onComplete.add(function () {
		pokemon.healthbar.backBar.destroy();
		pokemon.healthbar.healthBar.destroy();
		pokemon.sprite.kill();
		
		if (cb != undefined) {
			cb();
		}
	});
	
	tween.start();
	
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
	
	Battle.slickUI = game.plugins.add(Phaser.Plugin.SlickUI);
	Battle.slickUI.load('ui/kenney/kenney.json');
	
	game.load.audio('battle', ['assets/audio/battle.mp3']);
	
};

Battle.endBattle = function() {
	Battle.music.destroy();
	
	//Game.players = {};
	game.state.start('Game');
	
	// HACK VERY BAD!
	//location.reload();
}

Battle.create = function() {
	//return;
	Battle.music = game.add.audio('battle');
	//Battle.music.loopFull(.1);
	
	this.stage = game.add.sprite(game.width / 2, game.height / 2, 'atlas1', Battle.initPacket.background_name);
	this.stage.anchor.setTo(.5, .5);
	this.stage.width = game.width;
	this.stage.height = game.height;
	
	this.backPatch = game.add.sprite(game.width * (3 / 4), game.height * (6.25 / 12), 'atlas2', 'scenery/patch');
	this.backPatch.anchor.setTo(0.5, 0.5);
	this.backPatch.scale.set(1.25, 1.25);
	this.backPatch.visible = true;
	
	this.frontPatch = game.add.sprite(game.width * (3.5 / 12), game.height * (8.5 / 12), 'atlas2', 'scenery/patch');
	this.frontPatch.anchor.setTo(0.5, 0.5);
	this.frontPatch.scale.set(1.5, 1.5);
	this.frontPatch.visible = true;
	
	let pokemon_a = Battle.initPacket.pokemon_a;
	let pokemon_b = Battle.initPacket.pokemon_b;
	
	// Todo: update
	Battle.frontPokemon = new Pokemon(1, pokemon_a.species, pokemon_a.moves);
	Battle.backPokemon = new Pokemon(2, pokemon_b.species);
	
	this.drawDefaultMenu();
	this.drawPokemon(Battle.frontPokemon, Battle.backPokemon);
	
	this.stage.visible = true;
};

Battle.drawBackground = function(key) {
	
	if (this.backPokemon.sprite != undefined) {
		this.backPokemon.sprite.kill();
	}
	
	this.backPokemon.sprite = game.add.sprite(game.width * (3 / 4), game.height * (6.25 / 12), key);
	this.backPokemon.sprite.visible = false;
	this.backPokemon.sprite.anchor.setTo(0.5, 1);
	this.backPokemon.sprite.scale.set(.75, .75);
	
	
	Battle.switchIn(this.backPokemon);
	
}

Battle.drawHealthBox = function(pokemon) {
	pokemon.health = 100;
	pokemon.maxHealth = 100;
	
	pokemon.healthbar = this.game.add.plugin(Phaser.Plugin.HealthMeter)
	pokemon.healthbar.bar(pokemon, {
		x: pokemon.sprite.x - (125 / 2),
		y: (pokemon.sprite.y - (pokemon.sprite.height) - 30),
		width: 125,
		height: 6
	});
}

Battle.drawForeground = function(key) {
	
	if (this.frontPokemon.sprite != undefined) {
		this.frontPokemon.sprite.kill();
	}
	
	this.frontPokemon.sprite = game.add.sprite(game.width * (3.5 / 12), game.height * (8.5 / 12), key);
	this.frontPokemon.sprite.visible = false;
	this.frontPokemon.sprite.anchor.setTo(0.5, 1);
	this.frontPokemon.sprite.scale.set(.75, .75);
	
	
	Battle.switchIn(Battle.frontPokemon);
}

Battle.drawPokemon = function(fore, bg) {
	
	/*if (fore != undefined && this.frontPokemon != undefined) {
		this.frontPokemon = fore;
		
		if (this.frontPokemon.sprite != undefined) {
			this.frontPokemon.sprite.kill();	
		}
	}
	
	if (bg != undefined && this.backPokemon != undefined) {
		this.backPokemon = bg;
		
		if (this.backPokemon.sprite != undefined) {
			this.backPokemon.sprite.kill();	
		}
	}*/
	
	//this.drawFront(front.species);
	//this.drawFront(front.species)
	
	let frontKey = false;
	let backKey = false;
	
	if (fore != undefined) {
		backKey = 'back-' + fore.species;
		
		if (game.cache.checkImageKey(backKey)) {
			this.drawForeground(backKey);
		} else {
			game.load.atlasJSONHash(backKey, '/assets/sprites/pokemon/' + backKey + '.png', '/assets/sprites/pokemon/' + backKey + '.json');
		}
	}
	
	if (bg != undefined) {
		frontKey = 'front-' + bg.species;
		
		if (game.cache.checkImageKey(frontKey)) {
			this.drawBackground(frontKey);
		} else {
			game.load.atlasJSONHash(frontKey, '/assets/sprites/pokemon/' + frontKey + '.png', '/assets/sprites/pokemon/' + frontKey + '.json');
		}
	}

	game.load.start();
	
	game.load.onFileComplete.add(function(progress, cacheKey, success, totalLoaded, totalFiles) {
		
		if (cacheKey == frontKey) {
			this.drawBackground(cacheKey);
		} else if (cacheKey == backKey) {
			this.drawForeground(cacheKey);
		}
		 
	}, this);
	
}

Battle.clearMenu = function() {
	
	if (Battle.panel != undefined) {
		Battle.panel.destroy();
	}
	
	if (Battle.menuSeparator != undefined) {
		Battle.menuSeparator.kill();
	}
}

Battle.clearMoves = function() {
	
	if (Battle.moveButtons == undefined) {
		return;
	}
	
	for(let i = 0; i < Battle.moveButtons.length; i++) {
		Battle.moveButtons[i].destroy();
	}
	
	Battle.moveButtons = [];
}

Battle.showMoves = function() {
	
	Battle.clearMoves();
	
	Battle.moveButtons = [];
	
	let buttonWidth = Battle.panel.width / 4.1;
	
	for(let i = 0; i < this.frontPokemon.moves.length; i++) {
		let move = this.frontPokemon.moves[i];

		let yI = Math.floor(i / 2);
		let xI = i % 2;
		
		let moveButton = new SlickUI.Element.Button(xI * (buttonWidth + 4), (yI * 50), buttonWidth, 48);
		Battle.panel.add(moveButton);
		moveButton.add(new SlickUI.Element.Text(0, 0, move.name)).center();
		
		Battle.moveButtons.push(moveButton);
	}
	
	if (Battle.moveButtons.length == 0) {
		// Add struggle
	}
}

Battle.drawDefaultMenu = async function() {
	Battle.clearMenu();
	
	Battle.panel = new SlickUI.Element.Panel(8, game.height - (108 + 8), game.width - 16, 108)
	Battle.slickUI.add(Battle.panel);
	
	let buttonOffsetX = Battle.panel.width / 2;
	let buttonWidth = Battle.panel.width / 4.1;
	
	// Fight button
	let fightButton = new SlickUI.Element.Button(buttonOffsetX + 4, 0, buttonWidth, 48);
	Battle.panel.add(fightButton);
	fightButton.add(new SlickUI.Element.Text(0, 0, "Fight")).center();
	await fightButton.events;
	fightButton.events.onInputUp.add(function() {

		if (Battle.moveButtons == undefined || Battle.moveButtons.length == 0) {
			Battle.showMoves();
		} else {
			Battle.clearMoves();
		}
		
	});
	
	let switchButton = new SlickUI.Element.Button(buttonOffsetX + 4, 50, buttonWidth, 48);
	Battle.panel.add(switchButton);
	switchButton.add(new SlickUI.Element.Text(0, 0, "Switch")).center(); 
	
	let itemButton = new SlickUI.Element.Button(buttonOffsetX + 4 + buttonWidth + 4, 0, buttonWidth, 48);
	Battle.panel.add(itemButton);
	itemButton.add(new SlickUI.Element.Text(0, 0, "Item")).center(); 
	
	let forefitButton = new SlickUI.Element.Button(buttonOffsetX + 4 + buttonWidth + 4, 50, buttonWidth, 48);
	Battle.panel.add(forefitButton);
	forefitButton.add(new SlickUI.Element.Text(0, 0, "Forefit")).center();
	await forefitButton.events;
	forefitButton.events.onInputUp.add(function() {
		net.sendBattlePacket(BATTLE_ACTION.RUN, {});
	});
	
	if (Battle.menuSeparator != undefined) {
		Battle.menuSeparator.kill();
	}
	
	Battle.menuSeparator = game.add.graphics(0, 0);
	
	Battle.menuSeparator.lineStyle(2, 0x999999);
	Battle.menuSeparator.moveTo(buttonOffsetX + 12, game.height - (108));
	Battle.menuSeparator.lineTo(buttonOffsetX + 12, game.height - 16);
	
}

Battle.setup = function(initPacket) {
	
	Battle.battleId = initPacket.battle_id;
	Battle.battleType = initPacket.battle_type;
	
	// Hack:
	if (initPacket.backgroud_name != undefined) {
		initPacket.background_name = initPacket.backgroud_name;
	}
	
	Battle.initPacket = initPacket;
	
}