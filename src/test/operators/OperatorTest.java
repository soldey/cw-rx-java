package test.operators;
import main.observer.Observable;
import main.operators.FilterOperator;
import main.operators.FlatMapOperator;
import main.operators.MapOperator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OperatorTest {

    @Test
    void testMapAndFilter() {
        Observable<Integer> src = Observable.create(o -> {
            o.onNext(1);
            o.onNext(2);
            o.onNext(3);
            o.onComplete();
        });

        List<Integer> result = new ArrayList<>();
        Observable<Integer> mapped = MapOperator.apply(src, i -> i * 5);
        Observable<Integer> filtered = FilterOperator.apply(mapped, i -> i > 10);
        filtered.subscribe(result::add);

        assertEquals(List.of(20, 30), result);
    }

    @Test
    void testFlatMap() {
        Observable<Integer> src = Observable.<Integer>create(o -> {
            o.onNext(1);
            o.onNext(2);
            o.onComplete();
        });

        List<Integer> out = new ArrayList<>();
        Observable<Integer> flat = FlatMapOperator.apply(src, i ->
                Observable.just(i, i * 10)
        );
        flat.subscribe(out::add);

        assertEquals(4, out.size());
        assertTrue(out.containsAll(List.of(1, 10, 2, 20)));
    }
}
