package com.example.mvc.common;

import com.example.mvc.files.FileNotFoundException;
import com.example.mvc.files.InvalidFileException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail handleValidation(MethodArgumentNotValidException ex,
                                   HttpServletRequest request) {

        List<Map<String, String>> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> Map.of(
                        "field", fe.getField(),
                        "message", fe.getDefaultMessage() == null
                                ? "invalid value"
                                : fe.getDefaultMessage()
                ))
                .toList();

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Validation failed for " + errors.size() + " field(s)"
        );
        problem.setTitle("Validation failed");
        problem.setType(URI.create("https://example.com/problems/validation"));
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("timestamp", Instant.now().toString());
        problem.setProperty("errors", errors);
        addTraceId(problem);

        log.warn("Validation failed: path={} errors={}",
                request.getRequestURI(), errors);

        return problem;
    }


    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail handleIllegalArgument(IllegalArgumentException ex,
                                        HttpServletRequest request) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage() == null ? "Invalid argument" : ex.getMessage()
        );
        problem.setTitle("Invalid request");
        problem.setType(URI.create("https://example.com/problems/bad-request"));
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("timestamp", Instant.now().toString());
        addTraceId(problem);

        log.warn("Illegal argument: path={} message={}",
                request.getRequestURI(), ex.getMessage());

        return problem;
    }

    @ExceptionHandler(FileNotFoundException.class)
    ProblemDetail handleFileNotFound(FileNotFoundException ex,
                                     HttpServletRequest request) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
        problem.setTitle("File not found");
        problem.setType(URI.create("https://example.com/problems/file-not-found"));
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("timestamp", Instant.now().toString());
        addTraceId(problem);

        log.warn("File not found: path={} message={}",
                request.getRequestURI(), ex.getMessage());

        return problem;
    }

    // ============================================================
    // 5. Невалидный файл (расширение, пустое имя, path traversal) → 400
    // ============================================================
    @ExceptionHandler(InvalidFileException.class)
    ProblemDetail handleInvalidFile(InvalidFileException ex,
                                    HttpServletRequest request) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
        problem.setTitle("Invalid file");
        problem.setType(URI.create("https://example.com/problems/invalid-file"));
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("timestamp", Instant.now().toString());
        addTraceId(problem);

        log.warn("Invalid file: path={} message={}",
                request.getRequestURI(), ex.getMessage());

        return problem;
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    ProblemDetail handleMaxUploadSize(MaxUploadSizeExceededException ex,
                                      HttpServletRequest request) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONTENT_TOO_LARGE,
                "File is too large. Maximum allowed size exceeded."
        );
        problem.setTitle("Payload too large");
        problem.setType(URI.create("https://example.com/problems/payload-too-large"));
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("timestamp", Instant.now().toString());
        addTraceId(problem);

        log.warn("Max upload size exceeded: path={} message={}",
                request.getRequestURI(), ex.getMessage());

        return problem;
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail handleAll(Exception ex,
                            HttpServletRequest request) {

        if (ex instanceof org.springframework.web.ErrorResponse errorResponse) {
            try {
                throw (Throwable) errorResponse;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected error occurred"
        );
        problem.setTitle("Internal Server Error");
        problem.setType(URI.create("https://example.com/problems/internal-error"));
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("timestamp", Instant.now().toString());
        addTraceId(problem);

        log.error("Unhandled error: path={}", request.getRequestURI(), ex);

        return problem;
    }

    /**
     * Достаёт traceId из MDC. Ищет и "traceId", и "requestId",
     * чтобы работать с любым названием ключа.
     */
    private void addTraceId(ProblemDetail problem) {
        String traceId = MDC.get("traceId");
        if (traceId == null) {
            traceId = MDC.get("requestId");
        }
        if (traceId != null && !traceId.isBlank()) {
            problem.setProperty("traceId", traceId);
        }
    }
}