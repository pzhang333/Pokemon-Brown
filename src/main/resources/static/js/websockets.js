// declaring our message types
const MESSAGE_TYPE = {
  CONNECT: 0,
  GAME_PACKET: 1,
  UPDATE_USER: 2
};

let conn;
let myId = -1;

// Setup the WebSocket connection
const setup_player_connection = () => {
	
  conn = new WebSocket("ws://localhost:4567/game");
  conn.onerror = err => {
    console.log('Connection error:', err);
  };

  conn.onmessage = msg => {
    const data = JSON.parse(msg.data);
    switch (data.type) {
      default:
        console.log('Unknown message type!', data.type);
        break;
      case MESSAGE_TYPE.CONNECT:
    	// initial connection message
    	myId = data.payload.id;
    	console.log(data.payload.id);
        break;
      case MESSAGE_TYPE.GAME_PACKET:
    	// received new game packet from the server
      	break;
      case MESSAGE_TYPE.UPDATE_USER:
    	// received a specific user update
    	break;
    }
  };
}

const send_client_update = player => {
  // sends a message with an updated player object
  let messageObject = new PlayerUpdateMessage(player);
  conn.send(JSON.parse(messageObject));
}
