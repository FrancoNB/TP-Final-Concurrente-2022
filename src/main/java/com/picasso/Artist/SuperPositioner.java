package com.picasso.Artist;

/**
 * SuperPositioner apllies the superposition of the template pattern in the image.
 */
public class SuperPositioner extends GenericArtist {
    private static final long MIN_TIME = 22;
    private static final long MAX_TIME = 28;

    public SuperPositioner() {
        super(MIN_TIME, MAX_TIME);
    }

    @Override
    public String toString() {
        return "Super Positioner";
    }
}