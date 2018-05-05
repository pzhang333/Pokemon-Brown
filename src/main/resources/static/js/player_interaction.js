let pending = false;
let clicked_player = null;
let battler = null;
let panelMessage = null;

function playerInteraction() {
	if (panelMessage != null) {
		console.log("returning on click because panel message not null");
		return;
	}
	let player = this;
	if (pending) {
		cancelChallengeUI(clicked_player);
		clicked_player = null;
		pending = false;
		return;
	}
	if (clicked_player != null) {
		clicked_player.sprite.challenge.kill();
		clicked_player = null;
		return;
	}
	let dist = Math.sqrt(Math.pow(Game.player.x - player.x, 2) + Math.pow(Game.player.y - player.y, 2));
	if (dist > 5) {
		return;
	}
	drawOptionsMenu(player);
	clicked_player = player;
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
	player.sprite.challenge.kill();
	let dist = Math.sqrt(Math.pow(Game.player.x - player.x, 2) + Math.pow(Game.player.y - player.y, 2));
	if (dist <= 5) {
		net.requestChallenge(Game.player.id, player.id);
		battler = player;
		let pendingImage = game.add.image(-48/3, -2*Game.map.tileHeight, 'pending_challenge_button');
		pending = true;
		player.sprite.challenge = player.sprite.addChild(pendingImage);
		Game.playerFrozen = true;
	}
}

function renderChallenge(player) {
	if (panelMessage != null) {
		panelMessage.destroy();
	}
	if (clicked_player != null) {
		clicked_player.sprite.challenge.kill();
		clicked_player = null;
	}
	Game.playerFrozen = true;
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
		panelMessage = null;
		Game.playerFrozen = false;
	});
	acceptButton.events.onInputUp.add(function () {
		net.acceptChallenge(Game.player.id, true);
		panelMessage.destroy();
		panelMessage = null;
		Game.playerFrozen = false;
	});
}

function renderChallengeUpdate(response) {
	if (panelMessage != null) {
		panelMessage.destroy();
	}
	if (clicked_player != null) {
		clicked_player.sprite.challenge.kill();
		clicked_player = null;
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
		Game.playerFrozen = false;
		panelMessage = null;
		pending = false;
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
