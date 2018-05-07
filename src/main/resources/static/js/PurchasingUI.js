function renderItemPurchase(itemId) {
	let panelMessage = new SlickUI.Element.Panel(Game.map.widthInPixels/4, Game.map.heightInPixels/6, Game.map.widthInPixels/2, Game.map.heightInPixels/10);
	Game.slickUI.add(panelMessage);
	let header = new SlickUI.Element.Text(10 , 10, "Purchase item: ");
	let okButton = new SlickUI.Element.Button(panelMessage.width/2 - Game.map.widthInPixels/(9.5*2), Game.map.heightInPixels/23, Game.map.widthInPixels/9.5, Game.map.heightInPixels/25);

	let xOut = game.add.sprite(0, 0, 'x_icon');
	xOut.inputEnabled = true;
	xOut.events.onInputUp.add(function () {
		panelMessage.destroy();
    });

    let quantityField = game.add.inputField(0, 0, {
 			width: 40,
 			padding: 5,
 			fill: '#000000',
 			stroke: '#000000',
 			backgroundColor: '#ffffff',
 			borderWidth: 2,
 			borderColor: '#919191',
 			borderRadius: 3,	
 			textAlign: 'center',
 			font: '18px Arial',
 			placeHolder: "0",
 			placeHolderColor: '#000000',
 			cursorColor: '#000000'
 		});

 	panelMessage.add(new SlickUI.Element.DisplayObject(75, panelMessage.height*0.5, quantityField));

   	panelMessage.add(header);
	panelMessage.add(new SlickUI.Element.DisplayObject(panelMessage.width/1.1, panelMessage.height/25, xOut));

	panelMessage.add(header);
	panelMessage.add(okButton);

	okButton.add(new SlickUI.Element.Text(0, 0, "Ok")).center();
	okButton.events.onInputUp.add(function () {
		panelMessage.destroy();
		// send purchase
		if (! (quantityField.value == "")) {
			purchaseItem(Game.player.id, itemId, quantityField.value);
		}
	});
}