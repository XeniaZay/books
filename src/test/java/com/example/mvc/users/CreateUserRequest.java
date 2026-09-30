package com.example.mvc.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(@NotBlank(message = "name is required")
                                @Size(min = 2, max = 50, message = "name must be 2-50 characters")
                                String name,
                                @NotBlank(message = "email is required")
                                @Email(message = "email must be valid")
                                String email) {
}
