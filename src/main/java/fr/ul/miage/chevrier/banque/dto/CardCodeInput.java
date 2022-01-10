package fr.ul.miage.chevrier.banque.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.util.Date;
import java.util.UUID;

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