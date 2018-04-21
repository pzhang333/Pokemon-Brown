var game = new Phaser.Game(1000, 600,
    (navigator.userAgent.toLowerCase().indexOf('firefox') > -1 ? Phaser.CANVAS : Phaser.AUTO),
    document.getElementById('game'), null, true, false);

// Temp hack:
Game.player = new Player();


game.state.add('Home', Home);
game.state.add('Game', Game);
//game.state.add('Battle', Game);
game.state.start('Home');
