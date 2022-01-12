package fr.ul.miage.chevrier.dbank_api.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.util.UUID;

/**
 * Exception pour les comptes bancaires
 * non trouvés.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
@Getter
@ToString
@RequiredArgsConstructor
public class AccountNotFoundException extends RuntimeException {
    private final UUID id;
    public static AccountNotFoundException of(UUID id) {
        return new AccountNotFoundException(id);
    }
}