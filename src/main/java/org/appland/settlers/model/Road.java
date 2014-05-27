package org.appland.settlers.model;

public class Road {

	public Flag start;
	public Flag end;

	private Road(Flag start, Flag end) {
		this.start = start;
		this.end = end;
	}
	
	public static Road createRoad(Flag start, Flag end) {
		return new Road(start, end);
	}

        @Override
	public String toString() {
		return "Road " + start + " to " + end + " ";
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

    public Flag[] getFlags() {
        return new Flag[] {start, end};
    }
}
