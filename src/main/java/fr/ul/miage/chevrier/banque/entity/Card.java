package fr.ul.miage.chevrier.banque.entity;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
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

    private String code;

    private String cryptogram;

    private Integer blocked;

    private Integer localization;

    private Integer contactless;

    private Double cap;

    private Integer virtual;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dateAdded = new Date();

    private boolean active = true;

    @OneToOne
    @JoinColumn(name = "ACCOUNT_ID", referencedColumnName = "ID")
    private Account account;
}