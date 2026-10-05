package club.sk1er.patcher.util.enhancement;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public interface Enhancement {

    AtomicInteger counter = new AtomicInteger(0);
    ThreadPoolExecutor POOL = new ThreadPoolExecutor(50, 50,
        0L, TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(),
        r -> {
            // Pride Edition: daemon, so these idle threads never keep the JVM alive after the game closes
            Thread thread = new Thread(r, String.format("Patcher Concurrency Thread %s", counter.incrementAndGet()));
            thread.setDaemon(true);
            return thread;
        });

    String getName();

    default void tick() {
    }
}
