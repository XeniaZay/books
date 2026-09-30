package com.example.mvc.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.Callable;

@RestController
@RequestMapping("/api")
public class AsyncExportController {
    private static final Logger log = LoggerFactory.getLogger(AsyncExportController.class);

    @GetMapping(
            value = "/export/orders.csv",
            produces = "text/csv"
    )
    ResponseEntity<StreamingResponseBody> exportOrders() {
        StreamingResponseBody body = outputStream -> {
            outputStream.write("id,status,amountMinor\n".getBytes(StandardCharsets.UTF_8));

            for (int i = 1; i <= 1000; i++) {
                String row = i + ",CREATED," + (i * 100) + "\n";
                outputStream.write(row.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                log.info("Streamed {} rows", i);
            }
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"orders.csv\"")
                .body(body);
    }

    @GetMapping("/async/status")
    Callable<ResponseEntity<Map<String, String>>> status() {
        return () -> {
            log.info("Async status started: thread={}", Thread.currentThread().getName());
            Thread.sleep(3000);
            log.info("Async status completed: thread={}", Thread.currentThread().getName());
            return ResponseEntity.ok(Map.of("status", "READY"));
        };
    }
}
