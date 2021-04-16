package edu.iis.mto.time;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;


@ExtendWith(MockitoExtension.class)
class OrderTest {

    @Mock
    private Clock clock;

    private Order order;

    @BeforeEach
    void setUp() throws Exception {
        clock = Mockito.mock(Clock.class);
        order = new Order(clock);
    }

    @Test
    void confirm25HoursAfterSubmitionShouldThrowException() {
        Instant now = Instant.now();
        Instant later = Instant.now().plus(Duration.ofHours(25));
        Mockito.when(clock.instant()).thenReturn(now).thenReturn(later);

        order.submit();
        Assertions.assertThrows(OrderExpiredException.class,()->{
            order.confirm();
        });


    }

}
