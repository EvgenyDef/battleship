package by.cats.exception;

public class LobbyIsAlreadyOccupied extends RuntimeException {
    public LobbyIsAlreadyOccupied(String message) {
        super(message);
    }
}
