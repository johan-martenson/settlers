package org.appland.settlers.controller;

import org.appland.settlers.model.Game;
import org.appland.settlers.model.InvalidNumberOfPlayersException;

public class Controller {
	public Game createInitialGame(int nrPlayers) 
			throws InvalidNumberOfPlayersException {
		return new Game(nrPlayers);
	}
}
