
const MESSAGE_TYPE = {
	CONNECT: 0,
	GAME_PACKET: 1,
	UPDATE_USER: 2
};


class Net {

	constructor() {

		this.cfg = {
			url: 'ws://localhost:4567/game',
		};
		
		this.chunkBaseURL = "/assets/maps/chunk_";

		// TODO: maybe use somekind of queue?
		
		// Temporary hack...
		this.chunkId = 1;

		this.handlers = {}
		this.handlers[MESSAGE_TYPE.CONNECT] = this.connectHandler
		this.handlers[MESSAGE_TYPE.GAME_PACKET] = this.gamePacketHandler
		
		this.socket = new WebSocket(this.cfg.url);
		
		this.socket.onmessage = this.handleMsg.bind(this);
		this.socket.onerror = this.handleErr.bind(this);
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
		console.log('Got connect packet');
		
		Game.player.id = msg.payload.id;
	}
	
	gamePacketHandler(msg) {
		//console.log('Got game packet');
	}
	
	handleMsg(event) {

		const data = JSON.parse(event.data);
		
		if (data.type in this.handlers) {
			this.handlers[data.type](data);
		} else {
			console.log('Unknown message type!', data.type);
		}
	}
	
	handleErr(err) {
		console.log('Connection error:', err);
	}
}

var net = new Net();
