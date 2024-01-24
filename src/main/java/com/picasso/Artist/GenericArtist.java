package com.picasso.Artist;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.picasso.Config;
import com.picasso.Data.Logger;

/**
 * GenericArtist class is used to implement the generic artist of production system.
 * Abstract class for artists.
 */
public abstract class GenericArtist implements Artist {
    // Random number generator
    private final Random random;
    // Minimum time to work
    private final long minTime;
    // Maximum time to work
    private final long maxTime;

    /**
     * Constructor for GenericArtist.
     * @param minTime minimum time to work
     * @param maxTime maximum time to work
     */
    protected GenericArtist(long minTime, long maxTime) {
        this.random = new Random();
        this.minTime = minTime;
        this.maxTime = maxTime;
    }
    
    /**
     * Work method is used to simulate the work of artist.
     * @throws InterruptedException if the thread is interrupted.
     */
    public void work() {
        try {
            TimeUnit.MILLISECONDS.sleep(random.nextInt() % (maxTime - minTime) + minTime);
            
            Logger.logArtist(String.format("[%d] WORKED -> %-35s", (System.currentTimeMillis() - Config.START_TIME), this.toString()));
        } catch (InterruptedException e) {
            Logger.logArtist(String.format("[%d] WORK INTERRUPTED -> %-35s", (System.currentTimeMillis() - Config.START_TIME), this.toString()));
        }
    }
}
