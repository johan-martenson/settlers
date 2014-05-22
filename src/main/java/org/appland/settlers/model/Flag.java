package org.appland.settlers.model;

import java.util.ArrayList;
import java.util.List;

public class Flag {

	private Point position;
	private List<Cargo> stackedCargo;

	private Flag(Point p) {
		this.position = p;
		stackedCargo = new ArrayList<>();
	}
	
	public static Flag createFlag(Point p) {
		Flag f = new Flag(p);
		
		return f;
	}
	
	public List<Cargo> getStackedCargo() {
		return stackedCargo;
	}

	public void putCargo(Cargo c) {
		stackedCargo.add(c);
	}

	public Point getPosition() {
		return position;
	}

	public void setPosition(Point p) {
		this.position = p;
	}

	public Cargo retrieveNextCargo() {
		int size = stackedCargo.size();
		Cargo c = stackedCargo.get(size - 1);
		stackedCargo.remove(size - 1);
		
		return c;
	}
	
        @Override
	public String toString() {
		if (stackedCargo.isEmpty()) {
			return "Flag at " + position;
		} else {
			String s = "Flag at " + position + " (stacked cargo:";
			
			for (Cargo c : stackedCargo) {
				s += " " + c.getMaterial().name();
			}
			
			s += ")";
			
			return s;
		}
	}
}
