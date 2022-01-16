package fr.ul.miage.chevrier.dbank_api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import fr.ul.miage.chevrier.dbank_api.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

/**
 * Classe pour la vue complète de
 * l'extérieur des cartes des comptes
 * bancaires (DTO).
 */
@Data
@NoArgsConstructor
public class CardCompleteView extends CardView {
    private String cryprogram;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM", timezone = "UTC")
    private Date expirationDate;

    public CardCompleteView(UUID id, String number, String cryptogram, Date expirationDate, Double ceiling,
                            Boolean virtual, Boolean localization, Boolean contactless, Boolean blocked, Boolean expired,
                            Date dateAdded, UUID accountId) {
        super(id, number, ceiling, virtual, localization, contactless, blocked, expired, dateAdded, accountId);
        this.cryprogram = cryptogram;
        this.expirationDate = expirationDate;
    }
}