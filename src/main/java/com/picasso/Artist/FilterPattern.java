package com.picasso.Artist;

/**
 * FilterPattern class is used to refactor the filter pattern of production system.
 */
public class FilterPattern extends GenericArtist {
    private static final long MIN_TIME = 16;
    private static final long MAX_TIME = 19;

    public FilterPattern() {
        super(MIN_TIME, MAX_TIME);
    }

    @Override
    public String toString() {
        return "Refactory Filter Pattern";
    }
}
