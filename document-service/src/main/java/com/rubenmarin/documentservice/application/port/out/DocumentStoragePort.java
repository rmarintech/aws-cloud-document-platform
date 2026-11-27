package com.rubenmarin.documentservice.application.port.out;

import java.io.InputStream;

public interface DocumentStoragePort {

    /** Recibe la clave única generada por nuestra aplicación */
    void upload(String key,
                String contentType,
                long contentLength,
                InputStream content);
}
