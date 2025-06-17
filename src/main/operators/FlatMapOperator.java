package main.operators;

import main.observer.CompositeDisposable;
import main.observer.Disposable;
import main.observer.Observable;
import main.observer.Observer;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class FlatMapOperator {
    public static <T, R> Observable<R> apply(
            Observable<T> source,
            Function<? super T, Observable<? extends R>> mapper
    ) {
        return Observable.create(observer -> {
            CompositeDisposable composite = new CompositeDisposable();
            AtomicInteger activeCount = new AtomicInteger(1);
            ConcurrentLinkedQueue<Throwable> errors = new ConcurrentLinkedQueue<>();

            Disposable parentDisposable = source.subscribe(new Observer<T>() {
                @Override
                public void onNext(T item) {
                    activeCount.incrementAndGet();
                    Disposable innerDisposable = mapper.apply(item)
                            .subscribe(new Observer<R>() {
                                @Override
                                public void onNext(R inner) {
                                    observer.onNext(inner);
                                }
                                @Override
                                public void onError(Throwable t) {
                                    errors.add(t);
                                    completeIfDone();
                                }
                                @Override
                                public void onComplete() {
                                    completeIfDone();
                                }
                            });
                    composite.add(innerDisposable);
                }

                @Override
                public void onError(Throwable t) {
                    errors.add(t);
                    completeIfDone();
                }

                @Override
                public void onComplete() {
                    completeIfDone();
                }

                private void completeIfDone() {
                    if (activeCount.decrementAndGet() == 0) {
                        Throwable err = errors.poll();
                        if (err != null) {
                            observer.onError(err);
                        } else {
                            observer.onComplete();
                        }
                        composite.dispose();
                    }
                }
            });

            composite.add(parentDisposable);
        });
    }
}
