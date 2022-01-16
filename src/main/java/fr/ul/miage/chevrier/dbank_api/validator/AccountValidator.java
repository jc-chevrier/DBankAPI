package fr.ul.miage.chevrier.dbank_api.validator;

import fr.ul.miage.chevrier.dbank_api.dto.AccountInput;
import fr.ul.miage.chevrier.dbank_api.exception.LayerConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

/**
 * Validateur pour les comptes bancaires.
 */
@Service
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AccountValidator {
    private Validator validator;

    AccountValidator(Validator validator) {
        this.validator = validator;
    }

    public void validate(AccountInput input) {
        Set<ConstraintViolation<AccountInput>> violations = validator.validate(input);
        if (!violations.isEmpty()) {
            throw new LayerConstraintViolationException(violations);
        }
    }
}