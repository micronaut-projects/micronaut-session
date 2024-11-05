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

/**
 * Configuration properties for the HttpSessionFilter.
 *
 * This class contains settings for the session filter, including
 * a regex pattern to define which paths should have session handling applied.
 */
@Internal
@ConfigurationProperties("http.session.filter")
public  class HttpSessionFilterConfiguration {

    /**
     * The regex pattern for filtering paths that should have session handling.
     */
    private String regexPattern = "/.*";

    /**
     * Gets the regex pattern for filtering paths that should have session handling.
     *
     * Subclasses may override this method to provide a different regex pattern.
     * However, care should be taken to ensure that the new pattern remains compatible
     * with the intended usage of session handling.
     *
     * @return The regex pattern for filtering paths.
     */
    public String getRegexPattern() {
        return regexPattern;
    }

    /**
     * Sets the regex pattern for filtering paths that should have session handling.
     *
     * @param regexPattern The regex pattern to apply for filtering paths.
     *                     Default is "/.*", which matches all paths.
     */
    public void setRegexPattern(String regexPattern) {
        this.regexPattern = (regexPattern != null) ? regexPattern : "/.*";

    }
}
