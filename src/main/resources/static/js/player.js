class Player {
	
	/**
	 * Construct a new playerType.
	 */
	constructor(playerType) {

		/* Player type */
		if (playerType == undefined) {
			playerType = "lucas";
		}
		this.playerType = "lucas";

		/* Various variables */
		this.fps = 6;
		this.speed = 64;

		//this.initSprite();
	}

	/**
	 * Initialize the sprite
	 */
	initSprite() {

		/* Player animations */
		this.animations = {
			'idle': getAnimArr(this.playerType, range(0, 1)),
			'idle-forward': getAnimArr(this.playerType, range(0, 1)),
			'idle-backward': getAnimArr(this.playerType, range(4, 5)),
			'idle-left': getAnimArr(this.playerType, range(8, 9)),
			'idle-right': getAnimArr(this.playerType, range(12, 13)),
			'walk-forward': getAnimArr(this.playerType, range(1, 4, 0)),
			'walk-backward': getAnimArr(this.playerType, range(5, 8, 4)),
			'walk-left': getAnimArr(this.playerType, range(9, 12, 8)),
			'walk-right': getAnimArr(this.playerType, range(13, 16, 12))
		};

		this.directions = ['left', 'up', 'down', 'right'];

		this.facing = 'down';

		if (this.x == undefined) {
			this.x = -1000;
		}

		if (this.y == undefined) {
			this.y = -1000;
		}

		/* Delete the old sprite */
		if (this.sprite != undefined) {
			this.sprite.destroy();
		}
		
		/* Load the sprite */
		this.sprite = game.add.sprite(this.x * 16, this.y * 16, 'atlas1', this.animations['idle']);

		this.sprite.anchor.set(.25, .5);
		this.sprite.visible = false;

		/* Add all animations to the sprie. */
		for(name in this.animations) {
			this.sprite.animations.add(name, this.animations[name]);
		}

		/* Start the `idle` animation */
		this.idle();
	}

	idle(cancel) {
		if (cancel == undefined) {
			cancel = false;
		}

		this.playAnim(this.getAnim('idle', this.facing), {
			cancel: cancel,
			loop: true
		});
	}

	setCameraFocus(camera) {
		camera.follow(this.sprite, Phaser.Camera.FOLLOW_LOCKON, 0.1, 0.1);
	}

	getAnim(action, dir) {

		if (!this.directions.includes(dir)) {
			return undefined;
		}

		if (dir == 'down') {
			dir = 'forward';
		} else if (dir == 'up') {
			dir = 'backward';
		}

		//console.log(action + '-' + dir);
		return action + '-' + dir;
	}

	/**
	 * Set the visible state.
	 */ 
	setVisible(state) {
		if (state == undefined) {
			state = true;
		}

		this.sprite.visible = state;
	}

	/**
	 * Set the (x, y) coordinates.
	 */
	setPos(x, y) {
		this.setX(x);
		this.setY(y);
	}

	/**
	 * Set the Y coordinate.
	 */
	setY(y) {
		this.pendingY = undefined;
		this.y = y;
		
		if (this.sprite != undefined) {
			this.sprite.y = 16 * y;
		}
	}

	/**
	 * Set the X coordinate.
	 */
	setX(x) {
		this.pendingX = undefined;
		this.x = x;
		
		if (this.sprite != undefined) {
			this.sprite.x = 16 * x;
		}
	}

	/**
	 * Idle when the current animation completes.
	 */
	idleOnComplete() {
		this.sprite.animations.currentAnim.onComplete.addOnce(function() {
			this.idle();
		}, this);
	}

	/**
	 * Player an animation.
	 * Opts:
	 *		Cancel	- Cancel the current animation if one is playing.
	 *		fps		- Frames per second
	 *		loop	- Should the animation loop?
	 */
	playAnim(animName, opts) {

		if (this.sprite == undefined) {
			this.initSprite();
		}
		
		if (opts == undefined) {
			opts = {};
		}

		let cancel = mapGet(opts, 'cancel', false);
		let fps = mapGet(opts, 'fps', this.fps);
		let loop = mapGet(opts, 'loop', false);

		/* If the cancel option is set, stop the current animation. */
		if (cancel) {
			this.sprite.animations.stop();
			this.sprite.animations.play(animName, fps, loop);
			this.idleOnComplete();
			return;
		}

		/* Get the current animation. */
		let currentAnim = this.sprite.animations.currentAnim;

		/* The current animation is already playing */
		if(currentAnim != undefined && currentAnim.name == animName) {
			return;
		}

		/* If an animation is already playing, wait for the animation to finish.
		 * TODO: Possibly create some kind of FIFO queue?
		 */
		if(currentAnim.isPlaying && !currentAnim.loop) {
			currentAnim.onComplete.addOnce(function () {
				this.playAnim(animName, opts);
				this.idleOnComplete();
			}, this);
		} else {

			/* No animation is currently playing, start it. */
			this.sprite.animations.stop();
			this.sprite.animations.play(animName, fps, loop);
			this.idleOnComplete();
		}

	}

	/**
	 * Step the player in a direction.
	 * dir 		- 'left', 'right', 'up', or 'down'.
	 */
	step(dir) {
		if (debounce(this, 'lastStep', 50)) {
			return;
		}

		if (this.tweenRunning()) {
			return;
		}
		// Can make animation choppy. Fix only if truly needed
		//this.tweenStop();

		if (!this.directions.includes(dir)) {
			return;
		}

		let x = this.x;
		let y = this.y;

		/* The first step, simply changes the direction the person is facing */
		if (this.facing != dir) {
			this.facing = dir;
			this.idle(true);

			/* Have an extended delay so that single presses of arrow keys
			 * simply change the direction.
			 */
			//this.lastStep = performance.now() + 100;
			//return;
		}

		
		/* Map direction to a new (x,y) */
		if (dir == 'left') {
			x -= 1;
		} else if (dir == 'right') {
			x += 1;
		} else if (dir =='up') {
			y -= 1;
		} else if (dir == 'down') {
			y += 1;
		}

		
		
		//console.log(Math.trunc(y / 16) + ' : ' + Math.trunc(x / 16))
		if (getFromMatrix(Game.collisionMatrix, x, y, 0) != -1) {
			this.idle(true);
			return;
		}


		net.sendPacket(MESSAGE_TYPE.PLAYER_REQUEST_PATH, {
			path: [{
				row: y,
				col: x
			}]
		});
		
		//console.log(dir);

		/* Tween duration */
		let duration = .2;

		/* Create a new tween */
		let tween = game.add.tween(this.sprite);
		this.pendingX = x;
		this.pendingY = y;
		tween.to({
			x: 16 * x,
			y: 16 * y
		}, Phaser.Timer.SECOND * duration);

		/* Setup the tween update callback */
		tween.onUpdateCallback(function() {
			if (debounce(this, 'lastMovementUpdate', Phaser.Timer.SECOND * (duration / 4))) {
				return;
			}
			this.playAnim(this.getAnim('walk', dir));
		}, this);

		/* Return to idle once the tween is complete. */
		tween.onComplete.add(function () {
			/* Update the (x, y) */
			this.setPos(x, y);
			this.finishMovement(x, y);
		}, this);

		/* Play the tween */
		this.tween = tween;
		tween.start();
	}

	prepareMovement(end, force) {

		if (force == undefined) {
			force = false;
		}

		if (!force && debounce(this, 'lastMoveCommand', 250)) {
			return;
		}

		if (this.path != undefined && this.path.length > 0) {
			let curEnd = this.path[this.path.length - 1];

			if (curEnd.x == end.x && curEnd.y == end.y) {
				return;
			}
		}

		let x = this.x;
		if (this.pendingX != undefined) {
			x = this.pendingX;
		}
		
		let y = this.y;
		if (this.pendingY != undefined) {
			y = this.pendingY;
		}
		
		//Game.easystar.findPath(this.x, this.y, end.x, end.y,
		//	this.traversePath.bind(this));
		
		Game.easystar.findPath(x, y, end.x, end.y, function(path) {
			
			if (path == null) {
				return;
			}

			if (path.length == 0) {
				return;
			}
			
			if (this == Game.player) {
				
				let xyPath = [];
				
				for(let i = 0; i < path.length; i++) {
					let step = path[i];
					xyPath.push({
						row: step.y,
						col: step.x
					});
				}
				
				net.sendPacket(MESSAGE_TYPE.PLAYER_REQUEST_PATH, {
					path: xyPath
				});
			}
			
			this.traversePath(path);
		}.bind(this));
		
		Game.easystar.calculate();
	}

	showTeleport(x, y, chunk) {
		
		Game.camera.fade('#000000', 500);
		Game.camera.onFadeComplete.add(function() {

			if (chunk == undefined) {
				chunk = this.chunk;
			}
			this.setPos(x, y);

			// Temporary hack...
			/*if (chunk != Game.chunkId) {
				net.chunkId = chunk;
			}*/

			Game.time.events.add(Phaser.Timer.SECOND * .75, function() {
				Game.camera.resetFX();
			}, this);
		}, this);

	}

	finishMovement(x, y) {

		// Only teleport
		/*let door = Game.doors.getFirst(x, y);
		if (door != null) {

			if (Game.player == this) {
			//	this.teleport(door.x, door.y, door.chunk);
			} else {
				//alert('Other player teleported');
				
				this.sprite.destroy();
				Game.players[this.id] = undefined;
				return;
			}
		}*/

		this.idle();
	}
	
	del() {
		this.sprite.destroy();
		Game.players[this.id] = undefined;
		return;
	}

	orientBy(oldX, oldY, newX, newY) {
		if (newX > oldX) { // right
			this.facing = 'right';
		} else if (newX < oldX) { // left
			this.facing = 'left';
		} else if (newY > oldY) { // down
			this.facing = 'down';
		} else if (newY < oldY) { // up
			this.facing = 'up';
		}
	}

	tweenStop(cb) {

		if (this.tweenRunning()) {
			//this.tween.stop(false);
			this.tween.onComplete.add(function() {
				this.tween.stop(false);
				this.setPos(this.pendingX, this.pendingY);
				this.tween.onComplete.removeAll();
				cb();
			}, this, 100);
		} else {
			cb();
		}
	}

	tweenRunning() {
		return (this.tween != undefined && this.tween.isRunning);
	}

	traversePath(path) {
		
		if (path == null) {
			return;
		}

		if (path.length == 0) {
			return;
		}

		this.tweenStop(function() {

		this.path = path;
		let x = path[0].x;
		let y = path[0].y;

		this.orientBy(this.x, this.y, x, y);

		this.playAnim(this.getAnim('walk', this.facing), {
			loop: true
		});

		let duration = .2;

		let tween = game.add.tween(this.sprite);
		this.pendingX = x;
		this.pendingY = y;
		tween.to({
			x: 16 * x,
			y: 16 * y
		}, Phaser.Timer.SECOND * duration);

		/* Setup the tween update callback */
		tween.onUpdateCallback(function() {

			if (debounce(this, 'lastMovementUpdate', Phaser.Timer.SECOND * (duration / 4))) {
				return;
			}

			this.orientBy(this.x, this.y, x, y);

			this.playAnim(this.getAnim('walk', this.facing));
		}, this);

		/* Return to idle once the tween is complete. */
		tween.onComplete.add(function () {
			/* Update the (x, y) */
			this.setPos(x, y);

			/* Pop the path */
			path.shift();

			/* Continue Traversal or Finish */
			if (path.length != 0) {
				this.traversePath(path);
			} else {
				this.finishMovement(x, y);
			}
		}, this);

		/* Play the tween */
		this.tween = tween;
		tween.start();
		}.bind(this));
	}

}
