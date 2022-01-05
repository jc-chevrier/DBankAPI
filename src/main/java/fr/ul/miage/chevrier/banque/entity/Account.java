package fr.ul.miage.chevrier.banque.entity;

import com.sun.istack.Nullable;
import lombok.*;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account implements Serializable {
    @Id
    private UUID id;
    private Instant dateAdded;
    private String firstName;
    private String lastName;
    private Instant birthDate;
    private String passportNumber;
    @Nullable
    private String secret;
    private String IBAN;
    private Double balance;
}
