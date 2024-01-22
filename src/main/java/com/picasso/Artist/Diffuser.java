package com.picasso.Artist;

/**
 * Diffuser class is used to implement the filter for image diffuse of production system.
 */
public class Diffuser extends GenericArtist {
    private static final long MIN_TIME = 20;
    private static final long MAX_TIME = 25;

    public Diffuser() {
        super(MIN_TIME, MAX_TIME);
    }

    @Override
    public String toString() {
        return "Diffuser";
    }
}
