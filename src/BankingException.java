/**
 * BankingException.java
 * 
 * A custom exception class for handling banking-related errors.
 * It extends the built-in Exception class so we can throw it
 * whenever something goes wrong in our banking operations.
 */
public class BankingException extends Exception {

    // The error message describing what went wrong
    private String message;

    /**
     * Constructor - takes a message and stores it
     * @param message - the error description
     */
    public BankingException(String message) {
        super(message);
        this.message = message;
    }

    /**
     * Returns the error message
     * @return message
     */
    public String getMessage() {
        return message;
    }
}
