package fr.ul.miage.chevrier.banque.entity;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account implements Serializable {
    @Id
    private UUID id;

    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private Date dateAdded = new Date();

    private String firstName;

    private String lastName;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date birthDate;

    private String passportNumber;

    private String secret;

    private String IBAN;

    private Double balance;

    private boolean active = true;
}