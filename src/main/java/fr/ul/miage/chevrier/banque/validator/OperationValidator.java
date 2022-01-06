package fr.ul.miage.chevrier.banque.validator;

import fr.ul.miage.chevrier.banque.dto.OperationInput;
import org.springframework.stereotype.Service;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

/**
 * Validateur pour les opérations sur
 * les comptes bancaires des clients.
 */
@Service
public class OperationValidator {
    private Validator validator;

    OperationValidator(Validator validator) {
        this.validator = validator;
    }

    public void validate(OperationInput input) {
        Set<ConstraintViolation<OperationInput>> violations = validator.validate(input);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}