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
public class CardNotFoundException extends RuntimeException {
    private final UUID id;
    public static CardNotFoundException of(UUID id) {
        return new CardNotFoundException(id);
    }
}