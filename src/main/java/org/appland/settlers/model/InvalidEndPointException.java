package org.appland.settlers.model;

public class InvalidEndPointException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = -4726130104351916543L;

    public InvalidEndPointException() {
    }

    InvalidEndPointException(Point start) {
        super("Invalid endpoint: " + start);
    }

    InvalidEndPointException(String msg) {
        super(msg);
    }
}
