package org.appland.settlers.model;

public class InvalidStateForProduction extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4123948235819619294L;

	public InvalidStateForProduction(Building b) {
		super("Can not produce in building with state " + b.getConstructionState());
	}
}
