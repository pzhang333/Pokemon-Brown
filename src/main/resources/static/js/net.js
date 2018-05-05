const MESSAGE_TYPE = {
	CONNECT: 0,
	INITIALIZE_PACKET: 1,
	GAME_PACKET: 2,
	PLAYER_REQUEST_PATH: 3,
	WILD_ENCOUNTER: 4,
	TRADE: 5,
	START_BATTLE: 6,
    END_BATTLE: 7,
    BATTLE_TURN_UPDATE: 8,
    CLIENT_BATTLE_UPDATE: 9,
    CHAT: 10,
    SERVER_MESSAGE: 11,
    CHALLENGE_REQUEST: 12,
    CHALLENGE_RESPONSE: 13
};

const BATTLE_ACTION = {
	RUN: 0,
	SWITCH: 1,
	USE_ITEM: 2,
	FIGHT: 3
};

const OP_CODES = {
	PLAYER_ENTERED_CHUNK: 0,
	PLAYER_LEFT_CHUNK: 1,
	PLAYER_ENTERED_BATTTLE: 2,
	PLAYER_LEFT_BATTLE: 3,
	CHAT_RECEIVED: 4
};

function waitForSocketConnection(socket, callback) {
    setTimeout(
        function () {
            if (socket.readyState === 1) {
                //console.log("Connection is made")
                if(callback != null){
                    callback(socket);
                }
                return;

            } else {
                //console.log("wait for connection...")
                waitForSocketConnection(socket, callback);
            }

        }, 5); // wait 5 milisecond for the connection...
}


class Net {

	constructor() {


		//this.host = 'localhost';
		this.host = '10.38.37.243';
    	// this.host = '10.38.32.136';
    	this.port = 4567;

		this.cfg = {
			url: 'ws://' + this.host + ':' + this.port.toString() + '/game',
		};

		this.chunkBaseURL = "/assets/maps/";

		// TODO: maybe use somekind of queue?

		this.handlers = {}
		this.handlers[MESSAGE_TYPE.CONNECT] = this.connectHandler
		this.handlers[MESSAGE_TYPE.INITIALIZE_PACKET] = this.initPacketHandler;
		this.handlers[MESSAGE_TYPE.GAME_PACKET] = this.gamePacketHandler;
		//this.handlers[MESSAGE_TYPE.WILD_ENCOUNTER] = this.wildEncounterPacketHandler;
		this.handlers[MESSAGE_TYPE.START_BATTLE] = this.startBattleHandler;
		this.handlers[MESSAGE_TYPE.END_BATTLE] = this.endBattleHandler;
		this.handlers[MESSAGE_TYPE.BATTLE_TURN_UPDATE] = this.battleUpdateHandler;		
		this.handlers[MESSAGE_TYPE.SERVER_MESSAGE] = function(event) {
			let cleanMsg = event.payload.message.replace(/[\u00A0-\u9999<>\&]/gim, function(i) {
				return '&#' + i.charCodeAt(0) + ';';
			});
			
			this.displayChatMsg(cleanMsg, 'color: darkred; font-weight: bold;');
		}.bind(this);
		this.handlers[MESSAGE_TYPE.CHALLENGE_RESPONSE] = this.challengeResponseHandler;
		this.handlers[MESSAGE_TYPE.CHALLENGE_REQUEST] = this.requestChallengeHandler;
		
		//this.handlers[MESSAGE_TYPE.PATH_REQUEST_RESPONSE] = this.pathApprovalHandler;

	}

	sendBattlePacket(action, payload) {

		payload.action = action;
		payload.turn_id = -1;

		this.sendPacket(MESSAGE_TYPE.CLIENT_BATTLE_UPDATE, payload);
	}

	sendPacket(type, payload) {
		if (this.socket.readyState == this.socket.CLOSED) {
			throw "Socket closed...";
		}

		waitForSocketConnection(this.socket, function(socket) {
			console.log(net.packet(type, payload));
			socket.send(net.packet(type, payload));
		}.bind(this));
	}

	packet(type, payload) {
		payload.id = net.id;

		return JSON.stringify({
			type: type,
			payload: payload
		});
	}

	connect(id, token) {

		console.log(id);
		net.id = id;
		Game.player.id = id;
		net.token = token;

		this.socket = new WebSocket(this.cfg.url);
		this.socket.onmessage = this.handleMsg.bind(this);

		this.socket.onerror = function() {
			game.state.start('Home');
		};

		this.socket.onclose = function() {
			game.state.start('Home');
		};

		console.log('Auth(' + this.id + ', ' + this.token + ')');

		this.sendPacket(MESSAGE_TYPE.CONNECT, {
			token: token
		});
	}

	getChunk(cb) {

		let id = this.chunkId;

		if (id == undefined) {
			return false;
		}

		$.getJSON(this.chunkBaseURL + id.toString() + ".json", function(data) {
			cb(new Chunk(id, data));
		});

	}

	getCurrentChunkId() {
		// Hack
		return this.chunkId;
	}

	// Pretend this is a login packet... or something idk...
	connectHandler(msg) {
		console.log('Got connect packet', msg);

		//Game.player.id = msg.payload.id;
	}

	async initPacketHandler(msg) {

		if (Battle.inBattle) {
			if (Battle.showing != undefined) {
				await Battle.showing;
			}
			
			Battle.endBattle();
		}
		
		Cookies.set("id", net.id);
		Cookies.set("token", net.token);

		Game.player.id = net.id;

		let loc = msg.payload.location;

		if (Game.ready) {
			Game.player.showTeleport(loc.col, loc.row, loc.chunk_file, function() {
				net.chunkId = loc.chunk_file;
			});
		}
		net.chunkId = loc.chunk_file;

		Game.player.setPos(loc.col, loc.row);

		let leaderboard = msg.payload.leaderboards;
		if (leaderboard != undefined) {
			Game.leaderboard = [];
			for (let i = 0; i < leaderboard.length; i++) {
    			let name = leaderboard[i].username;
    			let elo = leaderboard[i].elo;
				Game.leaderboard.push(new EloUser(name, elo));
			}
		}

		let players = msg.payload.players;
		for(let i = 0; i < players.length; i++) {

			let player = players[i];

			if (player.id == net.id) {
				Game.player.username = player.username;
				Game.player.items = player.items;
				Game.player.pokemon = player.pokemon;
                Game.player.currency = player.currency;
                Game.player.activePokemon = player.active_pokemon;
				Game.player.elo = player.elo;
				continue;
			}

			let newPlayer = new Player();
			newPlayer.id = player.id;
			newPlayer.username = player.username;
			newPlayer.elo = player.elo;
			newPlayer.pokemon = player.pokemon;

			console.log(newPlayer);
			Game.players[player.id] = newPlayer;

		}

	}

	gamePacketHandler(msg) {
		//	console.log('Got game packet');

		//msg = generateFakeGamePacket();

		if (game.state.current != "Game") {
			return;
		}

		// leaderboard
		let leaderboard = msg.payload.leaderboards;
		if (leaderboard != undefined) {
			Game.leaderboard = [];
			for (let i = 0; i < leaderboard.length; i++) {
    			let name = leaderboard[i].username;
    			let elo = leaderboard[i].elo;
				Game.leaderboard.push(new EloUser(name, elo));
			}
		}

		// op codes
		let ops = msg.payload.ops;
		if (ops != undefined) {
			console.log(msg);

			for(let i = 0; i < ops.length; i++) {
				let op = ops[i];

				let code = op.code;
				let id = op.id;

				console.log(op);

				if (code == OP_CODES.PLAYER_ENTERED_CHUNK) {

					console.log('Player!');

					if (op.id == net.id) {
						//Game.player.username = op.username;
						continue;
					}

					let player = new Player();

					//player.initSprite();
					//player.setVisible(true);
					player.id = op.id;
					player.username = op.username;
					player.elo = op.elo;
					player.pokemon = op.pokemon;

					if (Game.players[op.id] != undefined) {
						Game.players[op.id].del();
					}

					Game.players[op.id] = player;

				} else if (code == OP_CODES.CHAT_RECEIVED) {
					
					
					try {
						let user = "";
						if (id == Game.player.id) {
							user = Game.player.username;
						} else {
							user = Game.players[op.id].username;
						}

						let cleanMsg = op.message.replace(/[\u00A0-\u9999<>\&]/gim, function(i) {
							return '&#' + i.charCodeAt(0) + ';';
						});
						
						net.displayChatMsg('<b>' + user + ':</b> ' + cleanMsg);
					} catch(err) {
						console.log(err);
					}
				}	else {
					console.log('Unhandled op code: ' + code);
				}
			}

		}

		let handled = [];

		let playerUpdates = msg.payload.users;
		for(let i = 0; i < playerUpdates.length; i++) {

			let update = playerUpdates[i];

			handled.push(update.id);

			//let loc = update.location;
			let loc = update.location;
			let id = update.id;


		//	console.log(id + " : " + net.id)
			if (id == net.id) {
				//console.log('skip: ' + id);
				Game.player.items = msg.payload.users[i].items;

                Game.player.pokemon = msg.payload.users[i].pokemon;
                Game.player.currency = msg.payload.users[i].currency;
                Game.player.activePokemon = msg.payload.users[i].active_pokemon;

				Game.player.elo = msg.payload.users[i].elo;
				continue;
			}

			if (Game.players[id] == undefined) {
                //console.log(Game.player.pokemon);
				continue;
			}

			if (Game.players[id].sprite == undefined || Game.players[id].sprite.alive == false) {
				let player = Game.players[id];
				player.initSprite();
				player.setVisible(true);
				player.setPos(loc.col, loc.row);
				player.sprite.inputEnabled = true;
    			player.sprite.events.onInputDown.add(playerInteraction, player);
				/*console.log('ayyy');*/
				//continue;
			}

			let player = Game.players[id];
			player.elo = update.elo;
			player.pokemon = update.pokemon;

			let dest = update.destination;

			if (dest == undefined) {

				if (!player.tweenRunning()) {
					player.setPos(loc.col, loc.row);
				}

				continue;
			}

			//console.log(player);
			if (player != undefined) {
				player.prepareMovement({
					x: dest.col,
					y: dest.row
				}, true);
			}
		}

		for (var key in Game.players) {
		    if (Game.players.hasOwnProperty(key)) {

		    	// Hack
		    	let id = parseInt(key);

		    	if (!handled.includes(id)) {
		    		let toDel = Game.players[id];

		    		if (toDel != undefined) {
		    			toDel.del();
		    		}

		    		Game.players[id] = undefined;
		    	}
		    }
		}
	}

	startBattleHandler(msg) {

		if (game.state.current != "Game") {
			return;
		}

		let payload = msg.payload;
		console.log(msg);

		let loc = payload.location;

		if (Game.player.tweenRunning()) {
			Game.player.tween.stop(false);
			Game.player.idle();
			Game.player.setPos(loc.col, loc.row);
		}

		Battle.setup(payload);
		Battle.run();
		//game.state.start('Battle');
		//alert('Encountered wild pokemon with id: ' + pokemon.id);
	}
	
	displayChatMsg(msg, style) {
		if (game.state.current != "Game") {
			return;
		}
		
		let chat = $("#chat");
		
		if (msg.length == 0) {
			return;
		}
		
		let doScroll = false;
		if (chat.scrollTop() + chat.innerHeight() >= chat[0].scrollHeight) {
			doScroll = true;
		}
			
		if (style == undefined) {
			style = "";
		}
		
		if (chatIdx % 2 != 0) {
			chat.append('<li class="chat-msg" style="background-color: lightgray;' + style + '">' + msg + '</li>');
		} else {
			chat.append('<li class="chat-msg" style="background-color: darkgray;' + style + '">' + msg + '</li>');

		}
		
		if (doScroll) {
			chat.scrollTop(chat[0].scrollHeight);
		}
	
		chatIdx++;
	}

	handleMsg(event) {

		// console.log('test!');
		//console.log(event);

		const data = JSON.parse(event.data);

		if (data.type in this.handlers) {
			this.handlers[data.type](data);
		} else {
			console.log('Unknown message type!', data.type);
		}
	}

	endBattleHandler(event) {
		Battle.battleOver(event.payload);
	}

	battleUpdateHandler(event) {
		Battle.handleUpdate(event.payload);
	}

	sendClientPlayerUpdate(networkPlayer, op){
  		// sends a message with an updated player object
  		let messageObject = new PlayerUpdateMessage(networkPlayer, op);
  		socket.send(JSON.parse(messageObject));
	}

	requestMovePath(path) {
  		// sends a message with an updated player object
  		let messageObject = new RequestPathMessage(path);
  		socket.send(JSON.parse(messageObject));
  	}

  	challengeResponseHandler(msg) {
  		let payload = msg.payload;

  		if (payload.reason == "denied") {
  			// expired, canceled, busy
  			renderChallengeUpdate("Challenge denied.");
  		} else if (payload.reason == "canceled") {
  			renderChallengeUpdate("Challenge canceled.");
  		} else if (payload.reason == "busy") {
  			renderChallengeUpdate("Challenge busy.");
  		} else {
  			renderChallengeUpdate("Challenge expired.");
  		}
  	}

  	requestChallengeHandler(msg) {
  		console.log(msg);
  		let payload = msg.payload;

  		let challengerId = payload.from;

  		if (Game.players[challengerId] != undefined) {
  			let player = Game.players[challengerId];
  			renderChallenge(player);
  		}
  	}

  	requestChallenge(id, challengedId) {
  		// sends a requesting a p2p battle
  		let messageObject = new RequestChallengeMessage(id, challengedId);
  		this.sendPacket(MESSAGE_TYPE.CHALLENGE_REQUEST, messageObject.payload);  	
  	}

  	cancelChallenge(id) {
  		// sends a requesting a p2p battle
  		let messageObject = new RequestChallengeMessage(id, -1);
  		console.log(messageObject);
  		this.sendPacket(MESSAGE_TYPE.CHALLENGE_REQUEST, {id: messageObject.payload.id});  	
  	}

  	rejectChallenge(id) {
  		// rejects a p2p battle request
  		let messageObject = new ChallengeResponseMessage(id, false);
  		this.sendPacket(MESSAGE_TYPE.CHALLENGE_RESPONSE, messageObject.payload);  	
  	}

  	acceptChallenge(id) {
  		// accepts a p2p battle request
  		let messageObject = new ChallengeResponseMessage(id, true);
  		this.sendPacket(MESSAGE_TYPE.CHALLENGE_RESPONSE, messageObject.payload);
  	}
}

var net = new Net();
