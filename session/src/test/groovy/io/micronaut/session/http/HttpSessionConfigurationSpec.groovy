package io.micronaut.session.http

import io.micronaut.context.ApplicationContext
import spock.lang.Specification

import java.time.Duration
import java.time.temporal.ChronoUnit

class HttpSessionConfigurationSpec extends Specification {

    void "test configuring max age"() {
        given:
        ApplicationContext ctx = ApplicationContext.run('micronaut.session.http.cookie-max-age': '365d')

        expect:
        Duration.from(ctx.getBean(HttpSessionConfiguration)
            .getCookieMaxAge()
            .get()
            ).getSeconds() == 365 * 24 * 60 * 60

        cleanup:
        ctx.close()
    }
}
