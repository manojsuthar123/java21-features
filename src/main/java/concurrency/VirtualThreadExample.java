package concurrency;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * <p>
 * Some important points regarding Virtual Threads
 * 1. A virtual thread is an instance of java.lang.Thread
 * 2. We can create a very high number of virtual threads (millions) in an application without depending on the number of platform threads
 * 3. Virtual threads are managed by JVM
 * 4. Virtual thread consumes an OS thread only when it performs the calculations on the CPU
 * 5. Virtual threads are always daemon threads
 * 6. Virtual threads always have the normal priority and the priority cannot be changed
 * 7. Virtual threads are not active members of thread groups
 * 8. Virtual threads do not support the stop(), suspend(), or resume() methods
 * 9. Virtual threads are designed to be inherently thread-safe
 * <p/>
 *
 * @author manoj.suthar
 * @since 21
 */

public class VirtualThreadExample {

    public static void main(String[] args) {

        //Below are the ways to create Virtual threads

        // Method 1:
        Runnable runnable1 = () -> System.out.println("Inside Runnable");
        Thread.startVirtualThread(runnable1);

        // Method 2:
        Runnable runnable2 = () -> System.out.println("Inside Runnable");
        Thread virtualThread = Thread.ofVirtual().start(runnable2);

        //Method 3:
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, 10_000).forEach(i -> {
                executor.submit(() -> {
                    Thread.sleep(Duration.ofSeconds(1));
                    return i;
                });
            });
        }

        // Method 4:
        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            // long running task
            return "Result1";
        }, executorService);
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            // long running task
            return "Result2";
        }, executorService);
        future1.thenCombine(future2, (res1, res2) -> {
            return res1 + " " + res2;
        }).thenAccept(System.out::println);

        executorService.shutdown();

    }
}
