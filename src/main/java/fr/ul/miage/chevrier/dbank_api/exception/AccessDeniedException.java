package fr.ul.miage.chevrier.dbank_api.exception;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception pour les accès refusés.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
@Getter
@ToString
public class AccessDeniedException extends RuntimeException {
    public static AccessDeniedException of() {
        return new AccessDeniedException();
    }
}