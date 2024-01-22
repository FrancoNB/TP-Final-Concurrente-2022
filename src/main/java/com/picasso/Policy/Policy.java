package com.picasso.Policy;

/**
 * Policy interface is used to implement different policies for deciding which transition to fire next.
 */
public interface Policy {
    /**
     * Decides which transition to fire next.
     * @param transitionsAbleToFire Array of transitions that are able to fire.
     * @param args Additional arguments.
     * @return Index of the transition to fire.
     */
    public int decide(int[] transitionsAbleToFire, Object ... args);
}
