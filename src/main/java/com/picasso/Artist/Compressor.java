package com.picasso.Artist;

/**
 * Compressor class is used to implement the image compressor of production system.
 */
public class Compressor extends GenericArtist {
    public static final long MIN_TIME = 60;
    public static final long MAX_TIME = 71;

    public Compressor() {
        super(MIN_TIME, MAX_TIME);
    }

    @Override
    public String toString() {
        return "Compressor";
    }
}
