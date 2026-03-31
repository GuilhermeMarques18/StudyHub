package dev.guilherme.demo.group.exception;

public class InvalidGroupActionException extends RuntimeException {
    public InvalidGroupActionException(String message) {
        super(message);
    }
}
