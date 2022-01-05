package fr.ul.miage.chevrier.banque.entity;

import lombok.*;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card implements Serializable {
    @Id
    private UUID id;
    private Instant dateAdded;
    private String number;
    private String code;
    private String cryptogram;
    private Integer blocked;
    private Integer localization;
    private Integer contactless;
    private Double cap;
    private Integer virtual;
    @OneToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account account;
}
