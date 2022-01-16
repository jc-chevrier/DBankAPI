package fr.ul.miage.chevrier.dbank_api.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

/**
 * Exception pour les cartes bancaires
 * bloquées.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
@Getter
@ToString
@RequiredArgsConstructor
public class CardBlockedException extends RuntimeException {
    private final UUID id;
}