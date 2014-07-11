package org.appland.settlers.model;

public class InvalidEndPointException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = -4726130104351916543L;
    private Point point;

    public InvalidEndPointException() {
    }
    
    InvalidEndPointException(Point start) {
        super("Invalid endpoint: " + start);
    }
}
