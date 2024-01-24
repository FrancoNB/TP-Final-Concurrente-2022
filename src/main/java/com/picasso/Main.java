package com.picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.picasso.Monitor.Monitor;
import com.picasso.PetriNet.PetriNet;
import com.picasso.Policy.*;
import com.picasso.Segment.Segment;
import com.picasso.Data.Logger;

/**
 * Main class is used to run the program.
 */
public class Main {
    /**
     * Logs the statistics of the program.
     * @param monitor Monitor of the Petri net.
     */
    private static void logStatistics(Monitor monitor) {
        Logger.logStatistics("* ------------------------------ STATISTICS ------------------------------ *\n");

        Logger.logStatistics(String.format("- TIME EXECUTION -> %dms", System.currentTimeMillis() - Config.START_TIME));

        Logger.logStatistics("\n- TRANSITIONS FIRED");
        monitor.getTransitionsFiredCount().forEach((k, v) -> Logger.logStatistics(String.format("\tT%-2d -> %d", k, v)));
        Logger.logStatistics("\n- INVARIANTS FIRED");
        monitor.getInvariantsTransitionsFiredCount().forEach((k, v) -> Logger.logStatistics(String.format("\tINV %s -> %d", Arrays.toString(k), v)));

        Logger.logStatistics("\n* ------------------------------ STATISTICS ------------------------------ *");
    }

    /**
     * Executes the threads of the program for a certain time. Waits until all threads are finished and return.
     * @param threads List of threads to execute.
     */
    private static void execute(List<Thread> threads) {
        for (Thread thread : threads) {
            thread.start();
            Logger.logSystem(String.format("STARTED -> %-35s", thread.getName()));
        }

        Logger.logSystem("");

        try {
            TimeUnit.MILLISECONDS.sleep(Config.TIME_EXECUTION);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (Thread thread : threads)
            thread.interrupt();

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Fires the final transitions of the Petri to return to the initial state.
     * @param monitor Monitor of the Petri net.
     */
    private static void finished(Monitor monitor) {
        while (true) {
            boolean end = true;

            for(int t : Config.FINAL_TRANSITIONS) {
                if (monitor.fireTransition(t)) {
                    Logger.logTransition(String.format("FIRED -> T%-2d ON [%s]", t, Thread.currentThread().getName()));

                    Config.SEGMENT_TRANSITIONS.forEach(segment -> {
                        if (segment.containsKey(t))
                            if (segment.get(t) != null)
                                segment.get(t).work();
                    });

                    end = false;
                }
            }

            if (end)
                break;
        }
    }

    /**
     * Main method of the program.
     * @param args not used.
     */
    public static void main(String[] args) {
        Logger.logSystem("* ------------------------------- PICASSO ------------------------------- *\n");

        PetriNet petriNet = new PetriNet("PetriNet", Config.INITIAL_MARKING, Config.INCIDENCE_MATRIX, Config.NUMBER_OF_PLACES, Config.NUMBER_OF_TRANSITIONS);
        Policy policy = new PolicyMinTransitions();
        Monitor monitor = new Monitor(petriNet, policy, Config.INVARIANTS_TRANSITIONS);
        List<Thread> threads = new ArrayList<Thread>();

        Segment[] segments = {
            new Segment("A", monitor, Config.SEGMENT_TRANSITIONS.get(0)),
            new Segment("B", monitor, Config.SEGMENT_TRANSITIONS.get(1)),
            new Segment("C", monitor, Config.SEGMENT_TRANSITIONS.get(2)),
            new Segment("D", monitor, Config.SEGMENT_TRANSITIONS.get(3)),
            new Segment("E", monitor, Config.SEGMENT_TRANSITIONS.get(4))
        };

        for (int i = 0; i < Config.SEGMENT_TRANSITIONS.size(); i++)
            for (int j = 0; j < Config.SEGMENT_THREADS.get(i); j++)
                threads.add(new Thread(segments[i], "[Segment " + segments[i].getName() + " - Thread " + j + "]"));

        petriNet.setPlaceName("P16", "CS1");
        petriNet.setPlaceName("P17", "CS2");
        petriNet.setPlaceName("P18", "CS3");

        petriNet.setTransitionTime("T4", Config.TIMED_TRANSITIONS.get(0)[0], Config.TIMED_TRANSITIONS.get(0)[1]);
        petriNet.setTransitionTime("T5", Config.TIMED_TRANSITIONS.get(1)[0], Config.TIMED_TRANSITIONS.get(1)[1]);
        petriNet.setTransitionTime("T6", Config.TIMED_TRANSITIONS.get(2)[0], Config.TIMED_TRANSITIONS.get(2)[1]);
        petriNet.setTransitionTime("T7", Config.TIMED_TRANSITIONS.get(3)[0], Config.TIMED_TRANSITIONS.get(3)[1]);
        petriNet.setTransitionTime("T8", Config.TIMED_TRANSITIONS.get(4)[0], Config.TIMED_TRANSITIONS.get(4)[1]);
        petriNet.setTransitionTime("T10", Config.TIMED_TRANSITIONS.get(5)[0], Config.TIMED_TRANSITIONS.get(5)[1]);
        petriNet.setTransitionTime("T11", Config.TIMED_TRANSITIONS.get(6)[0], Config.TIMED_TRANSITIONS.get(6)[1]);
        petriNet.setTransitionTime("T12", Config.TIMED_TRANSITIONS.get(7)[0], Config.TIMED_TRANSITIONS.get(7)[1]);

        execute(threads);

        finished(monitor);

        logStatistics(monitor);

        Logger.logSystem("\n* ------------------------------- PICASSO ------------------------------- *");

        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Logger.shutdown();
    }
}
