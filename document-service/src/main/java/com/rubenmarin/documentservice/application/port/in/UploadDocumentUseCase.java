package com.rubenmarin.documentservice.application.port.in;

import java.io.InputStream;

public interface UploadDocumentUseCase {
    /**
     * Recibe el nombre original del documento
     */
    String upload(String filename,
                  String contentType,
                  long contentLength,
                  InputStream content);
}
