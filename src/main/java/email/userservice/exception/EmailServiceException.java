package email.userservice.exception;

public class EmailServiceException extends RuntimeException {
    public EmailServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
