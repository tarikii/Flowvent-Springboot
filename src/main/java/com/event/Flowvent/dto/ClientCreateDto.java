package com.event.Flowvent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClientCreateDto {

    @NotBlank(message = "The name of the client cannot be empty.")
    @Schema(example = "Nick Drake")
    private String name;

    @NotBlank(message = "The email cannot be blank.")
    @Email(message = "Not a valid email.")
    @Schema(example = "example@gmail.com")
    private String email;
}
