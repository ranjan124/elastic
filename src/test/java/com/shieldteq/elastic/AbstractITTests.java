package com.shieldteq.elastic;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AbstractITTests {
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private ResourceLoader loader;

    protected <T> T readResource(String path, TypeReference<T> clazz) {
        String classPath = "classpath:" + path;
        try {
            File file = loader.getResource(classPath).getFile();
            return mapper.readValue(file, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected <T> Consumer<T> print() {
        return c -> log.info("{}", c);
    }
}
