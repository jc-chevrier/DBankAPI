package fr.ul.miage.chevrier.dbank_api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.UUID;

/**
 * Classe pour la vue complète de
 * l'extérieur des comptes bancaires
 * (DTO).
 */
@Data
@NoArgsConstructor
public class AccountCompleteView extends AccountView {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "UTC")
    private Date birthDate;

    private String passportNumber;
    
    public AccountCompleteView(UUID id, String firstName, String lastName, Date birthDate, String country, String passportNumber,
                       String phoneNumber, String IBAN, Double balance, Date dateAdded) {
        super(id, firstName, lastName, country, phoneNumber,  IBAN,  balance, dateAdded);
        this.birthDate = birthDate;
        this.passportNumber = passportNumber;
    }
}


