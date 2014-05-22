package org.appland.settlers.model;

import java.util.ArrayList;
import java.util.List;

import org.appland.settlers.policy.InitialState;

public class Game {

	List<Player> players;
	
	public Game(int nrPlayers) throws InvalidNumberOfPlayersException {
		if (nrPlayers < 2 || nrPlayers > InitialState.MAX_PLAYERS) {
			throw new InvalidNumberOfPlayersException(nrPlayers);
		}
		
		players = new ArrayList<>();
		
		/* Create initial players */
		int i;
		for (i = 0; i < nrPlayers; i++) {
			players.add(new Player());
		}
	}
	
	public List<Player> getPlayers() {
		return players;
	}
}
