let pending = false;
let battler = null;
let panelMessage = null;

function playerInteraction() {
	let player = this;
		if (player.clicked != undefined) {
			player.clicked = !player.clicked;
		} else {
			player.clicked = true;
		}
		if (player.clicked) {
			drawOptionsMenu(player);
		} else {
			if (!pending && player.sprite.challenge != undefined) {
				player.sprite.challenge.kill();
			} else if (pending) {
				cancelChallengeUI(player);
			}
		} 
}

function drawOptionsMenu(player) {
	let challengeImage = game.add.image(-48/3, -2*Game.map.tileHeight, 'challenge_button');
	challengeImage.inputEnabled = true;
	challengeImage.events.onInputDown.add(challengePlayer, player);	
	player.sprite.challenge = player.sprite.addChild(challengeImage);
}

function challengePlayer() {
	battler = null;
	let player = this;
	net.requestChallenge(Game.player.id, player.id);
	battler = player;
	player.sprite.challenge.kill();
	let pendingImage = game.add.image(-48/3, -2*Game.map.tileHeight, 'pending_challenge_button');
	pending = true;
	player.sprite.challenge = player.sprite.addChild(pendingImage);
	Game.playerFrozen = true;
}

function renderChallenge(player) {
	if (panelMessage != null) {
		panelMessage.destroy();
	}
	panelMessage = new SlickUI.Element.Panel(Game.map.widthInPixels/4, Game.map.heightInPixels/6, Game.map.widthInPixels/2, Game.map.heightInPixels/10);
	Game.slickUI.add(panelMessage);
	let header = new SlickUI.Element.Text(10 , 10, "Accept challenge");
	let playerInfo = new SlickUI.Element.Text(10 , 70, Game.players[player.id].username + " (" + Game.players[player.id].elo + ")");
	let rejectButton = new SlickUI.Element.Button(Game.map.widthInPixels/2.75, Game.map.heightInPixels/40, Game.map.widthInPixels/9.5, Game.map.heightInPixels/25);
	let acceptButton = new SlickUI.Element.Button(Game.map.widthInPixels/4.3, Game.map.heightInPixels/40, Game.map.widthInPixels/9.5, Game.map.heightInPixels/25);

	panelMessage.add(header);
	panelMessage.add(playerInfo);
	panelMessage.add(rejectButton);
	panelMessage.add(acceptButton);

	rejectButton.add(new SlickUI.Element.Text(0, 0, "Reject")).center();
	acceptButton.add(new SlickUI.Element.Text(0, 0, "Accept")).center();
	rejectButton.events.onInputUp.add(function () {
		net.rejectChallenge(Game.player.id, false);
		panelMessage.destroy();
	});
	acceptButton.events.onInputUp.add(function () {
		net.acceptChallenge(Game.player.id, true);
		panelMessage.destroy();
	});
}

function renderChallengeUpdate(response) {
	if (panelMessage != null) {
		panelMessage.destroy();
	}
	panelMessage = new SlickUI.Element.Panel(Game.map.widthInPixels/4, Game.map.heightInPixels/6, Game.map.widthInPixels/2, Game.map.heightInPixels/10);
	Game.slickUI.add(panelMessage);
	let header = new SlickUI.Element.Text(10 , 10, "Challenge Update: " + response);
	let okButton = new SlickUI.Element.Button(panelMessage.width/2 - Game.map.widthInPixels/(9.5*2), Game.map.heightInPixels/23, Game.map.widthInPixels/9.5, Game.map.heightInPixels/25);

	panelMessage.add(header);
	panelMessage.add(okButton);

	okButton.add(new SlickUI.Element.Text(0, 0, "Ok")).center();
	okButton.events.onInputUp.add(function () {
		panelMessage.destroy();
		if (battler != null && battler.sprite.challenge != undefined) {
			battler.sprite.challenge.kill();
		}
	});
}

function cancelChallengeUI(player) {
	net.cancelChallenge(Game.player.id);
	Game.playerFrozen = false;
	pending = false;
	battler = null;
	panelMessage = null;
	if (player.sprite.challenge != undefined) {
		player.sprite.challenge.kill();
	}
}
