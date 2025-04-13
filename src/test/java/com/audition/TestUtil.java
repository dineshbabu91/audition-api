package com.audition;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public final class TestUtil {
    public static <T> T getFile(final String filePath, final Class<T> responseType) throws IOException {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final File file = new File(Objects.requireNonNull(classLoader.getResource(filePath)).getFile());
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.readValue(file, responseType);
    }

}
