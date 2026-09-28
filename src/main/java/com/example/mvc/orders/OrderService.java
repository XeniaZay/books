package com.example.mvc.orders;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderService {

    Order create(String productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }
        return new Order(UUID.randomUUID().toString(), productId, quantity, "Created");
    }
}
