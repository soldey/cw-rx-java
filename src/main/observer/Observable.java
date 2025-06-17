package main.observer;

import main.schedulers.Scheduler;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class Observable<T> {
    private static final Logger logger = Logger.getLogger(Observable.class.getName());

    private final OnSubscribe<T> source;

    private Observable(OnSubscribe<T> source) {
        this.source = source;
    }

    public static <T> Observable<T> create(OnSubscribe<T> source) {
        logger.info("Observable: created");
        return new Observable<>(source);
    }

    public static <T> Observable<T> just(T item) {
        return create(observer -> {
            observer.onNext(item);
            observer.onComplete();
        });
    }

    @SafeVarargs
    public static <T> Observable<T> just(T... items) {
        return create(observer -> {
            Arrays.stream(items).forEach(observer::onNext);
            observer.onComplete();
        });
    }

    public Disposable subscribe(
            Consumer<? super T> onNext,
            Consumer<Throwable> onError,
            Runnable onComplete
    ) {
        Observer<T> obs = new Observer<T>() {
            @Override public void onNext(T item) {
                onNext.accept(item);
            }

            @Override public void onError(Throwable t) {
                onError.accept(t);
            }

            @Override public void onComplete() {
                onComplete.run();
            }
        };
        return subscribe(obs);
    }

    public Disposable subscribe(Consumer<? super T> onNext) {
        return subscribe(onNext, Throwable::printStackTrace, () -> {});
    }

    public Disposable subscribe(Observer<? super T> observer) {
        logger.info("Observable: new subscription");
        Disposable disposable = new Disposable();
        try {
            source.subscribe(new Observer<T>() {
                @Override
                public void onNext(T item) {
                    if (!disposable.isDisposed()) {
                        observer.onNext(item);
                    }
                }
                @Override
                public void onError(Throwable t) {
                    if (!disposable.isDisposed()) {
                        observer.onError(t);
                    }
                }
                @Override
                public void onComplete() {
                    if (!disposable.isDisposed()) {
                        observer.onComplete();
                    }
                }
            });
        } catch (Throwable t) {
            observer.onError(t);
        }
        return disposable;
    }

    public Observable<T> subscribeOn(Scheduler scheduler) {
        return Observable.create(observer ->
                scheduler.schedule(() -> this.subscribe(observer))
        );
    }

    public Observable<T> observeOn(Scheduler scheduler) {
        return Observable.create(observer ->
                this.subscribe(new Observer<T>() {
                    @Override
                    public void onNext(T item) {
                        scheduler.schedule(() -> observer.onNext(item));
                    }
                    @Override
                    public void onError(Throwable t) {
                        scheduler.schedule(() -> observer.onError(t));
                    }
                    @Override
                    public void onComplete() {
                        scheduler.schedule(observer::onComplete);
                    }
                })
        );
    }
}
