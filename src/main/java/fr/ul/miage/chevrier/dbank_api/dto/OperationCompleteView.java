package fr.ul.miage.chevrier.dbank_api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import fr.ul.miage.chevrier.dbank_api.entity.Account;
import fr.ul.miage.chevrier.dbank_api.entity.Card;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

/**
 * Classe pour la vue complèete de l'extérieur des
 * opérations sur les comptes bancaires (DTO).
 */
@Data
@NoArgsConstructor
public class OperationCompleteView extends OperationView {
    private String category;

    public OperationCompleteView(UUID id, String label, Double amount, String secondAccountName, String secondAccountCountry,
                                  String secondAccountIBAN, Double rate, String category, Boolean confirmed,
                                  Date dateAdded, UUID firstAccountId, UUID firstAccountCardId) {
        super(id, label, amount, secondAccountName, secondAccountCountry, secondAccountIBAN, rate, confirmed,
        dateAdded, firstAccountId, firstAccountCardId);
        this.category = category;
    }
}