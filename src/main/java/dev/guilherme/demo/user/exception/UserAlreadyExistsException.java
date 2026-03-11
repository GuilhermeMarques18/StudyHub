package dev.guilherme.demo.user.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String email) {
        super("Usuário já existe: " + email);
    }
}
