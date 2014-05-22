package org.appland.settlers.model;

public class Road {

	public Point start;
	public Point end;

	private Road(Point start, Point end) {
		this.start = start;
		this.end = end;
	}
	
	public static Road createRoad(Point start, Point end) {
		return new Road(start, end);
	}

	public String toString() {
		return "Road " + start.x + ", " + start.y + " to " + end.x + ", " + end.y + " ";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((start == null) ? 0 : start.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Road other = (Road) obj;
		if (end == null) {
			if (other.end != null)
				return false;
		} else if (!end.equals(other.end))
			return false;
		if (start == null) {
			if (other.start != null)
				return false;
		} else if (!start.equals(other.start))
			return false;
		return true;
	}
	

}
