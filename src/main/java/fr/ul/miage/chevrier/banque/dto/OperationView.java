package fr.ul.miage.chevrier.banque.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import fr.ul.miage.chevrier.banque.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Date dateAdded = new Date();

    private String label;

    private Double amount;

    private String externalAccountName;

    private String externalAccountIBAN;

    private String country;

    private Double rate;

    private String category;

    private UUID internalAccountId;
}