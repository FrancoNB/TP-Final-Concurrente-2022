package com.picasso.Artist;

/**
 * BWPainter class is used to implement the black and white painter of production system.
 */
public class BWPainter extends GenericArtist {
    private static final long MIN_TIME = 32;
    private static final long MAX_TIME = 41;

    public BWPainter() {
        super(MIN_TIME, MAX_TIME);
    }

    @Override
    public String toString() {
        return "Black and White Painter";
    }
}
