package fr.ul.miage.chevrier.dbank_api.dto;

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
 * Classe pour les saisies des champs des
 * cartes des comptes bancaires (DTO).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardInput {
    @NotBlank
    @Pattern(regexp = "^[0-9]{16}$")
    private String number;

    @NotBlank
    @Pattern(regexp = "^[0-9]{3,4}$")
    private String cryptogram;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM", timezone = "UTC")
    private Date expirationDate;

    @NotBlank
    @Pattern(regexp = "^[0-9]{4}$")
    private String code;

    @NotNull
    @Positive
    private Double ceiling;

    @NotNull
    private Boolean virtual;

    @NotNull
    private Boolean localization;

    @NotNull
    private Boolean contactless;

    @NotNull
    private Boolean blocked;

    @NotNull
    private UUID accountId;
}