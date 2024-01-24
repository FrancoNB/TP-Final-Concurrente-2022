package com.picasso.Artist;

/**
 * TemplatePattern is used to refactor the template pattern of production system.
 */
public class TemplatePattern extends GenericArtist {
    private static final long MIN_TIME = 20;
    private static final long MAX_TIME = 23;

    public TemplatePattern() {
        super(MIN_TIME, MAX_TIME);
    }

    @Override
    public String toString() {
        return "Refactory Template Pattern";
    }
}
