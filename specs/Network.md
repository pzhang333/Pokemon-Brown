Network Overview
======

The server and the clients will be in constant communication via Websockets. The communication will be done via JSON encoded messages.

## Normal Gameplay

Every game tick, the client will recieve a game packet containing all necessary information required to render the User Interface, including:
1. The players position.
2. The position of all other players.
3. The state of the map (e.g. what tiles to display in the background)
4. User interface state (e.g. should the inventory or world map be shown, etc?)
5. Any other necessary world information.

When a user inputs an action, a packet containg the action request along with any other necessary information is sent to the server.
These packets should be sent as soon as the action request is input.
It is up to the server to determine the order of these actions (as well as their validity/legality).
For instance, a client might request to use an item it doesn't posess, or to move through an impassible barrier on the map, it is up to the server not client to prevent this.

### List of OP Codes
1. a
2. b
3. c

## Battles

During battles there will be timing windows during which certain special battle command requests may be sent by the client. Once a battle request is sent, the timing window will be closed. When ever a timing window opens/closes each member of the battle will be sent a battle status packet, detailing the state of the battle (including: health of the Pokemon, PP remaining, etc). This status packet will also trigger attack/defending animations as well as animated health bar changes. The packet will also detail the options each battler currently has (e.g. possible moves, items, flee, etc).

### List of OP Codes
1. a
2. b
3. c

Packet Structure
======

## Normal Gameplay

```js
{
  // Update local entities (other players, etc.)
  local_enty_ops:
  [
    {
      opcode: 0,
      // Opcode specific data...
    },
    
    // Insert player into local player list, ex...
    {
      opcode: 1
      player_name: "Sam Smith",
      player_pokemon:
      [
        ...
      ]
    },
    
    // Update window chunk
    {
      opcode: 2,
      {
        ...
      }
    }
  ],
  movement:
  {
    player:
    {
      id: 0,
      x_pos: 0,
      y_pos: 0,
      animation_state: 0
    },
    others:
    [
      {
        id: 0
        x_pos: 0,
        y_pos: 0,
        animation_state: 0
      }
    ]
  },
  operations: [
    {
      opcode: 0,
      // Opcode specific data...
    },
    
    // Add item to tile
    {
      opcode: 12,
      item_id: 5,
      item_instance_id: 123,
    }
  ]
}
```

## Battle
