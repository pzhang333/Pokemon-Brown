
const MESSAGE_TYPE = {
	CONNECT: 0,
	GAME_PACKET: 1,
	UPDATE_USER: 2
};


class Net {

	constructor() {

		this.cfg = {
			url: 'ws://localhost:4567/socket',
		};
		
		this.chunkBaseURL = "/assets/maps/chunk_";

		// Temporary hack...
		this.chunkId = 1;

		this.events = [];

		this.socket = new WebSocket(this.cfg.url);
		
		this.socket.onmessage = this.handleMsg();
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

	
	handleMsg(msg) {
		console.log('Got msg!');
		console.log(msg);
	}
}

var net = new Net();
