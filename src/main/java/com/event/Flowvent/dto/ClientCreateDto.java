package com.event.Flowvent.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClientCreateDto {

    @NotBlank(message = "The name of the client cannot be empty.")
    private String name;

    @NotBlank(message = "The email cannot be blank.")
    @Email(message = "Not a valid email.")
    private String email;
}
