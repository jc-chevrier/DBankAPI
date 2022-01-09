package fr.ul.miage.chevrier.banque.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * Classe pour les saisies des champs des
 * comptes bancaires (DTO).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountInput {
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "UTC")
    private Date birthDate;

    @NotBlank
    private String country;

    @NotBlank
    @Size(min = 9, max = 9)
    @Pattern(regexp = "^[0-9]{9}$")
    private String passportNumber;

    @NotBlank
    @Size(min = 11, max = 14)
    @Pattern(regexp = "^[0-9]{11,14}$")
    private String phoneNumber;

    @NotBlank
    @Size(min = 15, max = 34)
    @Pattern(regexp = "^[A-Z]{2}[0-9]{13,32}$")
    private String IBAN;
}