package dev.guilherme.demo.auth.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordDTO(@NotBlank String oldPassword, @NotBlank  @Size(min = 8) String newPassword) {
}
