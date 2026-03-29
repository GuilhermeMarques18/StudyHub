package dev.guilherme.demo.friend.exception;

public class FriendshipNotFoundException extends RuntimeException {
    public FriendshipNotFoundException(Long id) {
        super("Amizade ou pedido com id: " + id +" não encotrado");
    }
}
