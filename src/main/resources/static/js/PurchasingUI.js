function renderItemPurchase(itemId) {
	let quantity = 0;
	let panelMessage = new SlickUI.Element.Panel(Game.map.widthInPixels/4, Game.map.heightInPixels/6, Game.map.widthInPixels/2, Game.map.heightInPixels/10);
	Game.slickUI.add(panelMessage);
	let header = new SlickUI.Element.Text(10 , 10, "Purchase item: ");
	let okButton = new SlickUI.Element.Button(panelMessage.width/2 - Game.map.widthInPixels/(9.5*2), Game.map.heightInPixels/23, Game.map.widthInPixels/9.5, Game.map.heightInPixels/25);

	let xOut = game.add.sprite(0, 0, 'x_icon');
	xOut.inputEnabled = true;
	xOut.events.onInputUp.add(function () {
		panelMessage.destroy();
	});

	let upArrowSmall = game.add.sprite(0, 0, 'up_arrow');
    upArrowSmall.scale.setTo(0.5);
 	let downArrowSmall = game.add.sprite(0, 0, 'down_arrow');
    downArrowSmall.scale.setTo(0.5);
	let coinUp = new SlickUI.Element.DisplayObject(0, 0, upArrowSmall);
    let coinDown = new SlickUI.Element.DisplayObject(0, 0, downArrowSmall);
    let coinText = new SlickUI.Element.Text(0, panelMessage.height*0.55, "x" + quantity)
    panelMessage.add(coinText);    
    panelMessage.add(coinUp);
    panelMessage.add(coinDown);

    coinText.x = 50 + 3 + 50
    coinUp.x = coinText.x + coinText.text.textWidth + 17
    coinDown.x = coinUp._x
    coinUp.y = 40 + (50 / 2) - coinUp.sprite.height - 3
    coinDown.y = coinUp._y + coinUp.sprite.height + 6

    upArrowSmall.inputEnabled = true;
    downArrowSmall.inputEnabled = true;

    upArrowSmall.events.onInputUp.add(function () {
      quantity++;
      if (quantity*mapGet(itemIdToMap, itemId, 1) > Game.player.currency) {
      	quantity = quantity - 1;
      }
      quantity = Math.max(0, quantity)
      coinText.value = "x" + quantity
    });

    downArrowSmall.events.onInputUp.add(function () {
      quantity=quantity-1;
      if (quantity*mapGet(itemIdToMap, itemId, 1) > Game.player.currency) {
      	quantity = quantity - 1;
      }
      quantity = Math.max(0, quantity)
      coinText.value = "x" + quantity
    });

	panelMessage.add(header);
	panelMessage.add(new SlickUI.Element.DisplayObject(panelMessage.width/1.1, panelMessage.height/25, xOut));

	panelMessage.add(header);
	panelMessage.add(okButton);

	okButton.add(new SlickUI.Element.Text(0, 0, "Ok")).center();
	okButton.events.onInputUp.add(function () {
		if (Game.player.currency - quantity*mapGet(itemIdToMap, itemId, 0) > 0) {
			panelMessage.destroy();
		// send purchase
		net.purchaseItem(Game.player.id, itemId, quantity);
	} else {

	}
});
}
