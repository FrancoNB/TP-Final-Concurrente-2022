package com.picasso.Policy;

import java.util.Random;

public class PolicyRandom implements Policy {
    private Random random;

    public PolicyRandom() {
        this.random = new Random();
    }

    public int decide(int[] transitionsAbleToFire, Object ... args) {
        if (transitionsAbleToFire.length == 0)
            return -1;
        
        return transitionsAbleToFire[random.nextInt(transitionsAbleToFire.length)];
    }
}
