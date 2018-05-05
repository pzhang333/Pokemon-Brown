function renderTeamManager(startIndex) {
	// intialization
	let selectedTeamManager = [];
	let teamMembersSelected = 0;
	let team = Game.player.pokemon;
	let selectedPokemon = team.filter(function (pokemon) {
		!pokemon.stored
	});
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
	panel.add(header);
	let upArrow = game.add.sprite(0, 0, 'up_arrow');
	let downArrow = game.add.sprite(0, 0, 'down_arrow');
	downArrow.inputEnabled = true;
	upArrow.inputEnabled = true;
	downArrow.events.onInputUp.add(function () {
    	// scroll down
    	if (startIndex+6+1 < team.length) {
    		renderTeamManager(startIndex+6);
    	}
    });
	upArrow.events.onInputUp.add(function () {
    	// scroll up
    	if (startIndex-6 >= 0) {
    		renderTeamManager(startIndex-6);
    	}
    });

	panel.add(new SlickUI.Element.DisplayObject(panel.width/1.2, panel.height/1.1, upArrow));
	panel.add(new SlickUI.Element.DisplayObject(panel.width/1.1, panel.height/1.1, downArrow));

	// pokemon

	for (let i=0; i<2; i++) {
		for (let k=0; k<3; k++) {
			if (3*i + k + startIndex < team.length) {
				let button = new SlickUI.Element.Button(k*panel.width/3, i*panel.height/3 + panel.height/7.5, panel.width/3, panel.height/3);
				// getting pokemon


				Battle.custDrawFrontPokemon(team[3*i+k+startIndex], function(key) {
					let pokeball = game.add.sprite(0, 0, key);
					pokeball.anchor.setTo(0.5, 0.5);
					panel.add(button);

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

					let level = new SlickUI.Element.Text(button.width/1.5, button.height/2.75, "lvl " + (startIndex+k + i*3));
					let nickname = new SlickUI.Element.Text(button.width/1.5, button.height/1.5, "name " + k);
					button.add(level);
					button.add(nickname);

					button.add(new SlickUI.Element.DisplayObject(panel.width/8, panel.height/4.2, pokeball));

				});
			}
		}
	}
}