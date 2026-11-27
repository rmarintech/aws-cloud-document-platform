package com.rubenmarin.documentservice.application.service;

import com.rubenmarin.documentservice.application.port.in.UploadDocumentUseCase;
import com.rubenmarin.documentservice.application.port.out.DocumentStoragePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.UUID;

public class DocumentService implements UploadDocumentUseCase {


    private static final Logger log = LoggerFactory.getLogger(DocumentService.class);
    private final DocumentStoragePort documentStoragePort;

    public DocumentService(DocumentStoragePort documentStoragePort) {
        this.documentStoragePort = documentStoragePort;
    }


    @Override
    public String upload(String filename, String contentType, long contentLength, InputStream content) {

        String key = "documents/" + UUID.randomUUID() + "/" + filename;
        log.info("Uploading document: {}", key);

        documentStoragePort.upload(key, contentType, contentLength, content);
        return key;
    }
}
