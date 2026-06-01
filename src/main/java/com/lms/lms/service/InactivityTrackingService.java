package com.lms.lms.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Tracks the timestamp of the most recent HTTP request and shuts the
 * application down gracefully when no request has been received for
 * {@value #INACTIVITY_THRESHOLD_MS} milliseconds (10 seconds).
 *
 * <p>The last-request timestamp is updated by {@link com.lms.lms.filter.InactivityFilter}
 * on every incoming HTTP request. A {@link Scheduled} task polls every second
 * and triggers a graceful {@link ConfigurableApplicationContext#close()} when
 * the idle window is exceeded.</p>
 */
@Slf4j
@Service
public class InactivityTrackingService {

    /** Idle threshold in milliseconds (10 seconds). */
    static final long INACTIVITY_THRESHOLD_MS = 10_000L;

    private final AtomicLong lastRequestTime = new AtomicLong(System.currentTimeMillis());
    private final ConfigurableApplicationContext applicationContext;

    public InactivityTrackingService(ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Called by the request filter on every incoming HTTP request to reset
     * the inactivity clock.
     */
    public void recordRequest() {
        lastRequestTime.set(System.currentTimeMillis());
    }

    /**
     * Scheduled check that runs every second. If the application has been
     * idle for longer than {@value #INACTIVITY_THRESHOLD_MS} ms the context
     * is closed, which triggers Spring's graceful-shutdown hooks and allows
     * in-flight requests to complete before the JVM exits.
     */
    @Scheduled(fixedDelay = 1_000L)
    public void checkInactivity() {
        long idleMs = System.currentTimeMillis() - lastRequestTime.get();
        if (idleMs >= INACTIVITY_THRESHOLD_MS) {
            log.warn("No HTTP requests received for {} ms — initiating graceful shutdown.", idleMs);
            // Close the Spring context on a separate daemon thread so the
            // scheduler thread itself is not blocked waiting for shutdown.
            Thread shutdownThread = new Thread(() -> {
                applicationContext.close();
            }, "inactivity-shutdown");
            shutdownThread.setDaemon(false);
            shutdownThread.start();
        }
    }
}
