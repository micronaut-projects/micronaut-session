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

/**
 * Configuration class for the HTTP session filter.
 * <p>
 * This class allows configuration of the pattern for excluding certain paths
 * from session filtering in Micronaut applications.
 * </p>
 */
@ConfigurationProperties("micronaut.session.filter")
public class HttpSessionFilterConfiguration {
    private String excludePattern = "/**";

    /**
     * Returns the exclude pattern used to filter certain paths from session handling.
     * <p>
     * Subclasses can override this method to provide a different exclude pattern if needed.
     * Be sure that the pattern follows the expected syntax and does not conflict with
     * other filters or session configurations.
     * </p>
     *
     * @return the exclude pattern
     */
    public String getExcludePattern() {
        return excludePattern;
    }

    /**
     * Sets the exclude pattern used to filter certain paths from session handling.
     * <p>
     * This method can be overridden by subclasses to customize the exclude pattern.
     * When overriding this method, ensure that the pattern is compatible with other
     * session filtering configurations, and that it does not conflict with other filters.
     * </p>
     *
     * @param excludePattern the exclude pattern to set
     */
    public void setExcludePattern(String excludePattern) {
        this.excludePattern = excludePattern;
    }
}
