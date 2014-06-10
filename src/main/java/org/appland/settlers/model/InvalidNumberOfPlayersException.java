package org.appland.settlers.model;

public class InvalidNumberOfPlayersException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = -5485225558077856601L;

    public InvalidNumberOfPlayersException(int players) {
        super("Invalid number of players: " + players);
    }
}
