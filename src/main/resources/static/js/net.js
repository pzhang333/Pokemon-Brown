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
    CLIENT_BATTLE_UPDATE: 9
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

		//this.host = '10.38.49.136';
		this.host = 'localhost';
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
		this.handlers[MESSAGE_TYPE.WILD_ENCOUNTER] = this.wildEncounterPacketHandler;
		this.handlers[MESSAGE_TYPE.TELEPORT_PACKET] = this.teleportHandler;
		//this.handlers[MESSAGE_TYPE.PATH_REQUEST_RESPONSE] = this.pathApprovalHandler;

	}
	
	sendPacket(type, payload) {
		if (this.socket.readyState == this.socket.CLOSED) {
			throw "Socket closed...";
		}
		
		waitForSocketConnection(this.socket, function(socket) {
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
	
	teleportHandler(msg) {

		return;
		console.log(msg);
		console.log(msg.payload);
		let loc = msg.payload.location;
		
		Game.player.showTeleport(loc.col, loc.row, loc.chunk_file);
	}
	
	
	initPacketHandler(msg) {
		
		console.log(msg);

		Cookies.set("id", net.id);
		Cookies.set("token", net.token);
		
		Game.player.id = this.id;
		
		let loc = msg.payload.location;
		
		Game.player.showTeleport(loc.col, loc.row, loc.chunk_file, function() {
			net.chunkId = loc.chunk_file;
		});
		
	}
	
	gamePacketHandler(msg) {
	//	console.log('Got game packet');
		
		//msg = generateFakeGamePacket();
		
		if (game.state.current != "Game") {
			return;
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
				continue;
			}
			
			//console.log(Game.players[id])
			if (Game.players[id] == undefined) {
				let player = new Player();
				
				player.setPos(loc.col, loc.row);
				player.initSprite();
				player.setVisible(true);
				player.id = id;
				
				Game.players[id] = player;

				console.log(Game.players[id]);
			}
			
			let player = Game.players[id];
			
			let dest = update.destination;
			
			if (dest == undefined) {
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

	wildEncounterPacketHandler(msg) {
		
		if (game.state.current != "Game") {
			return;
		}
		
		let payload = msg.payload;
		console.log(msg);
		
		let loc = payload.location;
		
		if (Game.player.tweenRunning()) {
			Game.player.tween.stop(true);
			Game.player.idle();
		}
		Game.player.setPos(loc.col, loc.row);
		
		let pokemon = payload.pokemon;
		
		let battleId = -1;
		Battle.startBattle({
			battleId: battleId
		});
		
		//alert('Encountered wild pokemon with id: ' + pokemon.id);
	}
	
	handleMsg(event) {

//		console.log('test!');
	//	console.log(event);
		
		const data = JSON.parse(event.data);
		
		if (data.type in this.handlers) {
			this.handlers[data.type](data);
		} else {
			console.log('Unknown message type!', data.type);
		}
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
}

var net = new Net();