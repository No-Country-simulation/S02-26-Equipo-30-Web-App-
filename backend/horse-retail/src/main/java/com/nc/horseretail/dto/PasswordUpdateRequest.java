package com.nc.horseretail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request object for updating user password")
public class PasswordUpdateRequest {

    @NotBlank(message = "Current password is required")
    @Schema(description = "User's current password", example = "OldPassword123!")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&._-]).+$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one number and one special character"
    )
    @Schema(
            description = "New password. Must contain uppercase, lowercase, number and special character",
            example = "NewSecure123!"
    )
    private String newPassword;

    @NotBlank(message = "Password confirmation is required")
    @Schema(description = "Confirmation of the new password", example = "NewSecure123!")
    private String confirmPassword;
}