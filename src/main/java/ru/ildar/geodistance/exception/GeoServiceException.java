package ru.ildar.geodistance.exception;

public class GeoServiceException extends RuntimeException {
    public GeoServiceException() {
        super();
    }

    public GeoServiceException(String message) {
        super(message);
    }

    public GeoServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
