package fr.ul.miage.chevrier.banque.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountInput {
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", timezone = "UTC")
    private Date birthDate;

    @NotBlank
    @Size(min = 9, max = 9)
    private String passportNumber;

    @NotBlank
    @Size(min = 27, max = 27)
    private String IBAN;
}