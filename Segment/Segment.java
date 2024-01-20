package Segment;

import java.util.concurrent.atomic.AtomicInteger;

import Monitor.Monitor;

public class Segment implements Runnable {
    private final int[] invariantsTransitions;
    private final Monitor monitor;
    private boolean isInterrupted;
    private AtomicInteger executedCiclesCount;
    
    protected Segment(Monitor monitor, int[] invariantsTransitions) {
        this.monitor = monitor;
        this.invariantsTransitions = invariantsTransitions;
        isInterrupted = false;
        executedCiclesCount = new AtomicInteger(0);
    }

    protected boolean isInterrupted() {
        return isInterrupted;
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                for (Integer t : invariantsTransitions)
                    monitor.fireTransition(t);

                executedCiclesCount.incrementAndGet();
            }
        }
        catch (RuntimeException e) {
            isInterrupted = true;
        }
    }
}
