package com.example.mvc.reports;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @GetMapping(value = "/sales", produces = MediaType.APPLICATION_JSON_VALUE)
    List<SalesRow> salesJson() {
        return List.of(
                new SalesRow("2026-01", 10, 150000),
                new SalesRow("2026-02", 12, 180000)
        );
    }

    @GetMapping(value = "/sales", produces = "text/csv")
    String salesCsv() {
        StringBuilder sb = new StringBuilder();
        sb.append("month,count,amountMinor\n");
        List<SalesRow> salesRowList = List.of(
                new SalesRow("2026-01", 10, 150000),
                new SalesRow("2026-02", 12, 180000));

        for (SalesRow row : salesRowList) {
            sb.append(row.month()).append(',')
                    .append(row.count()).append(',')
                    .append(row.amountMinor()).append('\n');
        }
        return sb.toString();
    }

}
