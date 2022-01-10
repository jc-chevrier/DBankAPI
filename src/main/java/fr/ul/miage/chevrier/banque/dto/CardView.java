package fr.ul.miage.chevrier.banque.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.UUID;

/**
 * Classe pour la vue de l'extérieur des
 * cartes des comptes bancaires (DTO).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardView {
    private UUID id;

    private String number;

    private Double ceiling;

    private Boolean virtual;

    private Boolean localization;

    private Boolean contactless;

    private Boolean blocked;

    private Date dateAdded;

    private UUID accountId;
}