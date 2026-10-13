package io.hexlet.spring.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class UserPatchDTO {
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50)
    private JsonNullable<String> firstName = JsonNullable.undefined();

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50)
    private JsonNullable<String> lastName = JsonNullable.undefined();

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private JsonNullable<String> email = JsonNullable.undefined();
}
