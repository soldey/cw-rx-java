package main;

import main.observer.Observable;
import main.operators.*;
import main.schedulers.IOThreadScheduler;
import main.schedulers.ComputationScheduler;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Observable<Integer> source = Observable.just(1, 2, 3, 4, 5);
        System.out.println("Scenario 1: map + filter");
        FilterOperator.apply(
                MapOperator.apply(source, i -> i * 5),
                i -> i >= 15
            )
            .subscribeOn(new IOThreadScheduler())
            .observeOn(new ComputationScheduler())
            .subscribe(
                i -> System.out.println("Received: " + i),
                Throwable::printStackTrace,
                () -> System.out.println("Completed\n")
            );
        Thread.sleep(500);

        System.out.println("Scenario 2: flatMap");
        FlatMapOperator.apply(source, i ->
            Observable.just(i, i * i)
        ).subscribe(i -> System.out.println("flatMap: " + i));
        Thread.sleep(500);
    }
}