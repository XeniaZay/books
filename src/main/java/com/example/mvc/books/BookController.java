package com.example.mvc.books;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
class BookController {

    private static final int MAX_PAGE_SIZE = 100;

    @GetMapping("/{id}")
    BookResponse getById(
            @PathVariable Long id,
            @RequestHeader(value = "X-Request-Id", required = false) String requestId
    ) {
        return new BookResponse(
                id,
                "Effective Java",
                "Joshua Bloch",
                requestId
        );
    }

    @GetMapping
    BookPageResponse search(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) List<String> tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);

        List<BookResponse> content = List.of(
                new BookResponse(
                        1L,
                        "Effective Java",
                        author != null ? author : "Joshua Bloch",
                        null
                )
        );

        return new BookPageResponse(content, page, safeSize, 1);
    }
}