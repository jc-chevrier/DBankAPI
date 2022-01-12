package fr.ul.miage.chevrier.dbank_api.validator;

import fr.ul.miage.chevrier.dbank_api.dto.OperationInput;
import fr.ul.miage.chevrier.dbank_api.exception.LayerConstraintViolationException;
import org.springframework.stereotype.Service;
import javax.validation.ConstraintViolation;
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
            throw new LayerConstraintViolationException(violations);
        }
    }
}