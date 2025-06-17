package io.micronaut.session.http

import io.micronaut.context.annotation.Property
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

import java.time.Duration
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAmount

@Property(name = 'micronaut.session.http.cookie-max-age', value = '365d')
@MicronautTest(startApplication = false)
class HttpSessionConfigurationSpec extends Specification {

    @Inject
    HttpSessionConfiguration configuration
    void "test configuring max age"() {
        given:
        long expected = 365L

        when:
        Optional<TemporalAmount> temporalAmountOptional = configuration.getCookieMaxAge()

        then:
        temporalAmountOptional.isPresent()

        when:
        TemporalAmount temporalAmount = temporalAmountOptional.get()
        long days = temporalAmount instanceof Duration ?
                ((Duration) temporalAmount).toDays() :
                temporalAmount.get(ChronoUnit.DAYS)

        then:
        expected == days
    }
}
