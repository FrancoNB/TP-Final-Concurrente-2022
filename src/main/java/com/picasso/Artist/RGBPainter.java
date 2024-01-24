package com.picasso.Artist;

/**
 * RGBPainter is an artist that paints the image with RGB colors.
 */
public class RGBPainter extends GenericArtist {
    private static final long MIN_TIME = 40;
    private static final long MAX_TIME = 47;

    public RGBPainter() {
        super(MIN_TIME, MAX_TIME);
    }

    @Override
    public String toString() {
        return "RGB Painter";
    }
}
