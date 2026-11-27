package com.rubenmarin.documentservice.infrastructure.adapter.in;

import com.rubenmarin.documentservice.application.port.in.UploadDocumentUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {

    private final UploadDocumentUseCase uploadDocumentUseCase;

    public DocumentController(UploadDocumentUseCase uploadDocumentUseCase) {
        this.uploadDocumentUseCase = uploadDocumentUseCase;
    }


    @PostMapping
    public ResponseEntity<String> uploadDocument(@RequestParam("file") MultipartFile file) {

        String filename = file.getOriginalFilename();
        String contentType = file.getContentType();
        long contentLength = file.getSize();

        try (InputStream content = file.getInputStream()) {

            String response = uploadDocumentUseCase.upload(filename, contentType, contentLength, content);

            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error reading document",
                    e
            );
        }


    }
}
