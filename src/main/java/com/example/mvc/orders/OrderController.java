package com.example.mvc.orders;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/orders")
class OrderController {
    private final OrderService service;

    OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    ResponseEntity<OrderResponse> create(@RequestBody OrderRequest request) {
        Order order = service.create(request.productId(), request.quantity());

        OrderResponse response = new OrderResponse(
                order.id(),
                order.productId(),
                order.quantity(),
                order.status()
        );

        return ResponseEntity
                .created(URI.create("/api/orders/" + order.id()))
                .body(response);
    }
}
