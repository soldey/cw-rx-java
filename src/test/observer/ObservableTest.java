package test.observer;

import main.observer.Observable;
import main.schedulers.IOThreadScheduler;
import main.schedulers.SingleScheduler;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class ObservableTest {

    @Test
    void testCreateAndSubscribe() {
        List<String> received = new ArrayList<>();
        Observable<String> source = Observable.create(observer -> {
            observer.onNext("A");
            observer.onNext("B");
            observer.onComplete();
        });

        source.subscribe(received::add,
                Throwable::printStackTrace,
                () -> received.add("DONE"));

        assertEquals(List.of("A", "B", "DONE"), received);
    }

    @Test
    void testSubscribeOnScheduler() throws InterruptedException {
        AtomicReference<String> threadName = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        Observable.just("A")
                .subscribeOn(new IOThreadScheduler())
                .subscribe(item -> {
                    threadName.set(Thread.currentThread().getName());
                    latch.countDown();
                });

        assertTrue(latch.await(1, TimeUnit.SECONDS));
        assertNotEquals(Thread.currentThread().getName(), threadName.get());
    }

    @Test
    void testObserveOnScheduler() throws InterruptedException {
        AtomicReference<String> threadName = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        Observable.just("B")
                .observeOn(new SingleScheduler())
                .subscribe(item -> {
                    threadName.set(Thread.currentThread().getName());
                    latch.countDown();
                });

        assertTrue(latch.await(1, TimeUnit.SECONDS));
        assertTrue(threadName.get().startsWith("pool-"));
    }
}