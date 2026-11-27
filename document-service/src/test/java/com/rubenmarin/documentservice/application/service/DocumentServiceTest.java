package com.rubenmarin.documentservice.application.service;

import com.rubenmarin.documentservice.application.port.out.DocumentStoragePort;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.InputStream;


class DocumentServiceTest {

    private DocumentStoragePort documentStoragePort;
    private DocumentService documentService;

    @BeforeEach
    void setUp() {
        documentStoragePort = Mockito.mock(DocumentStoragePort.class);
        documentService = new DocumentService(documentStoragePort);
    }

    @Test
    void shouldUploadDocumentAndReturnGeneratedKey() {

        // Given
        String filename = "invoice.pdf";
        String contentType = "application/pdf";

        byte[] data = "Test document".getBytes();
        long contentLength = data.length;
        InputStream content = new ByteArrayInputStream(data);

        // When
        String key = documentService.upload(filename, contentType, contentLength, content);

        // Then
        Assertions.assertNotNull(key);
        Assertions.assertTrue(key.startsWith("documents/"));
        Assertions.assertTrue(key.endsWith("/" + filename));

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(documentStoragePort, Mockito.times(1)).upload(
                keyCaptor.capture(),
                Mockito.eq(contentType),
                Mockito.eq(contentLength),
                Mockito.same(content)
        );

        Assertions.assertEquals(key, keyCaptor.getValue());

        Mockito.verifyNoMoreInteractions(documentStoragePort);
    }

    @Test
    void shouldGenerateDifferentKeysForSameFilename() {

        // Given
        String filename = "invoice.pdf";
        String contentType = "application/pdf";
        byte[] data = "Test document".getBytes();

        // When
        String firstKey = documentService.upload(
                filename,
                contentType,
                data.length,
                new ByteArrayInputStream(data)
        );

        String secondKey = documentService.upload(
                filename,
                contentType,
                data.length,
                new ByteArrayInputStream(data)
        );

        // Then
        Assertions.assertNotEquals(firstKey, secondKey);

        Mockito.verify(documentStoragePort, Mockito.times(2)).upload(
                Mockito.anyString(),
                Mockito.eq(contentType),
                Mockito.eq((long) data.length),
                Mockito.any(InputStream.class)
        );
    }

    @Test
    void shouldPropagateExceptionWhenStorageFails() {

        // Given
        String filename = "invoice.pdf";
        String contentType = "application/pdf";
        byte[] data = "Test document".getBytes();
        InputStream content = new ByteArrayInputStream(data);

        RuntimeException exception = new RuntimeException("Storage unavailable");

        Mockito.doThrow(exception)
                .when(documentStoragePort)
                .upload(
                        Mockito.anyString(),
                        Mockito.eq(contentType),
                        Mockito.eq((long) data.length),
                        Mockito.same(content)
                );

        // When
        RuntimeException result = Assertions.assertThrows(
                RuntimeException.class,
                () -> documentService.upload(filename, contentType, data.length, content)
        );

        // Then
        Assertions.assertSame(exception, result);

        Mockito.verify(documentStoragePort, Mockito.times(1)).upload(
                Mockito.anyString(),
                Mockito.eq(contentType),
                Mockito.eq((long) data.length),
                Mockito.same(content)
        );
    }
}