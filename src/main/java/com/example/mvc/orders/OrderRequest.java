package com.example.mvc.orders;

public record OrderRequest(
        String productId,
        int quantity
) {
}
