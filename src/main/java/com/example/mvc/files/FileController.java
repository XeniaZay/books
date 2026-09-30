package com.example.mvc.files;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/files")
class FileController {
    private final FileStorageService fileStorageServiceservice;

    FileController(FileStorageService service) {
        this.fileStorageServiceservice = service;
    }

    @PostMapping
    ResponseEntity<FileUploadResponse> upload(@RequestPart("file") MultipartFile file) {
        String storedName = fileStorageServiceservice.store(file);
        return ResponseEntity.ok(new FileUploadResponse(storedName, file.getSize()));
    }

    @GetMapping("/{name}")
    ResponseEntity<Resource> download(@PathVariable String name){
        Path path = fileStorageServiceservice.load(name);
        Resource resource;
        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException ex) {
            throw new FileNotFoundException("Invalid path: " + name);
        }

        return ResponseEntity.ok()
                .contentType(name.toLowerCase().endsWith(".csv")? MediaType.parseMediaType("text/csv") : name.toLowerCase().endsWith(".txt")? MediaType.TEXT_PLAIN : MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + name + "\"")
                .body(resource);
    }
}
