package com.audition;

import com.audition.configuration.ApiConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class AuditionApplicationTests {
    @Autowired
    private ApiConfiguration apiConfiguration;

    @Test
    void contextLoads() {
        assertNotNull(apiConfiguration.getAuditionApi());
        assertNotNull(apiConfiguration.getAuditionApi().getBaseUrl());
    }

}
