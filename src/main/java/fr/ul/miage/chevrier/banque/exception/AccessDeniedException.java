package fr.ul.miage.chevrier.banque.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception pour les accès refusés.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
@Getter
@ToString
@RequiredArgsConstructor
public class AccessDeniedException extends RuntimeException {
    public static AccessDeniedException of() {
        return new AccessDeniedException();
    }
}