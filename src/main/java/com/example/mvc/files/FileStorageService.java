package com.example.mvc.files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".txt", ".csv");
    private final Path storageDir;

    public FileStorageService() throws IOException {
        this.storageDir = Files.createTempDirectory("mvc-files-");
        log.info("File storage directory: {}", storageDir);
    }


    /**
     * Сохраняет загруженный файл и возвращает его имя (с UUID-префиксом).
     */
    public String store(MultipartFile file) {
        // 1. Проверка на пустой файл
        if (file.isEmpty()) {
            throw new InvalidFileException("File is empty");
        }

        // 2. Проверка оригинального имени
        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            throw new InvalidFileException("File name is missing");
        }

        // 3. Проверка расширения
        String extension = extractExtension(originalName);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new InvalidFileException(
                    "Only " + ALLOWED_EXTENSIONS + " are allowed, got: " + extension);
        }

        // 4. Генерируем безопасное имя — UUID + оригинальное расширение
        String storedName = UUID.randomUUID() + extension;

        // 5. Защита от path traversal
        Path target = storageDir.resolve(storedName).normalize();
        if (!target.startsWith(storageDir)) {
            throw new InvalidFileException("Invalid file path");
        }

        // 6. Сохраняем
        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            log.info("Stored file: original={} stored={} size={}",
                    originalName, storedName, file.getSize());
            return storedName;
        } catch (IOException ex) {
            log.error("Failed to store file", ex);
            throw new InvalidFileException("Failed to store file: " + ex.getMessage());
        }
    }

    /**
     * Возвращает Path к файлу, если он существует.
     */
    public Path load(String name) {
        Path file = storageDir.resolve(name).normalize();
        if (!file.startsWith(storageDir)) {
            throw new InvalidFileException("Invalid file name");
        }

        if (!Files.exists(file) || !Files.isRegularFile(file)) {
            throw new FileNotFoundException("File not found: " + name);
        }

        return file;
    }

    private String extractExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        if (dot < 0) {
            return "";
        }
        return filename.substring(dot).toLowerCase();
    }
}