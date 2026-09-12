package uk.ac.newcastle.enterprisemiddleware.flight;

import javax.validation.ValidationException;
public class UniqueDepartureAndDestinationException extends ValidationException{

    public UniqueDepartureAndDestinationException(String message) {
        super(message);
    }

    public UniqueDepartureAndDestinationException(String message, Throwable cause) {
        super(message, cause);
    }

    public UniqueDepartureAndDestinationException(Throwable cause) {
        super(cause);
    }


}
