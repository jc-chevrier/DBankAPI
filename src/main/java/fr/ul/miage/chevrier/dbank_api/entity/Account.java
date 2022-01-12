package fr.ul.miage.chevrier.dbank_api.entity;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Entité pour les comptes bancaires.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account implements Serializable {
    @Id
    private UUID id;

    private String firstName;

    private String lastName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;

    private String country;

    private String passportNumber;

    private String phoneNumber;

    private String IBAN;

    private String secret;

    private Double balance = 0.0;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dateAdded = new Date();

    private Boolean active = true;

    /**
     * Incémenter / décrementer le solde du compte
     * selon le signe du montant.
     *
     * @param amount        Montant.
     * @return Double       Nouveau solde.
     */
    public Double incrementBalance(Double amount) {
        balance += amount;
        return balance;
    }
}