package io.micronaut.session;

import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.Property;
import io.micronaut.core.util.StringUtils;
import io.micronaut.session.http.HttpSessionFilter;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Property(name = "micronaut.session.filter.enabled", value = StringUtils.FALSE)
@MicronautTest(startApplication = false)
class HttpSessionFilterDisabledTest {
    @Inject
    BeanContext beanContext;

    @Test
    void filterCanBeDisabled() {
        assertFalse(beanContext.containsBean(HttpSessionFilter.class));
    }
}