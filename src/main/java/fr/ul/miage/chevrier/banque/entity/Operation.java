package fr.ul.miage.chevrier.banque.entity;

import com.sun.istack.Nullable;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Operation {
    @Id
    private UUID id;
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private Date dateAdded = new Date();
    private String label;
    private Double amount;
    @Column(name = "EXTERNAL_ACCOUNT_IBAN")
    private String externalAccountIBAN;
    private String externalAccountName;
    private String country;
    @Nullable
    private Double rate;
    private String direction;
    @Nullable
    private String category;
    private boolean active;
    @OneToOne
    @JoinColumn(name = "internal_account_id", referencedColumnName = "id")
    private Account internalAccount;
}