package main.observer;

@FunctionalInterface
public interface OnSubscribe<T> {
    void subscribe(Observer<? super T> observer);
}
