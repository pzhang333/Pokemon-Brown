let alreadyRenderingTeamManager = false;

function renderTeamManager() {
	// initialization

	let selectedTeamManager = [];
	let teamMembersSelected = 0;

	let team = Game.player.pokemon;
	let selectedPokemon = team.filter(pokemon => pokemon.stored == false);

	for (let i=0; i<team.length; i++) {
		if (selectedPokemon.includes(team[i])) {
			selectedTeamManager.push(i);
			teamMembersSelected++;
		}
	}

	// panel
	let panel = new SlickUI.Element.Panel(Game.map.widthInPixels/4, Game.map.heightInPixels/15, Game.map.widthInPixels/2, Game.map.heightInPixels/2.5);
	Game.slickUI.add(panel);
	// header
	let header = new SlickUI.Element.Text(panel.width/2 - panel.width/8 , 10, "Team Manager ");

	// x-out button
	let xOut = game.add.sprite(0, 0, 'x_icon');
	xOut.inputEnabled = true;
	xOut.events.onInputUp.add(function () {
		if (selectedTeamManager.length > 0) {
	    	// x-out of page
	    	panel.destroy();
	    	// saving our new team
	    	selectedPokemon = [];
	    	for (let i=0; i<selectedTeamManager.length; i++) {
	    		console.log(i);
	    		selectedPokemon.push(team[selectedTeamManager[i]].id);
	    	}
	    	net.updateTeam(Game.player.id, selectedPokemon);
	    	alreadyRenderingTeamManager = false;
    	}
    });

   	panel.add(header);
	panel.add(new SlickUI.Element.DisplayObject(panel.width/1.1, panel.height/25, xOut));

	renderTeamManagerHelper(0, selectedTeamManager, panel, team, teamMembersSelected);
}

function renderTeamManagerHelper(startIndex, selectedTeamManager, panel, team, teamMembersSelected) {

	// page changers
	let upArrow = game.add.sprite(0, 0, 'up_arrow');
	let downArrow = game.add.sprite(0, 0, 'down_arrow');
	downArrow.inputEnabled = true;
	upArrow.inputEnabled = true;
	downArrow.events.onInputUp.add(function () {
    	// scroll down
    	if (startIndex+6 < team.length) {
    		renderTeamManagerHelper(startIndex+6, selectedTeamManager, panel, team, teamMembersSelected);
    	}
    });
	upArrow.events.onInputUp.add(function () {
    	// scroll up
    	if (startIndex-6 >= 0) {
    		renderTeamManagerHelper(startIndex-6, selectedTeamManager, panel, team, teamMembersSelected);
    	}
    });

	panel.add(new SlickUI.Element.DisplayObject(panel.width/1.2, panel.height/1.1, upArrow));
	panel.add(new SlickUI.Element.DisplayObject(panel.width/1.1, panel.height/1.1, downArrow));

	// pokemon

	for (let i=0; i<2; i++) {
		for (let k=0; k<3; k++) {
			let button = new SlickUI.Element.Button(k*panel.width/3, i*panel.height/3 + panel.height/7.5, panel.width/3, panel.height/3);
			panel.add(button);
			if (3*i + k + startIndex < team.length) {
				// getting pokemon

				let pok = team[3*i+k+startIndex];
				Battle.custDrawFrontPokemon(pok, function(key) {
					let pokemon = game.add.sprite(0, 0, key);
					let scale = Math.min(65/pokemon.width, 65/pokemon.height);
					pokemon.scale.setTo(scale, scale);
					pokemon.anchor.setTo(0.5, 0.5);

					if (selectedTeamManager.includes(3*i + k + startIndex)) {
						button.sprite.loadTexture(button.spriteOn.texture);
					}

					button.events.onInputDown.add(function () {
						if (teamMembersSelected >= 5 && !selectedTeamManager.includes(3*i + k + startIndex)){
							button.sprite.loadTexture(button.spriteOff.texture);
						}
					}, this);

					button.events.onInputUp.add(function () {
						if (selectedTeamManager.includes(3*i + k + startIndex)) {
							button.sprite.loadTexture(button.spriteOff.texture);
							selectedTeamManager.splice(selectedTeamManager.indexOf(3*i + k + startIndex), 1);
							teamMembersSelected--;
						} else if (teamMembersSelected < 5){
							button.sprite.loadTexture(button.spriteOn.texture);
							selectedTeamManager.push(3*i + k + startIndex);
							teamMembersSelected++;
						}
					}, this);

					let level = new SlickUI.Element.Text(button.width/1.5, button.height/2.75, "lvl " + pok.level);


					scaleFont(button, pok.nickname, 16)

					button.add(level);

					button.add(new SlickUI.Element.DisplayObject(panel.width/10, panel.height/8, pokemon));

				});
			}
		}
	}
}

function renderTradeUpdate(response) {
	if (panelMessage != null) {
		panelMessage.destroy();
	}
  destroyAllOverhead(battler);

	panelMessage = new SlickUI.Element.Panel(Game.map.widthInPixels/4, Game.map.heightInPixels/6, Game.map.widthInPixels/2, Game.map.heightInPixels/10);
	Game.slickUI.add(panelMessage);
	let header = new SlickUI.Element.Text(10 , 10, "Trade Update: " + response);
	let okButton = new SlickUI.Element.Button(panelMessage.width/2 - Game.map.widthInPixels/(9.5*2), Game.map.heightInPixels/23, Game.map.widthInPixels/9.5, Game.map.heightInPixels/25);

	panelMessage.add(header);
	panelMessage.add(okButton);

	okButton.add(new SlickUI.Element.Text(0, 0, "Ok")).center();
	okButton.events.onInputUp.add(function () {
		panelMessage.destroy();
		Game.playerFrozen = false;
		panelMessage = null;
		pending = false;
	});
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
