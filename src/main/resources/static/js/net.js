
const MESSAGE_TYPE = {
	CONNECT: 0,
	INITIALIZE_PACKET: 1,
	GAME_PACKET: 2,
	PLAYER_REQUEST_PATH: 3,
	TELEPORT_PACKET: 4
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

		this.host = 'localhost';
		this.port = 4567;
		
		this.cfg = {
			url: 'ws://' + this.host + ':' + this.port.toString() + '/game',
		};
		
		this.chunkBaseURL = "/assets/maps/chunk_";

		// TODO: maybe use somekind of queue?
		
		// Temporary hack...
		this.chunkId = 1;

		this.handlers = {}
		this.handlers[MESSAGE_TYPE.CONNECT] = this.connectHandler
		this.handlers[MESSAGE_TYPE.INITIALIZE_PACKET] = this.initPacketHandler;
		this.handlers[MESSAGE_TYPE.GAME_PACKET] = this.gamePacketHandler;
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
		this.socket.onerror = this.handleErr.bind(this);
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
	
	initPacketHandler(msg) {
		Game.player.id = this.id;
		
		let loc = msg.payload.location;
		Game.player.setPos(loc.col, loc.row);
		
		Game.chuknId = loc.chunkId;
		
		net.chunkId = loc.chunkId;
	}
	
	sendTeleport(loc) {
		this.sendPacket(MESSAGE_TYPE.TELEPORT_PACKET, {
			row: loc.y,
			col: loc.x,
			chunk: loc.chunk
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
			
			//handled.push(update.id);
			
			//let loc = update.location;
			let loc = update.location;
			let id = update.id;

			
		//	console.log(id + " : " + net.id)
			if (id == net.id) {
				console.log('skip: ' + id);
				continue;
			}
			

			console.log('test');
			if (Game.players[id] == undefined) {
				let player = new Player();
				
				player.setPos(loc.col, loc.row);
				player.initSprite();
				player.setVisible(true);
				player.id = id;
				
				Game.players[id] = player;
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
		    		Game.players[id].del();
		    	}
		    }
		}
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
	
	handleErr(err) {
		alert('Connection Error! Please refresh.');
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
