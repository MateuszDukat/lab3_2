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
    private OrderItem item1;
    private OrderItem item2;
    private OrderItem item3;

    @BeforeEach
    void setUp() throws Exception {
        clock = Mockito.mock(Clock.class);
        order = new Order(clock);
        item1 = new OrderItem();
        item2 = new OrderItem();
        item3 = new OrderItem();
    }

    @Test
    void confirm25HoursAfterSubmissionShouldThrowException() {
        Instant now = Instant.now();
        Instant later = Instant.now().plus(Duration.ofHours(25));
        Mockito.when(clock.instant()).thenReturn(now).thenReturn(later);

        order.submit();
        Assertions.assertThrows(OrderExpiredException.class,()->{
            order.confirm();
        });
    }

    @Test
    void confirm24HoursAfterSubmissionShouldNotThrowException() {
        Instant now = Instant.now();
        Instant later = Instant.now().plus(Duration.ofHours(24));
        Mockito.when(clock.instant()).thenReturn(now).thenReturn(later);

        order.submit();
        Assertions.assertDoesNotThrow(order::confirm);
    }

    @Test
    void confirmWithoutSubmissionTest() {
        Assertions.assertThrows(OrderStateException.class,()->{
            order.confirm();
        });
    }

    @Test
    void statesTest(){

        Instant now = Instant.now();
        Mockito.when(clock.instant()).thenReturn(now).thenReturn(now);
        order.addItem(item1);
        order.addItem(item2);
        order.addItem(item3);
        Assertions.assertTrue(order.getOrderState()== Order.State.CREATED);
        order.submit();
        Assertions.assertTrue(order.getOrderState()== Order.State.SUBMITTED);
        order.confirm();
        Assertions.assertTrue(order.getOrderState()== Order.State.CONFIRMED);
        order.realize();
        Assertions.assertTrue(order.getOrderState()== Order.State.REALIZED);
    }

    @Test
    void statesTestWhileSubmittingAfterEachAddedItem(){
        Instant now = Instant.now();
        Mockito.when(clock.instant()).thenReturn(now).thenReturn(now);
        order.addItem(item1);
        order.submit();
        Assertions.assertTrue(order.getOrderState()== Order.State.SUBMITTED);
        order.addItem(item2);
        order.submit();
        Assertions.assertTrue(order.getOrderState()== Order.State.SUBMITTED);
        order.addItem(item3);
        Assertions.assertTrue(order.getOrderState()== Order.State.CREATED);
        order.submit();
        Assertions.assertTrue(order.getOrderState()== Order.State.SUBMITTED);
        order.confirm();
        Assertions.assertTrue(order.getOrderState()== Order.State.CONFIRMED);
        order.realize();
        Assertions.assertTrue(order.getOrderState()== Order.State.REALIZED);
    }

    @Test
    void timeGoingBackwardsTest() {
        Instant now = Instant.now();
        Instant later = Instant.now().plus(Duration.ofHours(-1));
        Mockito.when(clock.instant()).thenReturn(now).thenReturn(later);

        order.submit();
        Assertions.assertThrows(Exception.class,()->{
            order.confirm();
            //I assume there should be some Exception thrown here, but it could be that I'm just nitpicking
        });
    }

}
