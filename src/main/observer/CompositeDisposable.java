package main.observer;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CompositeDisposable {
    private final Set<Disposable> disposables = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public void add(Disposable d) {
        disposables.add(d);
    }

    public void remove(Disposable d) {
        disposables.remove(d);
    }

    public void dispose() {
        for (Disposable d : disposables) {
            d.dispose();
        }
        disposables.clear();
    }

    public boolean isDisposed() {
        return disposables.stream().allMatch(Disposable::isDisposed);
    }
}
