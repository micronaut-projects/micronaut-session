
package io.micronaut.session;
import io.micronaut.session.http.HttpSessionFilterConfiguration;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HttpSessionFilterConfigurationTest {

    @Test
    void testRegexPatternDefault() {
        HttpSessionFilterConfiguration config = new HttpSessionFilterConfiguration();
        assertEquals("/.*", config.getRegexPattern(), "Default regex pattern should match all paths.");
    }

    @Test
    void testSetRegexPattern() {
        HttpSessionFilterConfiguration config = new HttpSessionFilterConfiguration();
        config.setRegexPattern("/assets/.*");
        assertEquals("/assets/.*", config.getRegexPattern(), "Regex pattern should be updated correctly.");
    }
    @Test
    void testSetRegexPatternToNull() {
        HttpSessionFilterConfiguration config = new HttpSessionFilterConfiguration();
        config.setRegexPattern(null);
        assertEquals("/.*", config.getRegexPattern(), "If set to null, regex pattern should revert to the default.");
    }
    @Test
    void testSetRegexPatternToEmptyString() {
        HttpSessionFilterConfiguration config = new HttpSessionFilterConfiguration();
        config.setRegexPattern("");
        assertEquals("", config.getRegexPattern(), "Regex pattern should accept an empty string if explicitly set.");
    }

}
