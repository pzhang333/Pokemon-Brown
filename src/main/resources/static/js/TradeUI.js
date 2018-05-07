let tradeWindowRowSize = 2;
let tradeWindowColumnSize = 2;
let lockedIn = false;
let coinInputField;
let activeTrade = null;
let tradePanel = null;
let yourCoinOffer = 0;
let yourPokemonOffer = [];

function renderTradeWindow(otherPlayerPokemon, otherPlayerCoins, otherPlayerId, otherAccepted) {
	// initialization

	let pokemonSelected = 0;

	let team = Game.player.pokemon.filter(pokemon => pokemon.stored == false);
	console.log(team);
	try {
		tradePanel.destroy();
	} catch (err) {
		tradePanel = null;
	}
	
	Game.playerFrozen = true;

	// panel
	tradePanel = new SlickUI.Element.Panel(Game.map.widthInPixels/6, Game.map.heightInPixels/25, Game.map.widthInPixels/1.5, Game.map.heightInPixels/2.3);
	Game.slickUI.add(tradePanel);

	// header
	let header = new SlickUI.Element.Text(tradePanel.width/2 - tradePanel.width/25 , 10, "Trade");

	// x-out button
	let xOut = game.add.sprite(0, 0, 'x_icon');
	xOut.inputEnabled = true;
	xOut.events.onInputUp.add(function () {
			tradePanel.destroy();
	    	// opening trade with yourself cancels open trade
	    	net.requestTrade(Game.player.id, Game.player.id);
	    	Game.playerFrozen = false;
	    });

	tradePanel.add(header);
	tradePanel.add(new SlickUI.Element.DisplayObject(tradePanel.width-35, 10, xOut));

	// accept trade button
	let acceptTradeButton = new SlickUI.Element.Button(tradePanel.width/2-tradePanel.width/8, tradePanel.height*0.885, tradePanel.width/4, tradePanel.height/9.2);
	tradePanel.add(acceptTradeButton);
	acceptTradeButton.add(new SlickUI.Element.Text(0, 0, "Accept Trade")).center();
	acceptTradeButton.inputEnabled = true;
	acceptTradeButton.events.onInputDown.add(function () {
    	// accept trade
    	let pokemonIds = yourPokemonOffer.map(index => team[index].id);
    	let otherPlayerPokemonIds = otherPlayerPokemon.map(pokemon => pokemon.id);
    	net.updateOpenTrade(Game.player.id, otherPlayerId, true, yourCoinOffer, pokemonIds, otherPlayerCoins, otherPlayerPokemonIds);
    });

    if (otherAccepted) {
    	acceptTradeButton.sprite.loadTexture(acceptTradeButton.spriteOn.texture);
    }

	// core panels
	let yourPanel = new SlickUI.Element.Panel(tradePanel.width/50, tradePanel.height/10, tradePanel.width/2.1, tradePanel.height/1.3);
	tradePanel.add(yourPanel);
	let otherPanel = new SlickUI.Element.Panel(tradePanel.width/1.98, tradePanel.height/10, tradePanel.width/2.1, tradePanel.height/1.3);
	tradePanel.add(otherPanel);
	renderTradeWindowHelper(0, yourPokemonOffer, yourPanel, team, pokemonSelected, true, otherPlayerCoins, otherPlayerId, otherPlayerPokemon);
	renderTradeWindowHelper(0, yourPokemonOffer, otherPanel, otherPlayerPokemon, pokemonSelected, false, otherPlayerCoins, otherPlayerId, otherPlayerPokemon);
}

function renderTradeWindowHelper(startIndex, selectedPokemon, panel, team, pokemonSelected, yourMenu, otherPlayerCoins, otherPlayerId, otherPlayerPokemon) {
	// panel headers
	let header;
	if (yourMenu) {
		header = new SlickUI.Element.Text(0 , 10, "Your offer:");
		let updateTradeButton = new SlickUI.Element.Button(panel.width/2, panel.height*0.85, panel.width/2.2, panel.height/7.5);
		panel.add(updateTradeButton);
		updateTradeButton.add(new SlickUI.Element.Text(0, 0, "Lock Offer")).center();
		if (lockedIn){
			updateTradeButton.sprite.loadTexture(updateTradeButton.spriteOn.texture);
		}
		updateTradeButton.inputEnabled = true;
		updateTradeButton.events.onInputUp.add(function () {
			if (lockedIn){
				updateTradeButton.sprite.loadTexture(updateTradeButton.spriteOff.texture);
				coinInputField.inputEnabled = true;
			} else {
				updateTradeButton.sprite.loadTexture(updateTradeButton.spriteOn.texture);
				coinInputField.inputEnabled = false;
				yourCoinOffer = coinInputField.value;
				if (yourCoinOffer == "") {
					yourCoinOffer = 0;
				}		
				// send update packet
    			let pokemonIds = yourPokemonOffer.map(index => team[index].id);
		    	let otherPlayerPokemonIds = otherPlayerPokemon.map(pokemon => pokemon.id);    			
		    	net.updateOpenTrade(Game.player.id, otherPlayerId, false, yourCoinOffer, pokemonIds, otherPlayerCoins, otherPlayerPokemonIds);
    		}
    		lockedIn = !lockedIn;
    	});



    	// coin
    	let coin = game.add.sprite(0, 0, 'coin_trade');
    	panel.add(new SlickUI.Element.DisplayObject(10, panel.height*0.843, coin));
		// coin input field
 		coinInputField = game.add.inputField(0, 0, {
 			width: 40,
 			padding: 5,
 			fill: '#000000',
 			stroke: '#000000',
 			backgroundColor: '#ffffff',
 			borderWidth: 2,
 			borderColor: '#919191',
 			borderRadius: 3,	
 			textAlign: 'center',
 			font: '18px Arial',
 			placeHolder: yourCoinOffer,
 			placeHolderColor: '#000000',
 			cursorColor: '#000000'
 		});

 		panel.add(new SlickUI.Element.DisplayObject(75, panel.height*0.863, coinInputField));
 		panel.add(new SlickUI.Element.Text(58, panel.height*0.87, "x"));

 		// page changers
 		let upArrow = game.add.sprite(0, 0, 'up_arrow');
 		let downArrow = game.add.sprite(0, 0, 'down_arrow');
 		downArrow.inputEnabled = true;
 		upArrow.inputEnabled = true;
 		downArrow.events.onInputUp.add(function () {
    	// scroll down
    	if (startIndex+tradeWindowRowSize*tradeWindowColumnSize < team.length) {
    		renderTradeWindowHelper(startIndex+tradeWindowRowSize*tradeWindowColumnSize, selectedPokemon, panel, team, pokemonSelected, yourMenu, otherPlayerCoins, otherPlayerId, otherPlayerPokemon);
    	}
    });
 		upArrow.events.onInputUp.add(function () {
    	// scroll up
    	if (startIndex-tradeWindowRowSize*tradeWindowColumnSize >= 0) {
    		renderTradeWindowHelper(startIndex-tradeWindowRowSize*tradeWindowColumnSize, selectedPokemon, panel, team, pokemonSelected, yourMenu, otherPlayerCoins, otherPlayerId, otherPlayerPokemon);
    	}
    });

 		panel.add(new SlickUI.Element.DisplayObject(panel.width/1.29, panel.height*0.02, upArrow));
 		panel.add(new SlickUI.Element.DisplayObject(panel.width/1.13, panel.height*0.02, downArrow));
 	} else {
 		header = new SlickUI.Element.Text(0, 10, "Their offer:");
    	// coin
    	let coin = game.add.sprite(0, 0, 'coin_trade');
    	panel.add(new SlickUI.Element.DisplayObject(panel.width/2-panel.width/7, panel.height*0.843, coin)); 
 		panel.add(new SlickUI.Element.Text(panel.width/1.97, panel.height*0.87, "x" + otherPlayerCoins));    
 	}

 	panel.add(header).centerHorizontally();


	// pokemon
	for (let i=0; i<tradeWindowColumnSize; i++) {
		for (let k=0; k<tradeWindowRowSize; k++) {
			let button = new SlickUI.Element.Button(k*panel.width/tradeWindowRowSize, i*panel.height/(tradeWindowColumnSize+1) + panel.height/7.5, panel.width/tradeWindowRowSize, panel.height/(tradeWindowColumnSize+1));
			panel.add(button);
			if (tradeWindowRowSize*i + k + startIndex < team.length) {
				// getting pokemon

				let pok = team[tradeWindowRowSize*i+k+startIndex];
				console.log("pok", pok);
				Battle.custDrawFrontPokemon(pok, function(key) {
					let pokemon = game.add.sprite(0, 0, key);
					let scale = Math.min(65/pokemon.width, 65/pokemon.height);
					pokemon.scale.setTo(scale, scale);
					pokemon.anchor.setTo(0.5, 0.5);

					if (yourMenu) {
						if (selectedPokemon.includes(tradeWindowRowSize*i + k + startIndex)) {
							button.sprite.loadTexture(button.spriteOn.texture);
						} 
						button.events.onInputDown.add(function () {
							if (pokemonSelected >= 4 && !selectedPokemon.includes(tradeWindowRowSize*i + k + startIndex)){
								if (!lockedIn) {
									button.sprite.loadTexture(button.spriteOff.texture);
								} else {
									button.sprite.loadTexture(button.spriteOn.texture);
								}
							} else {
								if (lockedIn && selectedPokemon.includes(tradeWindowRowSize*i + k + startIndex)) {
									button.sprite.loadTexture(button.spriteOn.texture);
								} else if (lockedIn) {
									button.sprite.loadTexture(button.spriteOff.texture);
								}
							}
							}, this);

						button.events.onInputUp.add(function () {
							if (selectedPokemon.includes(tradeWindowRowSize*i + k + startIndex)) {
								if(!lockedIn) {
									button.sprite.loadTexture(button.spriteOff.texture);
									selectedPokemon.splice(selectedPokemon.indexOf(tradeWindowRowSize*i + k + startIndex), 1);
									pokemonSelected--;
								} else {
									button.sprite.loadTexture(button.spriteOn.texture);
								}
							} else if (pokemonSelected < 4){
								if(!lockedIn) {
									button.sprite.loadTexture(button.spriteOn.texture);
									selectedPokemon.push(tradeWindowRowSize*i + k + startIndex);
									pokemonSelected++;
								} else {
									button.sprite.loadTexture(button.spriteOff.texture);
								}
							}
						}, this);
					} else {
						button.events.onInputDown.add(function () {
							button.sprite.loadTexture(button.spriteOff.texture);
						});
					}

					let level = new SlickUI.Element.Text(button.width/1.5, button.height/2.75, "lvl " + pok.level);


					scaleFont(button, pok.nickname, 16)

					button.add(level);

					button.add(new SlickUI.Element.DisplayObject(panel.width/10, panel.height/8, pokemon));

				});
			}
		}
	}
}

function scaleFont(button, nik, size) {
	let nickname = new SlickUI.Element.Text(0, button.height/1.3, nik);
	nickname.size = size;
	button.add(nickname);
	nickname.centerHorizontally();
	if (nickname.text.textHeight > 24) {
		nickname.text.destroy();
		scaleFont(button, nik, size - 1);
	}
}