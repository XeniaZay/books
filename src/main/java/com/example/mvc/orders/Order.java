package com.example.mvc.orders;

public record Order(
        String id,
        String productId,
        int quantity,
        String status
) {
}
