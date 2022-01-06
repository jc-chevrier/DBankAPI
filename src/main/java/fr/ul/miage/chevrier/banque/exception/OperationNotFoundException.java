package fr.ul.miage.chevrier.banque.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.util.UUID;

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