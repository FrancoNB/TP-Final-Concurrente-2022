package Policy;

import java.util.Map;

public class PolicyMinTransitions implements Policy {
    @SuppressWarnings("unchecked")
    public int decide(int[] transitionsAbleToFire, Object ... args) {    
        if (transitionsAbleToFire.length == 0)
            return -1;
        
        Map<Integer, Integer> transitionsFiredCount = null;
        Map<int[], Integer> invariantsTransitionsFiredCount = null;

        for (Object arg : args) {
            if (arg instanceof Map) {
                if (arg instanceof Map<?, ?>)
                    transitionsFiredCount = (Map<Integer, Integer>) arg;
                else if (arg instanceof Map<?, ?>)
                    invariantsTransitionsFiredCount = (Map<int[], Integer>) arg;
                else
                    throw new IllegalArgumentException("Argument must be a Map");
            } else
                throw new IllegalArgumentException("Unsupported argument type");
        }
        
        int[] minInvariant = getInvariantWithLessTransitionsExecuted(invariantsTransitionsFiredCount);
        
        int[] transitionsAbleToFireInInvariant = getTransitionsAbleToFireInInvariant(minInvariant, transitionsAbleToFire);

        if (transitionsAbleToFireInInvariant[0] == -1)
            return -1;

        int minTransition = getTransitionWithLessTransitionsExecuted(transitionsAbleToFireInInvariant, transitionsFiredCount);

        return minTransition;
    }

    private int[] getInvariantWithLessTransitionsExecuted(Map<int[], Integer> invariantsTransitionsFiredCount) {
        int minTransitionsExecuted = Integer.MAX_VALUE;
        int[] minInvariant = null;

        for (Map.Entry<int[], Integer> entry : invariantsTransitionsFiredCount.entrySet()) {
            if (entry.getValue() < minTransitionsExecuted) {
                minTransitionsExecuted = entry.getValue();
                minInvariant = entry.getKey();
            }
        }

        return minInvariant;
    }

    private int[] getTransitionsAbleToFireInInvariant(int[] invariant, int[] transitionsAbleToFire) {
        int[] transitionsAbleToFireInInvariant = new int[transitionsAbleToFire.length];
        int i = 0;

        for (int transition : transitionsAbleToFire) {
            for (int place : invariant) {
                if (transition == place) {
                    transitionsAbleToFireInInvariant[i] = transition;
                    i++;
                    break;
                }
            }
        }

        if (i == 0)
            return new int[]{-1};

        return transitionsAbleToFireInInvariant;
    }

    private int getTransitionWithLessTransitionsExecuted(int[] transitionsAbleToFire, Map<Integer, Integer> transitionsFiredCount) {
        int minTransitionsExecuted = Integer.MAX_VALUE;
        int minTransition = -1;

        for (int transition : transitionsAbleToFire) {
            if (transitionsFiredCount.get(transition) < minTransitionsExecuted) {
                minTransitionsExecuted = transitionsFiredCount.get(transition);
                minTransition = transition;
            }
        }

        return minTransition;
    }
}