package com.picasso.Policy;

import java.util.Random;

/**
 * PolicyRandom class is used to implement the policy that fires a random transition.
 */
public class PolicyRandom implements Policy {
    // Random object for generating random numbers
    private Random random;

    /**
     * Constructor for PolicyRandom class. Initializes the random object.
     */
    public PolicyRandom() {
        this.random = new Random();
    }

    /**
     * Decides which transition to fire next.
     * @param transitionsAbleToFire Array of transitions that are able to fire.
     * @param args not used.
     * @return Index of the transition to fire.
     */
    public int decide(int[] transitionsAbleToFire, Object ... args) {
        if (transitionsAbleToFire.length == 0)
            return -1;
        
        return transitionsAbleToFire[random.nextInt(transitionsAbleToFire.length)];
    }
}
