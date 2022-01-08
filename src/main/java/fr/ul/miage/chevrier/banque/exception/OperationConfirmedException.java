package fr.ul.miage.chevrier.banque.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.util.UUID;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
@Getter
@ToString
@RequiredArgsConstructor
public class OperationConfirmedException extends RuntimeException {
    private final UUID id;
    public static OperationConfirmedException of(UUID id) {
        return new OperationConfirmedException(id);
    }
}