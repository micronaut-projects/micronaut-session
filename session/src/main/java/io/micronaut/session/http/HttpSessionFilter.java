/*
 * Copyright 2017-2020 original authors
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

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.order.Ordered;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.RequestFilter;
import io.micronaut.http.annotation.ResponseFilter;
import io.micronaut.http.annotation.ServerFilter;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.http.filter.ServerFilterPhase;
import io.micronaut.http.server.exceptions.InternalServerException;
import io.micronaut.inject.MethodExecutionHandle;
import io.micronaut.session.Session;
import io.micronaut.session.SessionStore;
import io.micronaut.session.annotation.SessionValue;
import io.micronaut.web.router.MethodBasedRouteInfo;
import io.micronaut.web.router.RouteInfo;
import jakarta.inject.Inject;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.regex.Pattern;

/**
 * A server filter that resolves the current user {@link Session} if present and encodes the Session ID in
 * the response.
 *
 * @author Graeme Rocher
 * @since 1.0
 */
@Requires(property = HttpSessionFilterConfigurationProperties.PROPERTY_ENABLED, notEquals = StringUtils.FALSE, defaultValue = StringUtils.TRUE)
@ServerFilter("/**")
public class HttpSessionFilter implements Ordered {
    /**
     * The order of the filter.
     */
    public static final Integer ORDER = ServerFilterPhase.SESSION.order();

    /**
     * Constant for Micronaut SESSION attribute.
     */
    public static final CharSequence SESSION_ATTRIBUTE = "micronaut.SESSION";

    private final SessionStore<Session> sessionStore;
    private final HttpSessionIdResolver[] resolvers;
    private final HttpSessionIdEncoder[] encoders;
    private final @Nullable Pattern regexPattern;

    /**
     * Constructor.
     *
     * @param sessionStore The session store
     * @param resolvers The HTTP session id resolvers
     * @param encoders The HTTP session id encoders
     */
    public HttpSessionFilter(SessionStore<Session> sessionStore,
                             HttpSessionIdResolver[] resolvers,
                             HttpSessionIdEncoder[] encoders) {
        this(sessionStore, resolvers, encoders, new HttpSessionFilterConfigurationProperties());
    }

    /**
     * Constructor.
     *
     * @param sessionStore The session store
     * @param resolvers The HTTP session id resolvers
     * @param encoders The HTTP session id encoders
     * @param configuration The filter configuration
     */
    @Inject
    public HttpSessionFilter(SessionStore<Session> sessionStore,
                             HttpSessionIdResolver[] resolvers,
                             HttpSessionIdEncoder[] encoders,
                             HttpSessionFilterConfiguration configuration) {
        this.sessionStore = sessionStore;
        this.resolvers = resolvers;
        this.encoders = encoders;
        this.regexPattern = HttpSessionFilterConfigurationProperties.DEFAULT_REGEX_PATTERN.equals(configuration.getRegexPattern())
            ? null
            : Pattern.compile(configuration.getRegexPattern());
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

    /**
     * Resolve an existing HTTP session before the matched route is invoked.
     *
     * @param request The request
     * @return The request to continue with
     */
    @RequestFilter
    public CompletionStage<HttpRequest<?>> filterRequest(HttpRequest<?> request) {
        if (!matchesFilter(request)) {
            return CompletableFuture.completedFuture(request);
        }
        return loadSessionIntoRequest(request);
    }

    /**
     * Persist and encode the HTTP session after the matched route has produced a response.
     *
     * @param request The request
     * @param response The response
     * @param routeInfo The matched route information, if any
     * @return The response to continue with
     */
    @ResponseFilter
    public CompletionStage<MutableHttpResponse<?>> filterResponse(HttpRequest<?> request,
                                                                  MutableHttpResponse<?> response,
                                                                  @Nullable RouteInfo<?> routeInfo) {
        if (!isFilterApplied(request)) {
            return CompletableFuture.completedFuture(response);
        }
        MethodExecutionHandle<?, ?> routeMatch = routeInfo instanceof MethodBasedRouteInfo<?, ?> methodBasedRouteInfo
            ? methodBasedRouteInfo.getTargetMethod()
            : null;
        return encodeSessionInResponse(request, response, routeMatch);
    }

    private boolean matchesFilter(HttpRequest<?> request) {
        return regexPattern == null || regexPattern.matcher(request.getPath()).matches();
    }

    private boolean isFilterApplied(HttpRequest<?> request) {
        return request.getAttribute(HttpSessionFilter.class.getName(), Boolean.class).orElse(false);
    }

    private CompletionStage<HttpRequest<?>> loadSessionIntoRequest(HttpRequest<?> request) {
        request.setAttribute(HttpSessionFilter.class.getName(), true);
        try {
            String id = findSessionId(request);
            if (id != null) {
                return sessionStore.findSession(id)
                    .thenApply(session -> {
                        session.ifPresent(entries -> request.getAttributes().put(SESSION_ATTRIBUTE, entries));
                        return request;
                    });
            }
        } catch (IllegalArgumentException e) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return CompletableFuture.completedFuture(request);
    }

    private @Nullable String findSessionId(HttpRequest<?> request) {
        for (HttpSessionIdResolver resolver : resolvers) {
            List<String> ids = resolver.resolveIds(request);
            if (CollectionUtils.isNotEmpty(ids)) {
                return ids.getFirst();
            }
        }
        return null;
    }

    private CompletionStage<MutableHttpResponse<?>> encodeSessionInResponse(HttpRequest<?> request,
                                                                            MutableHttpResponse<?> response,
                                                                            @Nullable MethodExecutionHandle<?, ?> routeMatch) {
        Optional<?> body = response.getBody();
        String sessionAttr = getSessionAttr(routeMatch, body.isPresent());

        Optional<Session> opt = request.getAttributes().get(SESSION_ATTRIBUTE, Session.class);
        if (opt.isPresent()) {
            Session session = opt.get();
            if (sessionAttr != null) {
                session.put(sessionAttr, body.get());
            }

            if (session.isNew() || session.isModified()) {
                return saveAndEncodeSession(request, response, session);
            }
        } else if (sessionAttr != null) {
            Session newSession = sessionStore.newSession();
            newSession.put(sessionAttr, body.get());
            return saveAndEncodeSession(request, response, newSession);
        }
        encodeSessionId(request, response, opt);
        return CompletableFuture.completedFuture(response);
    }

    private static @Nullable String getSessionAttr(@Nullable MethodExecutionHandle<?, ?> routeMatch, boolean hasBody) {
        String sessionAttr;
        if (hasBody) {
            sessionAttr = Optional.ofNullable(routeMatch).flatMap(m -> {
                if (!m.hasAnnotation(SessionValue.class)) {
                    return Optional.empty();
                } else {
                    String attributeName = m.stringValue(SessionValue.class).orElse(null);
                    if (!StringUtils.isEmpty(attributeName)) {
                        return Optional.of(attributeName);
                    } else {
                        throw new InternalServerException("@SessionValue on a return type must specify an attribute name");
                    }
                }
            }).orElse(null);
        } else {
            sessionAttr = null;
        }
        return sessionAttr;
    }

    private CompletionStage<MutableHttpResponse<?>> saveAndEncodeSession(HttpRequest<?> request,
                                                                         MutableHttpResponse<?> response,
                                                                         Session session) {
        return sessionStore.save(session)
            .thenApply(savedSession -> {
                encodeSessionId(request, response, Optional.of(savedSession));
                return response;
            });
    }

    private void encodeSessionId(HttpRequest<?> request,
                                 MutableHttpResponse<?> response,
                                 Optional<Session> session) {
        session.ifPresent(s -> {
            for (HttpSessionIdEncoder encoder : encoders) {
                encoder.encodeId(request, response, s);
            }
        });
    }
}
