package main.operators;

import main.observer.Disposable;
import main.observer.Observable;
import main.observer.Observer;

import java.util.function.Predicate;

public class FilterOperator {
    public static <T> Observable<T> apply(
            Observable<T> source,
            Predicate<? super T> predicate
    ) {
        return Observable.create(observer -> {
            Disposable disposable = source.subscribe(new Observer<T>() {
                @Override
                public void onNext(T item) {
                    if (predicate.test(item)) {
                        observer.onNext(item);
                    }
                }
                @Override
                public void onError(Throwable t) {
                    observer.onError(t);
                }
                @Override
                public void onComplete() {
                    observer.onComplete();
                }
            });
        });
    }
}
