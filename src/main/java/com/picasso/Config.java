package com.picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.picasso.Artist.Artist;
import com.picasso.Artist.BWPainter;
import com.picasso.Artist.Compressor;
import com.picasso.Artist.Diffuser;
import com.picasso.Artist.FilterPattern;
import com.picasso.Artist.PainterPattern;
import com.picasso.Artist.RGBPainter;
import com.picasso.Artist.SuperPositioner;
import com.picasso.Artist.TemplatePattern;

/**
 * Config class is used to store the configuration of the program.
 */
public abstract class Config {
    // Start time of the program.
    public static final long START_TIME = System.currentTimeMillis();
    // Number of places of the Petri net.
    public static final int NUMBER_OF_PLACES = 18;
    // Number of transitions of the Petri net.
    public static final int NUMBER_OF_TRANSITIONS = 12;

    // Initial marking of the Petri net.
    public static final int[] INITIAL_MARKING = {0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 4, 2, 2, 3, 1, 4, 6, 3};

    // Incidence matrix of the Petri net.
    public static final int[][] INCIDENCE_MATRIX = { /*Matriz de incidencia*/
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
    public static final List<int[]> INVARIANTS_TRANSITIONS = List.of(
        new int[]{1, 2, 4, 6, 8},
        new int[]{1, 3, 5, 7, 8},
        new int[]{9, 10, 11, 12}
    );

    // Transitions and artists of each segment.
    public static final List<Map<Integer, Artist>> SEGMENT_TRANSITIONS = List.of(
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

    // Valid final transitions of the Petri net.
    public static final int[] FINAL_TRANSITIONS = {2, 3, 4, 5, 6, 7, 8, 10, 11, 12};

    // Time alpha and beta of each timed transition.
    public static final List<int[]> TIMED_TRANSITIONS = List.of(
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
    public static final List<Integer> SEGMENT_THREADS = List.of(2, 4, 4, 1, 4);

    // Time of execution until interrupting threads.
    public static final int TIME_EXECUTION = 60000;
}
