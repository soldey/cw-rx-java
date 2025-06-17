package main.observer;

import java.util.concurrent.atomic.AtomicBoolean;

public class Disposable {
    private final AtomicBoolean disposed = new AtomicBoolean(false);

    public void dispose() {
        disposed.set(true);
    }

    public boolean isDisposed() {
        return disposed.get();
    }
}
