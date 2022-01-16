package fr.ul.miage.chevrier.dbank_api.validator;

import fr.ul.miage.chevrier.dbank_api.dto.OperationInput;
import fr.ul.miage.chevrier.dbank_api.exception.LayerConstraintViolationException;
import org.springframework.stereotype.Service;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

/**
 * Validateur pour les opérations
 * bancaires.
 */
@Service
public class OperationValidator {
    //Validateur.
    private Validator validator;

    OperationValidator(Validator validator) {
        this.validator = validator;
    }

    /**
     * Valider une opération et levée d'une exception
     * si une contrainte n'est pas respectée.
     *
     * @param operationInput        Saisies de l'opération bancaire à valider.
     */
    public void validate(OperationInput operationInput) {
        Set<ConstraintViolation<OperationInput>> violations = validator.validate(operationInput);
        if (!violations.isEmpty()) {
            throw new LayerConstraintViolationException(violations);
        }
    }
}