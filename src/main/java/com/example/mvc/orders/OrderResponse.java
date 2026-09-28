package com.example.mvc.orders;

public record OrderResponse(
        String id,
        String productId,
        int quantity,
        String status
) {
}
