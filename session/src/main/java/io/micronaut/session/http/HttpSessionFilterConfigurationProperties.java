/*
 * Copyright 2017-2024 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.session.http;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.core.annotation.Internal;
import org.jspecify.annotations.NonNull;
import io.micronaut.session.SessionSettings;

/**
 * @author Sergio del Amo
 * @since 4.6.0
 */
@ConfigurationProperties(HttpSessionFilterConfigurationProperties.PREFIX)
@Internal
class HttpSessionFilterConfigurationProperties implements HttpSessionFilterConfiguration {
    /**
     * {@link HttpSessionFilterConfigurationProperties} prefix.
     */
    @SuppressWarnings("WeakerAccess")
    public static final String PREFIX = SessionSettings.PREFIX + ".filter";

    /**
     * The default enable value.
     */
    @SuppressWarnings("WeakerAccess")
    public static final boolean DEFAULT_ENABLED = true;
    public static final String PROPERTY_ENABLED = PREFIX + ".enabled";
    public static final String PROPERTY_REGEX_PATTERN = PREFIX + ".regex-pattern";

    /**
     * The default regex pattern.
     */
    @SuppressWarnings("WeakerAccess")
    public static final String  DEFAULT_REGEX_PATTERN = "^.*$";

    private String regexPattern = DEFAULT_REGEX_PATTERN;
    private boolean enabled = DEFAULT_ENABLED;

    @Override
    @NonNull
    public String getRegexPattern() {
        return regexPattern;
    }

    /**
     * Pattern the {@link HttpSessionFilter} should match. Default value {@value #DEFAULT_REGEX_PATTERN}.
     *
     * @param regexPattern the exclude pattern to set
     */
    public void setRegexPattern(String regexPattern) {
        this.regexPattern = regexPattern;
    }

    /**
     * Whether the {@link HttpSessionFilter} is enabled. Default value {@value #DEFAULT_ENABLED}
     * @return true if you want to enable the {@link HttpSessionFilter}
     */
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Enables {@link HttpSessionFilter}. Default value {@value #DEFAULT_ENABLED}
     * @param enabled True if it is enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
