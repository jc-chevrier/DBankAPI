package fr.ul.miage.chevrier.banque.validator;

import fr.ul.miage.chevrier.banque.dto.AccountInput;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

/**
 * Validateur pour les comptes bancaires
 * des clients.
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
            throw new ConstraintViolationException(violations);
        }
    }
}