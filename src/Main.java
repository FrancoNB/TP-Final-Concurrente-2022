import java.util.ArrayList;
import java.util.List;

import Data.Logger;
import Monitor.Monitor;
import PetriNet.PetriNet;
import Policy.*;
import Segment.Segment;

public class Main {
    private static final int NUMBER_OF_PLACES = 18;
    private static final int NUMBER_OF_TRANSITIONS = 12;

    private static final int[] INITIAL_MARKING = {0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 4, 2, 2, 3, 1, 4, 6, 3};

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
        {  0,  0,  0,  0,  0,  0,  0,  0,  0, -1,  1,  0}, //CS3
    };

    private static final List<int[]> INVARIANTS_TRANSITIONS = List.of(
        new int[]{1, 2, 4, 6, 8},
        new int[]{1, 3, 5, 7, 8},
        new int[]{9, 10, 11, 12}
    );

    private static final List<int[]> SEGMENT_TRANSITIONS = List.of(
        new int[]{1},
        new int[]{2, 4, 6},
        new int[]{3, 5, 7},
        new int[]{8},
        new int[]{9, 10, 11, 12}
    );

    private static final List<Integer> SEGMENT_THREADS = List.of(2, 4, 4, 1, 4);

    public static void main(String[] args) {
        Logger logger = new Logger();
        PetriNet petriNet = new PetriNet("PetriNet", INITIAL_MARKING, INCIDENCE_MATRIX, NUMBER_OF_PLACES, NUMBER_OF_TRANSITIONS, logger);
        Policy policy = new PolicyRandom();
        Monitor monitor = new Monitor(petriNet, policy, INVARIANTS_TRANSITIONS);
        List<Thread> threads = new ArrayList<Thread>();

        Segment[] segments = {
            new Segment("A", monitor, SEGMENT_TRANSITIONS.get(0)),
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

    }
}
