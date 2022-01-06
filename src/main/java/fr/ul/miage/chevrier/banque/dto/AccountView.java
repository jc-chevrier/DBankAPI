package fr.ul.miage.chevrier.banque.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.UUID;

/**
 * Classe pour la vue de l'extérieur
 * des comptes bancaires (DTO).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountView {
    private UUID id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Date dateAdded;

    private String firstName;

    private String lastName;

    private String country;

    private String IBAN;

    private Double balance;
}