package com.picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.picasso.Monitor.Monitor;
import com.picasso.PetriNet.PetriNet;
import com.picasso.Policy.*;
import com.picasso.Segment.Segment;
import com.picasso.Data.Logger;
import com.picasso.Artist.*;

/**
 * Main class is used to run the program.
 */
public class Main {
    // Start time of the program.
    public static final long startTime = System.currentTimeMillis();
    // Number of places of the Petri net.
    private static final int NUMBER_OF_PLACES = 18;
    // Number of transitions of the Petri net.
    private static final int NUMBER_OF_TRANSITIONS = 12;

    // Initial marking of the Petri net.
    private static final int[] INITIAL_MARKING = {0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 4, 2, 2, 3, 1, 4, 6, 3};

    // Incidence matrix of the Petri net.
    private static final int[][] INCIDENCE_MATRIX = { /*Matriz de incidencia*/
        //T1  T2  T3  T4  T5  T6  T7  T8  T9 T10 T11 T12
        {  1, -1, -1,  0,  0,  0,  0,  0,  0,  0,  0,  0}, //P1
        {  0,  1,  0, -1,  0,  0,  0,  0,  0,  0,  0,  0}, //P2
        {  0,  0,  1,  0, -1,  0,  0,  0,  0,  0,  0,  0}, //P3
        {  0,  0,  0,  1,  0, -1,  0,  0,  0,  0,  0,  0}, //P4
        {  0,  0,  0,  0,  1,  0, -1,  0,  0,  0,  0,  0}, //P5
        {  0,  0,  0,  0,  0,  1,  1, -1,  0,  0,  0,  0}, //P6
        {- 1,  0,  0,  0,  0,  0,  0,  1,  0,  0,  0,  0}, //P7
        {  0,  0,  0,  0,  0,  0,  0,  0,  1, -1,  0,  0}, //P8
        {  0,  0,  0,  0,  0,  0,  0,  0,  0,  1, -1,  0}, //P9
        {  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  1, -1}, //P10
        {  0,  0,  0,  0,  0,  0,  0,  0, -1,  0,  0,  1}, //P11
        {- 1,  1,  1,  0,  0,  0,  0,  0,  0,  0, -1,  1}, //P12
        {  0, -1, -1,  1,  1,  0,  0,  0,  0, -1,  1,  0}, //P13
        {  0,  0,  0, -1, -1,  1,  1,  0, -1,  1,  0,  0}, //P14
        {  0,  0,  0,  0,  0, -1, -1,  1,  0,  0,  0,  0}, //P15
        {  0, -1, -1,  1,  1,  0,  0,  0, -1,  1,  0,  0}, //CS1
        { -1,  0,  0,  1,  1,  0,  0,  0, -1,  0,  1,  0}, //CS2
        { -1,  1,  1,  0,  0,  0,  0,  0,  0, -1,  1,  0}, //CS3
    };

    // Transitions of each invariant.
    private static final List<int[]> INVARIANTS_TRANSITIONS = List.of(
        new int[]{1, 2, 4, 6, 8},
        new int[]{1, 3, 5, 7, 8},
        new int[]{9, 10, 11, 12}
    );

    // Transitions and artists of each segment.
    private static final List<Map<Integer, Artist>> SEGMENT_TRANSITIONS = List.of(
        new HashMap<Integer, Artist>() { 
            { put(1, null); }
        },
        new HashMap<Integer, Artist>() { 
            { put(2, null); put(4, new Diffuser()); put(6, new RGBPainter()); }
        },
        new HashMap<Integer, Artist>() { 
            { put(3, null); put(5, new SuperPositioner()); put(7, new BWPainter()); }
        },
        new HashMap<Integer, Artist>() { 
            { put(8, new Compressor()); }
        },
        new HashMap<Integer, Artist>() { 
            { put(9, null); put(10, new PainterPattern()); put(11, new FilterPattern()); put(12, new TemplatePattern()); }
        }
    );

    // Time alpha and beta of each timed transition.
    private static final List<int[]> TIMED_TRANSITIONS = List.of(
        new int[]{3, 43},   // T4
        new int[]{5, 45},   // T5
        new int[]{19, 69},  // T6
        new int[]{7, 67},   // T7
        new int[]{26, 106},  // T8
        new int[]{9, 19},    // T10
        new int[]{8, 28},   // T11
        new int[]{12, 32}    // T12
    );

    // Number of threads of each segment.
    private static final List<Integer> SEGMENT_THREADS = List.of(2, 4, 4, 1, 4);

    // Time of execution until interrupting threads.
    private static final int TIME_EXECUTION = 60000;

    /**
     * Logs the statistics of the program.
     * @param monitor Monitor of the Petri net.
     */
    private static void logStatistics(Monitor monitor) {
        Logger.logStatistics("* ------------------------------ STATISTICS ------------------------------ *\n");

        Logger.logStatistics(String.format("- TIME EXECUTION -> %dms", TIME_EXECUTION));

        Logger.logStatistics("\n- TRANSITIONS FIRED");
        monitor.getTransitionsFiredCount().forEach((k, v) -> Logger.logStatistics(String.format("\tT%-2d -> %d", k, v)));
        Logger.logStatistics("\n- INVARIANTS FIRED");
        monitor.getInvariantsTransitionsFiredCount().forEach((k, v) -> Logger.logStatistics(String.format("\tINV %s -> %d", Arrays.toString(k), v)));

        Logger.logStatistics("\n* ------------------------------ STATISTICS ------------------------------ *");
    }

    /**
     * Main method of the program.
     * @param args not used.
     */
    public static void main(String[] args) {
        Logger.logSystem("* ------------------------------- PICASSO ------------------------------- *\n");

        PetriNet petriNet = new PetriNet("PetriNet", INITIAL_MARKING, INCIDENCE_MATRIX, NUMBER_OF_PLACES, NUMBER_OF_TRANSITIONS);
        Policy policy = new PolicyMinTransitions();
        Monitor monitor = new Monitor(petriNet, policy, INVARIANTS_TRANSITIONS);
        List<Thread> threads = new ArrayList<Thread>();

        Segment[] segments = {
            new Segment("A", monitor, SEGMENT_TRANSITIONS.get(0)  ),
            new Segment("B", monitor, SEGMENT_TRANSITIONS.get(1)),
            new Segment("C", monitor, SEGMENT_TRANSITIONS.get(2)),
            new Segment("D", monitor, SEGMENT_TRANSITIONS.get(3)),
            new Segment("E", monitor, SEGMENT_TRANSITIONS.get(4))
        };

        for (int i = 0; i < SEGMENT_TRANSITIONS.size(); i++)
            for (int j = 0; j < SEGMENT_THREADS.get(i); j++)
                threads.add(new Thread(segments[i], "[Segment " + segments[i].getName() + " - Thread " + j + "]"));

        petriNet.setPlaceName("P16", "CS1");
        petriNet.setPlaceName("P17", "CS2");
        petriNet.setPlaceName("P18", "CS3");

        petriNet.setTransitionTime("T4", TIMED_TRANSITIONS.get(0)[0], TIMED_TRANSITIONS.get(0)[1]);
        petriNet.setTransitionTime("T5", TIMED_TRANSITIONS.get(1)[0], TIMED_TRANSITIONS.get(1)[1]);
        petriNet.setTransitionTime("T6", TIMED_TRANSITIONS.get(2)[0], TIMED_TRANSITIONS.get(2)[1]);
        petriNet.setTransitionTime("T7", TIMED_TRANSITIONS.get(3)[0], TIMED_TRANSITIONS.get(3)[1]);
        petriNet.setTransitionTime("T8", TIMED_TRANSITIONS.get(4)[0], TIMED_TRANSITIONS.get(4)[1]);
        petriNet.setTransitionTime("T10", TIMED_TRANSITIONS.get(5)[0], TIMED_TRANSITIONS.get(5)[1]);
        petriNet.setTransitionTime("T11", TIMED_TRANSITIONS.get(6)[0], TIMED_TRANSITIONS.get(6)[1]);
        petriNet.setTransitionTime("T12", TIMED_TRANSITIONS.get(7)[0], TIMED_TRANSITIONS.get(7)[1]);

        for (Thread thread : threads) {
            thread.start();
            Logger.logSystem(String.format("STARTED -> %-35s", thread.getName()));
        }

        Logger.logSystem("");

        try {
            TimeUnit.MILLISECONDS.sleep(TIME_EXECUTION);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (Thread thread : threads)
            thread.interrupt();

        for (Thread thread : threads)
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        logStatistics(monitor);

        Logger.logSystem("\n* ------------------------------- PICASSO ------------------------------- *");
        Logger.shutdown();
    }
}
