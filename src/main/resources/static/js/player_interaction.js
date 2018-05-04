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
		player.sprite.challenge.kill();
	}
}

function drawOptionsMenu(player) {
	let challengeImage = game.add.image(-48/3, -2*Game.map.tileHeight, 'challenge_button');
	challengeImage.inputEnabled = true;
    challengeImage.events.onInputDown.add(challengePlayer, player);	
   	player.sprite.challenge = player.sprite.addChild(challengeImage);
}

function challengePlayer() {
	let player = this;
	// send packet
	this.sprite.challenge.kill();
}