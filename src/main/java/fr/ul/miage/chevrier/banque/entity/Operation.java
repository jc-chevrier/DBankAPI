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

    private String secondAccountName;

    private String secondAccountCountry;

    @Column(name = "SECOND_ACCOUNT_IBAN")
    private String secondAccountIBAN;

    private Double rate;

    private String category;

    private Boolean confirmed = false;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dateAdded = new Date();

    private Boolean active = true;

    @ManyToOne
    @JoinColumn(name = "FIRST_ACCOUNT_ID", referencedColumnName = "ID")
    private Account firstAccount;

    @ManyToOne
    @JoinColumn(name = "FIRST_ACCOUNT_CARD_ID", referencedColumnName = "ID")
    private Card firstAccountCard;
}