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
package io.micronaut.session;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.session.event.SessionCreatedEvent;
import jakarta.inject.Singleton;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SessionEventListenerRegistrationTest {

    @Test
    void concreteSessionListenersReceivePublishedEvents() throws Exception {
        try (ApplicationContext applicationContext = ApplicationContext.run()) {
            InMemorySessionStore sessionStore = applicationContext.getBean(InMemorySessionStore.class);
            ConcreteCreatedListener concreteCreatedListener = applicationContext.getBean(ConcreteCreatedListener.class);
            AnnotatedSessionListener annotatedSessionListener = applicationContext.getBean(AnnotatedSessionListener.class);
            InMemorySession session = sessionStore.newSession();

            session.put("foo", "bar");
            sessionStore.save(session).get();

            assertEquals(List.of(session.getId()), concreteCreatedListener.eventIds());
            assertEquals(List.of(session.getId()), annotatedSessionListener.eventIds());
        }
    }

    @Singleton
    static final class ConcreteCreatedListener implements ApplicationEventListener<SessionCreatedEvent> {
        private final List<String> eventIds = new ArrayList<>();

        @Override
        public void onApplicationEvent(SessionCreatedEvent event) {
            eventIds.add(event.getSource().getId());
        }

        List<String> eventIds() {
            return eventIds;
        }
    }

    @Singleton
    static final class AnnotatedSessionListener {
        private final List<String> eventIds = new ArrayList<>();

        @EventListener
        void onSessionCreated(SessionCreatedEvent event) {
            eventIds.add(event.getSource().getId());
        }

        List<String> eventIds() {
            return eventIds;
        }
    }
}
