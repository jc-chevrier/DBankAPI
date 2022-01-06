package fr.ul.miage.chevrier.banque.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.UUID;

/**
 * Classe pour les saisies des champs des opérations
 * sur les comptes bancaires (DTO).
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
    @Size(min = 15, max = 34)
    @Pattern(regexp = "^[A-Z]{2}[0-9]{13,32}$")
    private String externalAccountIBAN;

    @NotBlank
    private String externalAccountName;

    @NotBlank
    private String country;

    private String category;

    @NotNull
    private UUID internalAccountId;
}