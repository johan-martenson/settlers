package org.appland.settlers.model;

public class Road {

    public Flag start;
    public Flag end;
    private boolean promisedCourier;
    private Courier courier;

	public Road(Flag start, Flag end) {
		this.start = start;
		this.end = end;
                
                promisedCourier = false;
                courier = null;
	}
	
	public static Road createRoad(Flag start, Flag end) {
		return new Road(start, end);
	}

        @Override
	public String toString() {
            if (courier == null) {
                return "Road " + start + " to " + end + " with no courier";
            } else {
                return "Road " + start + " to " + end + " with courier";
            }
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

    public boolean needsCourier() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void promiseCourier() throws Exception {
        if (promisedCourier) {
            throw new Exception("Road " + this + " already has a courier promised");
        }
        
        promisedCourier = true;
    }

    public Courier getCourier() {
        return courier;
    }

    void setCourier(Courier wr) {
        courier = wr;
    }
}
