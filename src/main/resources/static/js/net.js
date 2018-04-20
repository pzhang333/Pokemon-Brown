class Chunk {

	constructor(id, data) {
		this.id = id;
		this.data = data;
	}
}

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

}

var net = new Net();
