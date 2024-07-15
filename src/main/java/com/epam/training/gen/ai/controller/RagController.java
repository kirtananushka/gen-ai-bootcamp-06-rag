package com.epam.training.gen.ai.controller;

import com.epam.training.gen.ai.model.ChatRequest;
import com.epam.training.gen.ai.model.ChatResponse;
import com.epam.training.gen.ai.service.RagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RagController {

    private final RagService ragService;

    @PostMapping("/ai/upload")
    public void uploadMultipleFiles(@RequestParam("files") List<MultipartFile> files) {
        files.stream()
                .filter(file -> !file.isEmpty())
                .map(this::getUploadedFile)
                .forEach(this::processFile);
    }

    @PostMapping("/ai/add")
    public void addInfo(@Valid @RequestBody ChatRequest chatRequest) {
        ragService.addDocument(chatRequest.query());
    }

    @PostMapping("/ai/query")
    public ResponseEntity<ChatResponse> query(@Valid @RequestBody ChatRequest chatRequest) {
        String response = ragService.generateAnswer(chatRequest.query());
        return new ResponseEntity<>(new ChatResponse(response), HttpStatus.OK);
    }

    private File getUploadedFile(MultipartFile file) {
        Path uploadPath = getUploadPath();
        Path filePath = copyUploadedFile(file, uploadPath);
        return filePath.toFile();
    }

    private Path getUploadPath() {
        Path uploadPath = Paths.get("target", "uploads");

        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create upload directory", e);
            }
        }
        return uploadPath;
    }

    private Path copyUploadedFile(MultipartFile file, Path uploadPath) {
        String fileName = file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        try {
            Files.copy(file.getInputStream(), filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to copy uploaded file", e);
        }
        return filePath;
    }

    private void processFile(File uploadedFile) {
        try {
            ragService.addDocument(uploadedFile);
        } finally {
            uploadedFile.delete();
        }
    }
}