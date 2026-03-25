package be.hers.pi.comprendre_et_parler.exceptions;

public class DuplicatePrimaryKeyException extends RuntimeException {
    public DuplicatePrimaryKeyException(String message) {
        super(message);
    }
}
