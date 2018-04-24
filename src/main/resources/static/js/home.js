var Home = {
	maxNameLength : 20 // max length of the name of the player
};

Home.init = function() {
	game.scale.pageAlignHorizontally = true;
	game.add.plugin(PhaserInput.Plugin);
};

Home.preload = function() {
	game.load.atlasJSONHash('atlas1', 'assets/sprites/pokemon_atlas1.png', 'assets/sprites/pokemon_atlas1.json'); // PNJ, HUD, marker, achievements ...
};

Home.create = function() {

	Home.displayLogo();
	Home.displayLogin();
};

Home.displayLogo = function(xScale, yScale) {
	
	if (xScale == undefined) {
		xScale = .5;
	}
	
	if (yScale == undefined) {
		yScale = .5;
	}
	
	let logo = game.add.sprite(0, 2, 'atlas1', 'logo');

	logo.scale.setTo(xScale, yScale);

	logo.anchor.set(0.5, 0);
	logo.x = game.width / 2;
	logo.y = 0;

	logo.hideTween = game.add.tween(logo);
    logo.hideTween.to({alpha: 0}, Phaser.Timer.SECOND * 0.2);
	Home.logo = logo;
}

Home.makeScroll = function(xScale, yScale) {

	
	if (xScale == undefined) {
		xScale = 1;
	}
	
	if (yScale == undefined) {
		yScale = 1;
	}
	
	var scroll = game.add.sprite(0, 0, 'atlas1', 'grey-panel');

	scroll.x = (game.width / 2) - (scroll.width / 2);
	scroll.y = (game.height / 2) - (scroll.height / 2.5);
	
	scroll.scale.setTo(xScale, yScale);


	//scroll.addChild(game.add.sprite(-78,0,'atlas1','scroll_3'));
	//scroll.addChild(game.add.sprite(scroll.width,0,'atlas1','scroll_2'));
   
	scroll.fixedToCamera = true;
	scroll.alpha = 0;
	scroll.visible = false;
	
	return scroll;
};

Home.setFadeTweens = function(element){
	var speedCoef = 0.2;
	element.showTween = game.add.tween(element);
	element.hideTween = game.add.tween(element);
	element.showTween.to({alpha: 1}, Phaser.Timer.SECOND * speedCoef);
	element.hideTween.to({alpha: 0}, Phaser.Timer.SECOND * speedCoef);
	element.hideTween.onComplete.add(function() {
		element.visible = false;
	}, this);
};

Home.createUsernameField = function(placeholder) {
	
	if (placeholder == undefined) {
		placeholder = "Username";
	}
	
	let inputField = game.add.inputField(185, 160, {
		width: 300,
		padding: 10,
		fill: '#EEEEEE',
		stroke: '#EEEEEE',
		backgroundColor: '#808080',
		borderWidth: 2,
		borderColor: '#919191',
		borderRadius: 3,
		font: '18px pixel',
		placeHolder: placeholder,
		placeHolderColor: '#EEEEEE',
		cursorColor: '#EEEEEE'
	});
	
	return inputField;
}

Home.createPasswordField = function() {

	let inputField = game.add.inputField(185, 160, {
		width: 300,
		padding: 10,
		fill: '#EEEEEE',
		stroke: '#EEEEEE',
		backgroundColor: '#808080',
		borderWidth: 2,
		borderColor: '#919191',
		borderRadius: 3,
		font: '18px pixel',
		placeHolderColor: '#EEEEEE',
		cursorColor: '#EEEEEE',
		placeHolder: 'Password',
		type: PhaserInput.InputType.password
	});

	return inputField;
}

Home.displayRegister = function() {
	if (Home.scroll != undefined) {
		Home.scroll.destroy();
	}
	
	let stdTextStyle = {
		font: '22px pixel',
		fill: 'white'
	};

	
	Home.scroll = Home.makeScroll();
	Home.setFadeTweens(Home.scroll);
	Home.scroll.visible = true;
	Home.scroll.showTween.start();
	
	Home.statusText = game.add.text(0, 50, "Register", {
		font: '22px pixel',
		fill: '#cc0000'
	});
	Home.statusText.anchor.set(0.5, 0.5);
	Home.statusText.x = (Home.scroll.width / 2);
	Home.statusText.visible = false;
	Home.scroll.addChild(Home.statusText);

	// (Home.scroll.width / 2) - (Home.inputField.width / 2);
	Home.userField = Home.createUsernameField();
	Home.userField.x = (Home.scroll.width / 2) - (Home.userField.width / 2) - Home.userField.inputOptions.padding;
	Home.userField.y -= 80;
	Home.scroll.addChild(Home.userField);
	
	Home.emailField = Home.createUsernameField("Email");
	Home.emailField.x = (Home.scroll.width / 2) - (Home.userField.width / 2) - Home.userField.inputOptions.padding;
	Home.emailField.y -= 25;
	Home.scroll.addChild(Home.emailField);

	Home.passwordField = Home.createPasswordField();
	Home.passwordField.x = (Home.scroll.width / 2) - (Home.emailField.width / 2) - Home.emailField.inputOptions.padding;
	Home.passwordField.y = Home.emailField.y + Home.emailField.height + 15;
	Home.scroll.addChild(Home.passwordField);
	
	Home.nicknameField = Home.createUsernameField("Pokemon Nickname");
	Home.nicknameField.x = (Home.scroll.width / 2) - (Home.passwordField.width / 2) - Home.passwordField.inputOptions.padding;
	Home.nicknameField.y = Home.passwordField.y + Home.passwordField.height + 15;
	Home.scroll.addChild(Home.nicknameField);

	let loginButtonCb = function(ctx) {
		
		let username = Home.userField.value;
		let email = Home.emailField.value;
		let password = Home.passwordField.value;
		let species = 'pikachu';
		let nickname = Home.nicknameField.value;
		
		Home.statusText.visible = false;
		
		register(username, email, password, species, nickname, function(resp) {
		
			net.connect(resp.id, resp.token);
			Home.scroll.hideTween.onComplete.add(function() {
				//Game.player = new Player();

				// Hack. This should be loaded from the network.
				//Game.player.setPos(20, 20);
				
				game.state.start('Game');
			}, 
			this);
			
			Home.scroll.hideTween.start();
			Home.logo.hideTween.start();
		}, function(resp) {
			Home.statusText.text = resp.error;
			Home.statusText.visible = true;
		}.bind(this));
	}.bind(this);

	let toLoginCb = function(ctx) {
		Home.displayLogin();
	}.bind(this);
	

	Home.loginButton = game.add.button(Home.nicknameField.x + (Home.nicknameField.width / 2) + Home.nicknameField.inputOptions.padding ,
		Home.nicknameField.y + Home.nicknameField.height + 25, 'atlas1', loginButtonCb, this, 'blue-button-primary/0', 'blue-button-primary/0', 'blue-button-primary/1');


	Home.loginButton.anchor.set(0.5, 0);
	Home.loginButton.scale.setTo(Home.nicknameField.width / Home.loginButton.width, 1);
	Home.scroll.addChild(Home.loginButton);

	Home.loginButtonText = game.add.text(Home.loginButton.x, Home.loginButton.y + (Home.loginButton.height / 2) + 3, "Register", stdTextStyle);
	Home.loginButtonText.anchor.set(0.5, 0.5);
	Home.scroll.addChild(Home.loginButtonText);
	
	
	/* Register Button */
	Home.toRegisterButton = game.add.button(Home.nicknameField.x + (Home.nicknameField.width / 2) + Home.nicknameField.inputOptions.padding,
			Home.loginButton.y + Home.loginButton.height + 15, 'atlas1', toLoginCb, this, 'blue-button-primary/0', 'blue-button-primary/0', 'blue-button-primary/1');

	Home.toRegisterButton.anchor.set(0.5, 0);
	Home.toRegisterButton.scale.setTo(Home.nicknameField.width / Home.toRegisterButton.width, 1);
	Home.scroll.addChild(Home.toRegisterButton);

	Home.toRegisterButtonText = game.add.text(Home.toRegisterButton.x, Home.toRegisterButton.y + (Home.toRegisterButton.height / 2) + 3, "Back to Login", stdTextStyle);
	Home.toRegisterButtonText.anchor.set(0.5, 0.5);
	Home.scroll.addChild(Home.toRegisterButtonText);
}

Home.displayLogin = function() {
	if (Home.scroll != undefined) {
		Home.scroll.destroy();
	}
	
	let stdTextStyle = {
		font: '22px pixel',
		fill: 'white'
	};
	
	Home.scroll = Home.makeScroll();
	Home.setFadeTweens(Home.scroll);
	Home.scroll.visible = true;
	Home.scroll.showTween.start();
	
	Home.statusText = game.add.text(0, 50, "Register", {
		font: '22px pixel',
		fill: '#cc0000'
	});
	Home.statusText.anchor.set(0.5, 0.5);
	Home.statusText.x = (Home.scroll.width / 2);
	Home.statusText.y += 25;
	Home.statusText.visible = false;
	Home.scroll.addChild(Home.statusText);

	// (Home.scroll.width / 2) - (Home.inputField.width / 2);
	Home.userField = Home.createUsernameField();
	Home.userField.x = (Home.scroll.width / 2) - (Home.userField.width / 2) - Home.userField.inputOptions.padding;
	Home.userField.y -= 50;
	Home.scroll.addChild(Home.userField);

	Home.passwordField = Home.createPasswordField();
	Home.passwordField.x = (Home.scroll.width / 2) - (Home.passwordField.width / 2) - Home.passwordField.inputOptions.padding;
	Home.passwordField.y = Home.userField.y + Home.userField.height + 25;
	Home.scroll.addChild(Home.passwordField);


	let loginButtonCb = function(ctx) {
		
		let username = Home.userField.value;
		let password = Home.passwordField.value;
		
		Home.statusText.visible = false;
		
		login(username, password, function(resp) {
			
			net.connect(resp.id, resp.token);
			Home.scroll.hideTween.onComplete.add(function() {
				//Game.player = new Player();

				// Hack. This should be loaded from the network.
				//Game.player.setPos(20, 20);
				
				game.state.start('Game');
			}, 
			this);
			
			Home.scroll.hideTween.start();
			Home.logo.hideTween.start();
		}, function(resp) {

			Home.statusText.text = resp.error;
			Home.statusText.visible = true;
			
		}.bind(this));
	}.bind(this);

	let toRegisterCb = function(ctx) {
		Home.displayRegister();
	}.bind(this);
	

	Home.loginButton = game.add.button(Home.passwordField.x + (Home.passwordField.width / 2) + Home.passwordField.inputOptions.padding ,
		Home.passwordField.y + Home.passwordField.height + 25, 'atlas1', loginButtonCb, this, 'blue-button-primary/0', 'blue-button-primary/0', 'blue-button-primary/1');


	Home.loginButton.anchor.set(0.5, 0);
	Home.loginButton.scale.setTo(Home.passwordField.width / Home.loginButton.width, 1);
	Home.scroll.addChild(Home.loginButton);

	Home.loginButtonText = game.add.text(Home.loginButton.x, Home.loginButton.y + (Home.loginButton.height / 2) + 3, "Login", stdTextStyle);
	Home.loginButtonText.anchor.set(0.5, 0.5);
	Home.scroll.addChild(Home.loginButtonText);
	
	
	/* Register Button */
	Home.toRegisterButton = game.add.button(Home.passwordField.x + (Home.passwordField.width / 2) + Home.passwordField.inputOptions.padding,
			Home.loginButton.y + Home.loginButton.height + 15, 'atlas1', toRegisterCb, this, 'blue-button-primary/0', 'blue-button-primary/0', 'blue-button-primary/1');

	Home.toRegisterButton.anchor.set(0.5, 0);
	Home.toRegisterButton.scale.setTo(Home.passwordField.width / Home.toRegisterButton.width, 1);
	Home.scroll.addChild(Home.toRegisterButton);

	Home.toRegisterButtonText = game.add.text(Home.toRegisterButton.x, Home.toRegisterButton.y + (Home.toRegisterButton.height / 2) + 3, "Register", stdTextStyle);
	Home.toRegisterButtonText.anchor.set(0.5, 0.5);
	Home.scroll.addChild(Home.toRegisterButtonText);
}
