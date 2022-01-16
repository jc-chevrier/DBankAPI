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
    //Validateur.
    private Validator validator;

    CardValidator(Validator validator) {
        this.validator = validator;
    }

    /**
     * Valider une opération bancaire et levée
     * d'une exception si une contrainte n'est pas
     * respectée.
     *
     * @param cardInput        Saisies de la carte bancaire à valider.
     */
    public void validate(CardInput cardInput) {
        Set<ConstraintViolation<CardInput>> violations = validator.validate(cardInput);
        if (!violations.isEmpty()) {
            throw new LayerConstraintViolationException(violations);
        }
    }
}