package Policy;

import java.util.Map;

public class PolicyMinTransitions implements Policy {
    public int decide(Map<Integer, Integer> transitionsAbleToFire) {    
        if (transitionsAbleToFire.isEmpty())
            return -1;
        
        int minTransitionsExecuted = Integer.MAX_VALUE;
        int minTransition = -1;
        
        for (Map.Entry<Integer, Integer> entry : transitionsAbleToFire.entrySet()) {
            int transitionsExecuted = entry.getValue();
            
            if (transitionsExecuted < minTransitionsExecuted) {
                minTransitionsExecuted = transitionsExecuted;
                minTransition = entry.getKey();
            }
        }
        
        return minTransition;
    }
}
