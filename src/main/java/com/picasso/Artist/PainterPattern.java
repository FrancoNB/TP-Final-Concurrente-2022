package com.picasso.Artist;

/**
 * PainterPattern is used to refactor the painter pattern of production system.
 */
public class PainterPattern extends GenericArtist {
    private static final long MIN_TIME = 13;
    private static final long MAX_TIME = 15;

    public PainterPattern() {
        super(MIN_TIME, MAX_TIME);
    }
    
    @Override
    public String toString() {
        return "Refactory Painter Pattern";
    }
}
