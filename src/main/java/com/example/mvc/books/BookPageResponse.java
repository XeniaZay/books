package com.example.mvc.books;

import java.util.List;

public record BookPageResponse(
        List<BookResponse> content,
        int page,
        int size,
        long total
) {
}