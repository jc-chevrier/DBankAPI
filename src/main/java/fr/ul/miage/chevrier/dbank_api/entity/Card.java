package fr.ul.miage.chevrier.dbank_api.entity;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Entité pour les cartes des comptes
 * bancaires.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card implements Serializable {
    @Id
    private UUID id;

    private String number;

    private String cryptogram;

    @DateTimeFormat(pattern = "yyyy-MM")
    private Date expirationDate;

    private String code;

    private Double ceiling;

    private Boolean virtual;

    private Boolean localization;

    private Boolean contactless;

    private Boolean blocked;

    private Boolean expired;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dateAdded = new Date();

    private Boolean active = true;

    @ManyToOne
    @JoinColumn(name = "ACCOUNT_ID", referencedColumnName = "ID")
    private Account account;
}