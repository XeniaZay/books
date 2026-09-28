package com.example.mvc.orders;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record OrderRequest(
        @NotBlank String productId,
        @Min(1)
        @Max(1000)
        int quantity
) {
}
