package fr.ul.miage.chevrier.banque.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountView {
    private UUID id;
    private Date dateAdded;
    private String firstName;
    private String lastName;
    private String IBAN;
    private Double balance;
    //TODO Pas de birthdate, ni de passportNumber ?
}
