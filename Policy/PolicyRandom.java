package Policy;

import java.util.Map;
import java.util.Random;

public class PolicyRandom implements Policy {
    private Random random;

    public PolicyRandom() {
        this.random = new Random();
    }

    public int decide(Map<Integer, Integer> transitionsAbleToFire) {
        if (transitionsAbleToFire.isEmpty())
            return -1;
            
        return transitionsAbleToFire.keySet().toArray(new Integer[0])[random.nextInt(transitionsAbleToFire.size())];
    }
}
