function renderTeamManager() {
	let panel = new SlickUI.Element.Panel(Game.map.widthInPixels/4, Game.map.heightInPixels/15, Game.map.widthInPixels/2, Game.map.heightInPixels/2.5);
	Game.slickUI.add(panel);
	let header = new SlickUI.Element.Text(10 , 10, "Team Manager ");
	header.anchor.setTo(0.5);
	panel.add(header);
 }