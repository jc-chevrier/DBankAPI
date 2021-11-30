package fr.ul.miage.chevrier.banque.entity;

import com.sun.istack.Nullable;
import lombok.*;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account implements Serializable {
    @Id
    private UUID id;
    private Date dateAdded;
    private String firstName;
    private String lastName;
    private Date birthDate;
    private String passportNumber;
    @Nullable
    private String secret;
    private String IBAN;
    private Double balance;
}
