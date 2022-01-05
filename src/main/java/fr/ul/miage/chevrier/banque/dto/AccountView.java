package fr.ul.miage.chevrier.banque.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss", timezone = "UTC")
    private Date dateAdded;
    private String firstName;
    private String lastName;
    private String IBAN;
    private Double balance;
    //TODO Pas de birthdate, ni de passportNumber ?
}