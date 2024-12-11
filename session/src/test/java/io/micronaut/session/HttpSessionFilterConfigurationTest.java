package io.micronaut.session;


import io.micronaut.context.annotation.Property;
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
        assertEquals("/**", configuration.getPath(),
            "Default exclude pattern should be '/**'");
    }

    @Test
    @Property(name = "micronaut.session.filter.exclude-pattern", value = "/static/**,/public/**")
    void testMultipleExclusionPatterns() {
        assertEquals("/static/**,/public/**", configuration.getPath());
        assertTrue(matchesExcludePattern("/static/image.jpg"), "Static path should be excluded");
        assertTrue(matchesExcludePattern("/public/script.js"), "Public path should be excluded");
        assertFalse(matchesExcludePattern("/api/users"), "API path shouldn't be excluded");
        assertFalse(matchesExcludePattern("/login"), "Login path shouldn't be excluded");
    }

    @Test
    @Property(name = "micronaut.session.filter.exclude-pattern", value = "/admin/**")
    void testSingleExclusionPattern() {
        assertEquals("/admin/**", configuration.getPath());

        assertTrue(matchesExcludePattern("/admin/users"), "Admin path should be excluded");
        assertTrue(matchesExcludePattern("/admin/settings"), "Admin path should be excluded");
        assertFalse(matchesExcludePattern("/user/profile"), "User path shouldn't be excluded");
    }

    @Test
    @Property(name = "micronaut.session.filter.exclude-pattern", value = "")
    void testEmptyExclusionPattern() {
        assertEquals("", configuration.getPath());
        assertFalse(matchesExcludePattern("/any/path"), "No path should match empty pattern");
    }


    private boolean matchesExcludePattern(String path) {
        if (configuration.getPath() == null || configuration.getPath().isEmpty()) {
            return false;
        }

        String[] patterns = configuration.getPath().split(",");
        for (String pattern : patterns) {
            if (pathMatches(pattern.trim(), path)) {
                return true;
            }
        }
        return false;
    }

    private boolean pathMatches(String pattern, String path) {
        if (pattern.isEmpty()) {
            return false;
        }

        String regex = "^" + pattern
            .replace(".", "\\.")
            .replace("**", ".*")
            .replace("*", "[^/]*") + "$";
        return path.matches(regex);
    }
}
