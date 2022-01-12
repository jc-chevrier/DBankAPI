package fr.ul.miage.chevrier.dbank_api.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.util.UUID;

/**
 * Exception pour les opérations bancaires
 * non trouvées.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
@Getter
@ToString
@RequiredArgsConstructor
public class OperationNotFoundException extends RuntimeException {
    private final UUID id;
    public static OperationNotFoundException of(UUID id) {
        return new OperationNotFoundException(id);
    }
}