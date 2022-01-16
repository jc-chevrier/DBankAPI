package fr.ul.miage.chevrier.dbank_api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.UUID;

/**
 * Classe pour la vue de l'extérieur des
 * opérations sur les comptes bancaires (DTO).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationView {
    private UUID id;

    private String label;

    private Double amount;

    private String secondAccountName;

    private String secondAccountCountry;

    private String secondAccountIBAN;

    private Double rate;

    private Boolean confirmed;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Date dateAdded = new Date();

    private UUID firstAccountId;

    private UUID firstAccountCardId;
}