package fr.ul.miage.chevrier.dbank_api.validator;

import fr.ul.miage.chevrier.dbank_api.dto.CardInput;
import fr.ul.miage.chevrier.dbank_api.exception.LayerConstraintViolationException;
import org.springframework.stereotype.Service;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

/**
 * Validateur pour les cartes des comptes
 * bancaires.
 */
@Service
public class CardValidator {
    private Validator validator;

    CardValidator(Validator validator) {
        this.validator = validator;
    }

    public void validate(CardInput input) {
        Set<ConstraintViolation<CardInput>> violations = validator.validate(input);
        if (!violations.isEmpty()) {
            throw new LayerConstraintViolationException(violations);
        }
    }
}