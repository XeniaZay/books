package com.example.mvc.books;

public record BookResponse(
        Long id,
        String title,
        String author,
        String requestId
) {
}