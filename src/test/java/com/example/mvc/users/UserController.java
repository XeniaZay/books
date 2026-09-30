package com.example.mvc.users;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final AtomicLong idGenerator = new AtomicLong(0);

    @PostMapping(
            consumes = "application/json",
            produces = "application/json"
    )
    ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        Long id = idGenerator.incrementAndGet();
        UserResponse response = new UserResponse(id, request.name(), request.email());
        return ResponseEntity
                .created(URI.create("/api/users/" + id))
                .body(response);
    }
}
