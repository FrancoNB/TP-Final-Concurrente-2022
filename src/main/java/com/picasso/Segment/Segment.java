package com.picasso.Segment;

import com.picasso.Data.Logger;
import com.picasso.Monitor.Monitor;

public class Segment implements Runnable {
    private final int[] transitions;
    private final Monitor monitor;
    private final String name;

    public Segment(String name, Monitor monitor, int[] transitions) {
        this.monitor = monitor;
        this.transitions = transitions;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                for (Integer t : transitions) {
                    monitor.fireTransition(t);
                    Logger.logTransition(String.format("FIRED -> T%-2d ON %-35s", t, Thread.currentThread().getName()));
                }
            }         
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();      
        }
        finally {
            Logger.logSystem(String.format("FINISHED -> %-35s", Thread.currentThread().getName()));
        }
    }
}
