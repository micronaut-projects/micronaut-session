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
import io.micronaut.core.annotation.NonNull;
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
    public static final String PATH_PROPERTY = PREFIX + ".path";
    public static final String DEFAULT_PATH = "/**";
    private String path = DEFAULT_PATH;

    @Override
    @NonNull
    public String getPath() {
        return path;
    }

    /**
     * Pattern the {@link HttpSessionFilter} should match. Default value {@value #DEFAULT_PATH}.
     *
     * @param path the exclude pattern to set
     */
    public void setPath(String path) {
        this.path = path;
    }
}
