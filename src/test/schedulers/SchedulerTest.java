package test.schedulers;
import main.observer.Observable;
import main.schedulers.IOThreadScheduler;
import main.schedulers.SingleScheduler;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class SchedulerTest {

    @Test
    void testIOThreadScheduler() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> threadName = new AtomicReference<>();

        Observable.just("A")
                .subscribeOn(new IOThreadScheduler())
                .subscribe(item -> {
                    threadName.set(Thread.currentThread().getName());
                    latch.countDown();
                });

        assertTrue(latch.await(1, TimeUnit.SECONDS));
        assertTrue(threadName.get().contains("pool"));
    }

    @Test
    void testSingleScheduler() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(2);
        AtomicReference<String> first = new AtomicReference<>();
        AtomicReference<String> second = new AtomicReference<>();

        Observable.just("A")
                .observeOn(new SingleScheduler())
                .subscribe(item -> {
                    first.set(Thread.currentThread().getName());
                    latch.countDown();
                });

        Observable.just("B")
                .observeOn(new SingleScheduler())
                .subscribe(item -> {
                    second.set(Thread.currentThread().getName());
                    latch.countDown();
                });

        assertTrue(latch.await(1, TimeUnit.SECONDS));
        assertEquals(first.get(), second.get());
    }
}
