package fr.ul.miage.chevrier.dbank_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.*;
import java.util.UUID;

/**
 * Classe pour les saisies des champs des
 * opérations sur les comptes bancaires (DTO).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationInput {
    @NotBlank
    private String label;

    @NotNull
    private Double amount;

    @NotBlank
    private String secondAccountName;

    @NotBlank
    private String secondAccountCountry;

    @NotBlank
    @Size(min = 15, max = 34)
    @Pattern(regexp = "^[A-Z]{2}[0-9]{13,32}$")
    private String secondAccountIBAN;

    private String category;

    @NotNull
    private UUID firstAccountId;

    private UUID firstAccountCardId;
}