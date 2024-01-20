package Segment;

import Monitor.Monitor;

public class Segment implements Runnable {
    private final int[] transitions;
    private final Monitor monitor;
    private final String name;
    private boolean isInterrupted;

    public Segment(String name, Monitor monitor, int[] transitions) {
        this.monitor = monitor;
        this.transitions = transitions;
        this.name = name;
        isInterrupted = false;
    }

    public String getName() {
        return name;
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
