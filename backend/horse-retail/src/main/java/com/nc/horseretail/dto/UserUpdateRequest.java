package com.nc.horseretail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request to update user profile")
public class UserUpdateRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 150, message = "Full name cannot exceed 150 characters")
    @Schema(example = "John Doe")
    private String fullName;

    @Email(message = "Invalid email format")
    @Size(max = 150)
    @Schema(example = "john@example.com")
    private String email;
}