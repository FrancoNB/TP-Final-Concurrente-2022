package com.picasso.Segment;

import java.util.Map;

import com.picasso.Artist.GenericArtist;
import com.picasso.Data.Logger;
import com.picasso.Artist.Artist;
import com.picasso.Monitor.Monitor;

/**
 * Segment class is used to implement a segment of the Petri net. A segment is a set of transitions that are fired together.
 */
public class Segment implements Runnable {
    // Array of transitions that are fired together
    private final Map<Integer, Artist> transitions;
    // Monitor for synchronization
    private final Monitor monitor;
    // Name of the segment
    private final String name;

    /**
     * Constructor for Segment class.
     * @param name Name of the segment.
     * @param monitor Monitor for synchronization.
     * @param transitions Array of transitions that are fired together.
     */
    public Segment(String name, Monitor monitor, Map<Integer, Artist> transitions) {
        this.monitor = monitor;
        this.transitions = transitions;
        this.name = name;
    }

    /**
     * Returns the name of the segment.
     * @return String with the name of the segment.
     */
    public String getName() {
        return name;
    }

    /**
     * Fires the transitions of the segment.
     */
    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                for (Integer t : transitions.keySet()) {
                    Artist artist = transitions.get(t);

                    monitor.fireTransition(t);
                    
                    Logger.logTransition(String.format("FIRED -> T%-2d ON %-35s", t, Thread.currentThread().getName()));

                    if (artist != null)
                        ((GenericArtist)artist).work();
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
