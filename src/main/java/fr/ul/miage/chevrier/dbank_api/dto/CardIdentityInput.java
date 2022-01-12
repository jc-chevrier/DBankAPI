package fr.ul.miage.chevrier.dbank_api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;

/**
 * Classe pour les saisies de vérification
 * des codes des cartes des comptes bancaires
 * (DTO).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardIdentityInput {
    @NotBlank
    @Pattern(regexp = "^[0-9]{16}$")
    private String number;

    @NotBlank
    @Pattern(regexp = "^[0-9]{3,4}$")
    private String cryptogram;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM", timezone = "UTC")
    private Date expirationDate;
}