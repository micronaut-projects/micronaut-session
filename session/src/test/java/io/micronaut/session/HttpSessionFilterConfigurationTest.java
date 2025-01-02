package io.micronaut.session;

import io.micronaut.session.http.HttpSessionFilterConfiguration;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class HttpSessionFilterConfigurationTest {

    @Inject
    HttpSessionFilterConfiguration configuration;

    @Test
    void testDefaultConfiguration() {
        assertEquals("^.*$", configuration.getRegexPattern(),
            "Default exclude pattern should be '^.*$'");

        assertTrue( configuration.isEnabled(),
                "filter is enabled by default");
    }
}
