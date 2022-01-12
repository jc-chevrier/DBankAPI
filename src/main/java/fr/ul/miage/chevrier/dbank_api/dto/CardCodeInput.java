package fr.ul.miage.chevrier.dbank_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * Classe pour les saisies de vérification
 * des codes des cartes des comptes bancaires
 * (DTO).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardCodeInput {
    @NotBlank
    @Pattern(regexp = "^[0-9]{4}$")
    private String code;
}