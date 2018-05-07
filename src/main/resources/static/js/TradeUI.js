let tradeWindowRowSize = 2;
let tradeWindowColumnSize = 2;
let activeTrade = null;
let tradePanel = null;
let yourCoinOffer = 0;
let yourPokemonOffer = [];
let youAccepted = false;
let otherPlayerPokemon = [];
let otherPlayerCoins = 0;
let otherPlayerId = -1;
let otherPlayerAccepted = false;

function renderTradeWindow(otherPokemon, otherCoins, otherId, otherAccept, youAccept, first) {
	// initialization

  youAccepted = false;

  if (first) {
    yourCoinOffer = 0;
    yourPokemonOffer = [];
  }

  otherPlayerPokemon = otherPokemon;
  otherPlayerCoins = otherCoins;
  otherPlayerId = otherId;
  otherPlayerAccepted = otherAccept;
  youAccepted = youAccept;

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
	let acceptTradeButton = new SlickUI.Element.Button(tradePanel.width/4-tradePanel.width/8, tradePanel.height*0.885, tradePanel.width/4, tradePanel.height/9.2);
	tradePanel.add(acceptTradeButton);
  let acceptText = new SlickUI.Element.Text(0, 0, "Accept Trade");
  acceptTradeButton.add(acceptText)
  if (youAccepted) {
    acceptText.text.text = "Cancel";
    acceptTradeButton.sprite.loadTexture(acceptTradeButton.spriteOn.texture)
  } else {
    acceptTradeButton.sprite.loadTexture(acceptTradeButton.spriteOff.texture)
  }
  acceptText.center();
	acceptTradeButton.inputEnabled = true;
	acceptTradeButton.events.onInputDown.add(function () {
    sendTradeUpdate(!youAccepted)
    youAccepted = !youAccepted
    /*if (youAccepted) {
      acceptText.text.text = "Cancel";
    } else {
      acceptText.text.text = "Accept Trade";
    }
    acceptText.center();*/
  });

  let otherStatusText = new SlickUI.Element.Text(tradePanel.width*3/4, tradePanel.height*0.905, "Waiting");
  tradePanel.otherStatusText = otherStatusText;
  tradePanel.add(otherStatusText)
  otherStatusText.x = tradePanel.width*3/4 - (otherStatusText.text.width / 2);
  if (otherPlayerAccepted) {
    otherStatusText.text.text = "Accepted";
    otherStatusText.x = tradePanel.width*3/4 - (otherStatusText.text.width / 2);
  }

	// core panels
	let yourPanel = new SlickUI.Element.Panel(tradePanel.width/50, tradePanel.height/10, tradePanel.width/2.1, tradePanel.height/1.3);
	tradePanel.add(yourPanel);
	let otherPanel = new SlickUI.Element.Panel(tradePanel.width/1.98, tradePanel.height/10, tradePanel.width/2.1, tradePanel.height/1.3);
	tradePanel.add(otherPanel);
	renderTradeWindowHelper(0, yourPanel, true);
	renderTradeWindowHelper(0, otherPanel, false);
}

function sendTradeUpdate(accepted) {
  // send update packet
    let pokemonIds = yourPokemonOffer.map(index => Game.player.pokemon.filter(pokemon => pokemon.stored == false)[index].id);
    let otherPlayerPokemonIds = otherPlayerPokemon.map(pokemon => pokemon.id);
    net.updateOpenTrade(Game.player.id, otherPlayerId, accepted, yourCoinOffer, pokemonIds, otherPlayerCoins, otherPlayerPokemonIds);
}

function renderTradeWindowHelper(startIndex, panel, yourMenu) {
  let team;
  if (yourMenu) {
    team = Game.player.pokemon.filter(pokemon => pokemon.stored == false);
  } else {
    team = otherPlayerPokemon;
  }
	// panel headers
	let header;
	if (yourMenu) {
		header = new SlickUI.Element.Text(0 , 10, "Your offer:");

    // page changers
 		let upArrowSmall = game.add.sprite(0, 0, 'up_arrow');
    upArrowSmall.scale.setTo(0.5);
 		let downArrowSmall = game.add.sprite(0, 0, 'down_arrow');
    downArrowSmall.scale.setTo(0.5);

  	// coin
  	let coin = game.add.sprite(0, 0, 'coin_trade');
    let coinObj = new SlickUI.Element.DisplayObject(0, panel.height*0.843, coin)
  	panel.add(coinObj);
    let coinText = new SlickUI.Element.Text(0, panel.height*0.87, "x" + yourCoinOffer)
    panel.add(coinText);
    let coinUp = new SlickUI.Element.DisplayObject(0, 0, upArrowSmall);
    let coinDown = new SlickUI.Element.DisplayObject(0, 0, downArrowSmall);
    panel.add(coinUp);
    panel.add(coinDown);
    coinObj.x = (panel.width / 2) - ((coinObj.sprite.width + 3 + coinText.text.textWidth + 3 + coinUp.sprite.width) / 2)
    coinText.x = coinObj._x + 3 + coinObj.sprite.width
    coinUp.x = coinText.x + coinText.text.textWidth + 6
    coinDown.x = coinUp._x
    coinUp.y = coinObj._y + (coinObj.sprite.height / 2) - coinUp.sprite.height - 3
    coinDown.y = coinUp._y + coinUp.sprite.height + 6

    upArrowSmall.inputEnabled = true;
    downArrowSmall.inputEnabled = true;

    upArrowSmall.events.onInputUp.add(function () {
      if (yourCoinOffer < 100) {
        yourCoinOffer += 5;
      } else if (yourCoinOffer < 500) {
        yourCoinOffer += 25;
      } else {
        yourCoinOffer += 50;
      }
      yourCoinOffer = Math.max(0, yourCoinOffer)
      yourCoinOffer = Math.min(yourCoinOffer, Game.player.currency)
      sendTradeUpdate(false);
    });

    downArrowSmall.events.onInputUp.add(function () {
      if (yourCoinOffer < 100) {
        yourCoinOffer -= 5;
      } else if (yourCoinOffer < 500) {
        yourCoinOffer -= 25;
      } else {
        yourCoinOffer -= 50;
      }
      yourCoinOffer = Math.max(0, yourCoinOffer)
      yourCoinOffer = Math.min(yourCoinOffer, Game.player.currency)
      sendTradeUpdate(false);
    });

    // page changers
    let upArrow = game.add.sprite(0, 0, 'up_arrow');
    let downArrow = game.add.sprite(0, 0, 'down_arrow');

 		downArrow.inputEnabled = true;
 		upArrow.inputEnabled = true;
 		downArrow.events.onInputUp.add(function () {
    	// scroll down
    	if (startIndex+tradeWindowRowSize*tradeWindowColumnSize < team.length) {
    		renderTradeWindowHelper(startIndex+tradeWindowRowSize*tradeWindowColumnSize, panel, yourMenu);
    	}
    });
 		upArrow.events.onInputUp.add(function () {
    	// scroll up
    	if (startIndex-tradeWindowRowSize*tradeWindowColumnSize >= 0) {
    		renderTradeWindowHelper(startIndex-tradeWindowRowSize*tradeWindowColumnSize, panel, yourMenu);
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
						if (yourPokemonOffer.includes(tradeWindowRowSize*i + k + startIndex)) {
							button.sprite.loadTexture(button.spriteOn.texture);
						}
						button.events.onInputDown.add(function () {
              let changed = false;
              if (!yourPokemonOffer.includes(tradeWindowRowSize*i + k + startIndex)) {
                button.sprite.loadTexture(button.spriteOn.texture);
                yourPokemonOffer.push(tradeWindowRowSize*i + k + startIndex);
                changed = true;
              } else if (yourPokemonOffer.length <= 4) {
                button.sprite.loadTexture(button.spriteOff.texture);
                yourPokemonOffer.splice(yourPokemonOffer.indexOf(tradeWindowRowSize*i + k + startIndex), 1);
                changed = true;
              }
              if (changed) {
                sendTradeUpdate(false);
              }
						}, this);
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
