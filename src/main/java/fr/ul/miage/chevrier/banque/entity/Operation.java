package fr.ul.miage.chevrier.banque.entity;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

/**
 * Entité pour les opérations sur les
 * comptes bancaires.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Operation {
    @Id
    private UUID id;

    private String label;

    private Double amount;

    private String externalAccountName;

    @Column(name = "EXTERNAL_ACCOUNT_IBAN")
    private String externalAccountIBAN;

    private String country;

    private Double rate;

    private String category;

    private boolean confirmed = false;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dateAdded = new Date();

    private boolean active = true;

    @OneToOne
    @JoinColumn(name = "INTERNAL_ACCOUNT_ID", referencedColumnName = "id")
    private Account internalAccount;
}