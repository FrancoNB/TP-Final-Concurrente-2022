package Segment;

import Monitor.Monitor;

public class Segment implements Runnable {
    private final int[] transitions;
    private final Monitor monitor;
    private boolean isInterrupted;

    public Segment(Monitor monitor, int[] transitions) {
        this.monitor = monitor;
        this.transitions = transitions;
        isInterrupted = false;
    }

    public boolean isInterrupted() {
        return isInterrupted;
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted())
                for (Integer t : transitions)
                    monitor.fireTransition(t);
        }
        catch (RuntimeException e) {
            isInterrupted = true;
        }
    }
}
