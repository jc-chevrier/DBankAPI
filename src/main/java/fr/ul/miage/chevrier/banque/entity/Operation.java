package fr.ul.miage.chevrier.banque.entity;

import com.sun.istack.Nullable;
import lombok.*;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Operation {
    @Id
    private UUID id;
    private Date dateAdded;
    private String label;
    private Double amount;
    private String externalAccountIBAN;
    private String externalAccountName;
    private String country;
    @Nullable
    private Double rate;
    private String direction;
    @Nullable
    private String category;
    @OneToOne
    @JoinColumn(name = "internal_account_id", referencedColumnName = "id")
    private Account internalAccount;
}
