package main.schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ComputationScheduler implements Scheduler {
    private static final ExecutorService EXEC = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    @Override
    public void schedule(Runnable task) {
        EXEC.submit(task);
    }
}
