/*
 * Copyright 2017 original authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package org.particleframework.session.http;

import org.particleframework.http.HttpRequest;

import java.util.List;

/**
 * Strategy interface for resolving {@link org.particleframework.session.Session} IDs
 *
 * @author Graeme Rocher
 * @since 1.0
 */
public interface HttpSessionIdResolver {

    /**
     * Resolve the Session ID from the given HTTP message
     *
     * @param message The session ID
     * @return An {@link java.util.Optional}
     */
    List<String> resolveIds(HttpRequest<?> message);
}
