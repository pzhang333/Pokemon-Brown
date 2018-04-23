
const MESSAGE_TYPE = {
	CONNECT: 0,
	INITIALIZE_PACKET: 1,
	GAME_PACKET: 2,
	TELEPORT_PACKET: 3
};

function waitForSocketConnection(socket, callback) {
    setTimeout(
        function () {
            if (socket.readyState === 1) {
                console.log("Connection is made")
                if(callback != null){
                    callback(socket);
                }
                return;

            } else {
                console.log("wait for connection...")
                waitForSocketConnection(socket, callback);
            }

        }, 5); // wait 5 milisecond for the connection...
}


class Net {

	constructor() {

		this.cfg = {
			url: 'ws://10.38.49.136:4567/game',
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
	
	packet(type, payload) {
		payload.id = net.id;
		
		return JSON.stringify({
			type: type,
			payload: payload
		});
	}

	connect(id, token) {
		
		this.id = id;
		this.token = token;
		
		this.socket = new WebSocket(this.cfg.url);
		this.socket.onmessage = this.handleMsg.bind(this);
		this.socket.onerror = this.handleErr.bind(this);
		
		console.log('Auth(' + this.id + ', ' + this.token + ')');
		
		waitForSocketConnection(this.socket, function(socket) {
			socket.send(net.packet(MESSAGE_TYPE.CONNECT, {
				token: token
			}));
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
		Game.player.setPos(loc.column, loc.row);
		
		net.chunkId = loc.chunkId;
	}
	
	sendTeleportPacket(loc) {
		this.sendPacket(MESSAGE_TYPE.TELEPORT_PACKET, {
			row: loc.y,
			col: loc.x,
			chunk: loc.chunk
		});
	}
	
	gamePacketHandler(msg) {
		console.log('Got game packet');
		
		//msg = generateFakeGamePacket();
		
		if (game.state.current != "Game") {
			return;
		}
		
		let playerUpdates = msg.payload.users;
		for(let i = 0; i < playerUpdates.length; i++) {
			let update = playerUpdates[i];
			
			let loc = playerUpdates[i].location;
			let id = Game.players[update.id];
			
			console.log(player);
			if (player != undefined) {
				player.prepareMovement({
					x: loc.column,
					y: loc.rows
				}, true);
			}
		}
	}

	handleMsg(event) {

		console.log('test!');
		console.log(event);
		
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
