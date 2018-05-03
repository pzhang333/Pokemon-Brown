var Battle = {
	frontBaseURL: "/assets/pokemon/front/",
	backBaseURL: "/assets/pokemon/back/",
	inBattle: false,
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

Battle.getPokemonByOwnerId = function(id) {
	return (id == Battle.frontPokemon.owner_id) ? Battle.frontPokemon : Battle.backPokemon;
}

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

Battle.setHealth = function(pokemon, health) {
	
	pokemon.health = health;
	
	console.log('AYyyy: ' + health);
	if (health == 0) {
		if (Battle.attack.animations.currentAnim.isPlaying) {
			Battle.attack.animations.currentAnim.onComplete.addOnce(function() {
				Battle.showKO(pokemon);
			});
		} else {
			Battle.showKO(pokemon);
		}
	}
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

Battle.showAttackSummary = function(first, cb) {
	
	
	let defFirst = Battle.getPokemonById(first.defendingId);
	
	let offsetsFirst = Battle.offsets[first.animation];
	Game.time.events.add(Phaser.Timer.SECOND * offsetsFirst.time, function() {
		Battle.setHealth(defFirst, first.health);
	});
	
	if (offsetsFirst != undefined) {
		Battle.showAttack(defFirst.sprite, first.animation, function() {
			if (cb != undefined) {
				cb();
			}
		});
	} else {
		if (cb != undefined) {
			cb();
		}
	}
}


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
			
			if (cb != undefined) {
				cb();
			}
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
		pokemon.healthbar.destroy();
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
	
	
	game.load.audio('battle', ['assets/audio/battle.mp3']);
	
};

Battle.run = function() {
	Battle.init();
	Battle.create();
}

Battle.endBattle = function() {
	Battle.music.destroy();
	
	Battle.clearMenu();
	
	console.log('EndBattle()');
	
	Battle.inBattle = false;
	
	//Game.players = {};
	game.state.start("Game", true, false);
	
	// HACK VERY BAD!
	//location.reload();
}

Battle.create = function() {
	
	Battle.inBattle = true;
	
	Game.map.gameLayers['Base'].inputEnabled = false;
	
	Battle.slickUI = game.plugins.add(Phaser.Plugin.SlickUI);
	Battle.slickUI.load('ui/kenney/kenney.json');
	
	game.world.bringToTop(Battle.slickUI.container.displayGroup);
	
	//return;
	Battle.music = game.add.audio('battle');
	//Battle.music.loopFull(.1);
	
	console.log(game.height / 2);
	
	game.camera.follow(null);
	game.camera.reset();
	
	this.stage = game.add.sprite(0, 0, 'atlas1', Battle.initPacket.background_name);
	this.stage.width = game.width;
	this.stage.height = game.height;
	this.stage.anchor.setTo(0, 0);
	
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
	
	
	// Other:
	/*Battle.team = [
		pokemon_a,
		{
			id: 123, 
			species: 'pikachu',
			health: 122,
			maxHealth: 150
		},
		{
			id: 234, 
			species: 'chandelure',
			health: 68,
			maxHealth: 150
		},
		{
			id: 345, 
			species: 'arceus',
			health: 100,
			maxHealth: 150
		},
		{
			id: 13232,
			species: 'giratina',
			health: 150,
			maxHealth: 150
		}
	];*/

	if (pokemon_a.owner_id == Game.player.id) {
		Battle.frontPokemon = pokemon_a;
		Battle.backPokemon = pokemon_b;
	} else {
		Battle.frontPokemon = pokemon_b;
		Battle.backPokemon = pokemon_a;
	}
	
	this.drawDefaultMenu();
	this.drawPokemon(Battle.frontPokemon, Battle.backPokemon);
	
	this.drawMessage("Battle!");
	
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

Battle.custDrawFrontPokemon = function(pokemon, cb) {
	
	let frontKey = 'front-' + pokemon.species;
	
	if (game.cache.checkImageKey(frontKey)) {
		cb(frontKey);
	} else {
		game.load.atlasJSONHash(frontKey, '/assets/sprites/pokemon/' + frontKey + '.png', '/assets/sprites/pokemon/' + frontKey + '.json');
	}
	
	game.load.start();
	game.load.onFileComplete.add(function(progress, cacheKey, success, totalLoaded, totalFiles) {
		if (frontKey == cacheKey) {
			cb(frontKey);
		}
	}, this);
}

Battle.drawPokemon = function(fore, bg) {
	
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

Battle.drawMessage = function(text, size) {
	
	Battle.clearMessageText();
	
	if (text == undefined) {
		text = "";
	}
	
	if (Battle.messageText == undefined) {
		
		Battle.messageText = new SlickUI.Element.Text(8, 8, text);
		
		if (size == undefined) {
			size = 12;
		}
		
		Battle.messageText.size = size;
		
		Battle.panel.add(Battle.messageText);
	} else {
		Battle.messageText.value = text;
	}
	
	Battle.messageText.visible = true;
}

Battle.clearMenu = function() {
	
	if (Battle.panel != undefined) {
		Battle.panel.destroy();
		Battle.panel = undefined;
	}
	
	if (Battle.menuSeparator != undefined) {
		Battle.menuSeparator.kill();
	}
	
	Battle.clearButtons(Battle.stdButtons);
}

Battle.clearButtons = function(buttons) {
	if (buttons == undefined) {
		return;
	}
	
	for(let i = 0; i < buttons.length; i++) {
		buttons[i].inputEnabled = false;
		buttons[i].destroy();
	}
	
	 while(buttons.length) {
		 buttons.pop();
	 }
}

Battle.clearMoves = function(drawMsg) {
	Battle.clearButtons(Battle.moveButtons);

	if (drawMsg == undefined) {
		drawMsg = true;
	}
	
	if (drawMsg) {
		Battle.drawMessage();
	}
}

Battle.clearTeam = async function(drawMsg) {
	Battle.clearButtons(Battle.teamButtons);
	
	if (drawMsg == undefined) {
		drawMsg = true;
	}
	
	if (drawMsg) {
		Battle.drawMessage();
	}
}

Battle.clearMessageText = function() {
	if (Battle.messageText != undefined) {
		Battle.messageText.visible = false;
	}
}

Battle.showTeam = async function() {
	Battle.clearTeam();
	Battle.clearMoves();
	Battle.clearMessageText();
	
	Battle.teamButtons = [];
	
	let buttonWidth = Battle.panel.width / 4.1;
	
	let pos = 0;
	for(let i = 0; i < Battle.team.length; i++) {
		let pokemon = Battle.team[i];
		
		console.log(Battle.frontPokemon.id + ' : ' + pokemon.id);
		if (pokemon.id == Battle.frontPokemon.id) {
			continue;
		}

		let yI = Math.floor(pos / 2);
		let xI = pos % 2;
		
		let pokeButton = new SlickUI.Element.Button(xI * (buttonWidth + 4), (yI * 50), buttonWidth, 48);
		Battle.panel.add(pokeButton);
		
		Phaser.Canvas.setImageRenderingCrisp(game.canvas);
		
		let text = new SlickUI.Element.Text(50, 0, ucfirst(pokemon.species));
		text.size = 11;
		pokeButton.add(text);
		
		await pokeButton.events;
		
		
		if (pokemon.health <= 0) {
			pokeButton.events.onInputUp.removeAll();
			pokeButton.events.onInputDown.removeAll();
		} else {
			pokeButton.events.onInputUp.add(function() {
				Battle.switchTo(pokemon.id);
			});
		}
		
		let x = Battle.panel.x + (xI * (buttonWidth + 4)) + (text.x);
		let y = Battle.panel.y + (yI * 50) + (48 / 2);
		
		Battle.custDrawFrontPokemon(pokemon, function(key) {
			let sprite = game.add.sprite(0, 0, key);
			sprite.visible = true;
			sprite.anchor.setTo(1, .5);
			
			let s = 35 / sprite.height;
			
			
			sprite.scale.set(s, s);
			sprite.x = x;
			sprite.y = y + 5;
			
			Battle.teamButtons.push(sprite);
		});
		
		let healthbar = this.game.add.plugin(Phaser.Plugin.HealthMeter)
		healthbar.bar(pokemon, {
			x: x + 10,
			y: y + 10,
			width: 125,
			height: 2
		});
		
		Battle.teamButtons.push(healthbar);
		Battle.teamButtons.push(pokeButton);

		pos++;
	}
}

Battle.showMoves = async function() {
	
	Battle.clearTeam();
	Battle.clearMoves();
	Battle.clearMessageText();
	
	Battle.moveButtons = [];
	
	let buttonWidth = Battle.panel.width / 4.1;
	
	let canStruggle = true;
	for(let i = 0; i < this.frontPokemon.moves.length; i++) {
		let move = this.frontPokemon.moves[i];

		let yI = Math.floor(i / 2);
		let xI = i % 2;
		
		let moveButton = new SlickUI.Element.Button(xI * (buttonWidth + 4), (yI * 50), buttonWidth, 48);
		Battle.panel.add(moveButton);
		
		let text = new SlickUI.Element.Text(0, 0, move.name);
		text.size = 11;
		moveButton.add(text).center();
		text.y -= 10;
		
		text = new SlickUI.Element.Text(0, (48 / 2), move.pp + " / " + move.max_pp);
		text.size = 11;
		moveButton.add(text).center();
		text.y += 10;
		
		
		await moveButton.events;
		
		if (move.pp < move.cost) {
			moveButton.events.onInputUp.removeAll();
			moveButton.events.onInputDown.removeAll();
		} else {
			
			canStruggle = false;
			
			moveButton.events.onInputUp.add(function() {
				Battle.useMove(move.id);
			});
		}
		
		Battle.moveButtons.push(moveButton);
	}
	
	if (Battle.moveButtons.length == 0 || canStruggle) {
		Battle.clearMoves();
		
		let moveButton = new SlickUI.Element.Button(0, 0, 2 * buttonWidth, 2 * 48);
		Battle.panel.add(moveButton);
		
		
		let text = new SlickUI.Element.Text(0, 0, "Struggle");
		moveButton.add(text).center();

		await moveButton.events;
		moveButton.events.onInputUp.add(function() {
			Battle.useMove(-1);
		});
		
		Battle.moveButtons.push(moveButton);
	}
}

Battle.useMove = async function(id) {
	console.log('Use move: ' + id);
	
	let moves = Battle.frontPokemon.moves;
	for(let i = 0; i < moves.length; i++) {
		let move = moves[i];
		
		if (move.id == id) {
			move.pp -= move.cost;
		}
	}
	
	if (Battle.showing != undefined) {
		await Battle.showing;
	}
	
	if (Battle.frontPokemon.health == 0) {
		return;
	}
	
	net.sendBattlePacket(BATTLE_ACTION.FIGHT, {
		moveId: id
	});
	
	Battle.clearMoves();
}

Battle.switchTo = async function(id) {
	console.log('Switch to: ' + id);
	
	if (Battle.showing != undefined) {
		await Battle.showing;
	}
	
	net.sendBattlePacket(BATTLE_ACTION.SWITCH, {
		switchId: id
	});
	
	Battle.clearTeam();
}

Battle.forfeit = function() {
	console.log('Forfeit!');
	net.sendBattlePacket(BATTLE_ACTION.RUN, {});
	
	Battle.endBattle();
}

Battle.drawDefaultMenu = async function() {
	Battle.clearMenu();
	
	Battle.panel = new SlickUI.Element.Panel(8, game.height - (108 + 8), game.width - 16, 108)
	Battle.slickUI.add(Battle.panel);
	
	//game.world.bringToTop(Battle.slickUI.container.displayGroup);
	
	let buttonOffsetX = Battle.panel.width / 2;
	let buttonWidth = Battle.panel.width / 4.1;
	
	// Fight button
	let fightButton = new SlickUI.Element.Button(buttonOffsetX + 4, 0, buttonWidth, 48);
	Battle.panel.add(fightButton);
	fightButton.add(new SlickUI.Element.Text(0, 0, "Fight")).center();
	await fightButton.events;
	
	if (Battle.frontPokemon.health <= 0) {
		//fightButton.events.onInputUp.removeAll();
		//fightButton.events.onInputDown.removeAll();
	} else {
		fightButton.events.onInputUp.add(function() {
			if (Battle.moveButtons == undefined || Battle.moveButtons.length == 0) {
				Battle.showMoves();
			} else {
				Battle.clearMoves();
			}
		});
	}
	
	let switchButton = new SlickUI.Element.Button(buttonOffsetX + 4, 50, buttonWidth, 48);
	Battle.panel.add(switchButton);
	switchButton.add(new SlickUI.Element.Text(0, 0, "Switch")).center(); 
	
	if (Battle.frontPokemon.health <= 0) {
		//switchButton.events.onInputUp.removeAll();
		//switchButton.events.onInputDown.removeAll();
	} else {
		switchButton.events.onInputUp.add(function() {
			if (Battle.teamButtons == undefined || Battle.teamButtons.length == 0) {
				Battle.showTeam();
			} else {
				Battle.clearTeam();
			}
		});
	}
	
	
	let itemButton = new SlickUI.Element.Button(buttonOffsetX + 4 + buttonWidth + 4, 0, buttonWidth, 48);
	Battle.panel.add(itemButton);
	itemButton.add(new SlickUI.Element.Text(0, 0, "Item")).center(); 
	
	let forfeitButton = new SlickUI.Element.Button(buttonOffsetX + 4 + buttonWidth + 4, 50, buttonWidth, 48);
	Battle.panel.add(forfeitButton);
	forfeitButton.add(new SlickUI.Element.Text(0, 0, "Forfeit")).center();
	await forfeitButton.events;
	forfeitButton.events.onInputUp.add(function() {
		Battle.forfeit();
	});
	
	if (Battle.menuSeparator != undefined) {
		Battle.menuSeparator.kill();
	}
	
	Battle.stdButtons = [fightButton, switchButton, itemButton, forfeitButton];
	
	Battle.menuSeparator = game.add.graphics(0, 0);
	
	Battle.menuSeparator.lineStyle(2, 0x999999);
	Battle.menuSeparator.moveTo(buttonOffsetX + 12, game.height - (108));
	Battle.menuSeparator.lineTo(buttonOffsetX + 12, game.height - 16);
	
	if (Battle.frontPokemon.health <= 0) {
		Battle.showTeam();
	}
	
//	Battle.sendToTop(Battle.slickUI);
}

Battle.battleOver = async function(packet) {
	
	if (Battle.showing != undefined) {
		await Battle.showing;
	}
	
	Battle.showing = new Promise(async function(resolve, reject) {
	
		Battle.clearMoves();
		Battle.clearButtons(Battle.stdButtons);
		Battle.clearMessageText();
		
		
		let msg = "";
		if (packet.winner_id == Game.player.id) {
			msg = "Victory!";
		} else {
			msg = "Loss.";
		}
		let messageText = new SlickUI.Element.Text(8, 8, msg);
		messageText.size = 16;
		Battle.panel.add(messageText);
		
		let buttonOffsetX = Battle.panel.width / 2;
		let buttonWidth = Battle.panel.width / 4.1;
		
		let exitButton = new SlickUI.Element.Button(buttonOffsetX + 4, 0, 2 * (buttonWidth + 2), 48 * 2);
		Battle.panel.add(exitButton);
		exitButton.add(new SlickUI.Element.Text(0, 0, "Exit Battle")).center();
		
		await exitButton.events;
		exitButton.events.onInputUp.add(function() {
			Battle.endBattle();
		});
		
		resolve();
	});
}

Battle.setup = function(initPacket) {
	
	Battle.battleId = initPacket.battle_id;
	
	// Hack:
	
	Battle.team = initPacket.pokemon_team;
	Battle.initPacket = initPacket;
	
}


Battle.showSummaries = async function(summaries, packet, resolveShow) {
	
	if (Battle.subShowing != undefined) {
		await Battle.subShowing;
	}
	
	Battle.subShowing = new Promise(function(resolve, reject) {
	
		if (summaries.length == 0) {
			// handle end packet...
			Battle.team = packet.pokemon_team;
			
			Battle.drawDefaultMenu();
			
			resolve();
			resolveShow();
			return;
		}
		
		let summary = summaries.shift();
		
		// Battle.showAttackPair({defendingId: 1, attack: 'basic', damage: 75}, {defendingId: 2, attack: 'basic', damage: 250}, 1);
	
		let SUMMARY_TYPE = {
			FIGHT: 0,
			SWITCH: 1
		};
		
		console.log(summary.msg);
		if (summary.msg != undefined && summary.msg.length != 0) {
			Battle.drawMessage(summary.msg);
		}
		
		console.log('Type: ' + summary.type);
		if (summary.type == SUMMARY_TYPE.FIGHT) {
			Battle.showAttackSummary({
				defendingId: summary.defending.id,
				health: summary.defending.health,
				animation: summary.animation
			}, function() {
				Game.time.events.add(Phaser.Timer.SECOND * .75, function() {
					resolve();
					Battle.showSummaries(summaries, packet, resolveShow);
				});
			});
		} else if (summary.type == SUMMARY_TYPE.SWITCH) {
			let pOut = Battle.getPokemonById(summary.pokemonOut.id);
			
			let pIn = Battle.team[0];
			if (pOut.owner_id == Game.player.id) {
				for(let i = 0; i < Battle.team.length; i++) {
					if (Battle.team[i].id == summary.pokemonIn.id) {
						
						pIn = Battle.team[i];
						break;
					}
				}
			} else {
				pIn = summary.pokemonIn;
			}
			
			Battle.doSwitch(pOut, pIn, function() {
				Game.time.events.add(Phaser.Timer.SECOND * .75, function() {
					resolve();
					Battle.showSummaries(summaries, packet, resolveShow);
				});
			});
		}
	});
	
}

Battle.handleUpdate = async function(packet) {
	console.log(packet);
	
	console.log(packet.update.summaries.slice(0));
	
	/*let userPokemon = pokemon_a;
	if (pokemon_b.owner_id == Game.player.id) {
		userPokemon = pokemon_b;
	}*/
	
	if (Battle.showing != undefined) {
		await Battle.showing;
	}
	
	Battle.showing = new Promise(function(resolve, reject) {
		Battle.showSummaries(packet.update.summaries, packet, resolve);
	});
	
}