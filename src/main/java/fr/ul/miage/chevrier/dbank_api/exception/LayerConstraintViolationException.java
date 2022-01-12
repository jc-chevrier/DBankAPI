package fr.ul.miage.chevrier.dbank_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Surcouche de l'exception des contraintes
 * validations.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class LayerConstraintViolationException extends ConstraintViolationException {
    public LayerConstraintViolationException(String message, Set<? extends ConstraintViolation<?>> constraintViolations) {
        super(message, constraintViolations);
    }

    public LayerConstraintViolationException(Set<? extends ConstraintViolation<?>> constraintViolations) {
        this(constraintViolations != null ? toString(constraintViolations) : null, constraintViolations);
    }

    private static String toString(Set<? extends ConstraintViolation<?>> constraintViolations) {
        return (String)constraintViolations.stream().map((cv) -> {
            return cv == null ? "null" : cv.getPropertyPath() + ": " + cv.getMessage();
        }).collect(Collectors.joining(", "));
    }
}
