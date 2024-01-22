package com.picasso.Policy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * PolicyMinTransitions class is used to implement the policy that fires the transition with less invariant transitions executed.
 * If there are more than one transition with the same number of invariant transitions executed, the policy fires the transition with less transitions executed.
 * If there are more than one transition with the same number of invariant transitions executed and the same number of transitions executed, the policy fires the first transition.
 */
public class PolicyMinTransitions implements Policy {

    /**
     * Decides which transition to fire next.
     * @param transitionsAbleToFire Array of transitions that are able to fire.
     * @param args Map of fired transitions and their count and map of invariants and their count.
     * @return Index of the transition to fire.
     */
    @SuppressWarnings("unchecked")
    public int decide(int[] transitionsAbleToFire, Object ... args) {    
        int[] transitionsAbleToFireInInvariant = null;

        if (transitionsAbleToFire.length == 0)
            return -1;
        
        Map<Integer, Integer> transitionsFiredCount = new HashMap<>((Map<Integer, Integer>) args[0]);
        Map<int[], Integer> invariantsTransitionsFiredCount = new HashMap<>((Map<int[], Integer>) args[1]);

        do {
            int[] invariantToFire = getInvariantWithLessTransitionsExecuted(invariantsTransitionsFiredCount);
        
            transitionsAbleToFireInInvariant = getTransitionsAbleToFireInInvariant(invariantToFire, transitionsAbleToFire);
        
            invariantsTransitionsFiredCount.remove(invariantToFire);
        } while (transitionsAbleToFireInInvariant.length == 0);
        
        int transitionToFire = getTransitionWithLessTransitionsExecuted(transitionsAbleToFireInInvariant, transitionsFiredCount);

        return transitionToFire;
    }

    /**
     * Returns the invariant with less executions.
     * @param invariantsTransitionsFiredCount Map of invariants and their fire count.
     * @return Invariant with less executions.
     */
    private int[] getInvariantWithLessTransitionsExecuted(Map<int[], Integer> invariantsTransitionsFiredCount) {
        return invariantsTransitionsFiredCount.entrySet()
                                              .stream()
                                              .min(Map.Entry.comparingByValue())
                                              .map(Map.Entry::getKey)
                                              .orElse(null);
    }

    /**
     * Returns the transitions able to fire in the invariant. 
     * @param invariant Invariant to check.
     * @param transitionsAbleToFire Array of transitions able to fire.
     * @return Array of transitions able to fire in the invariant.
     */
    private int[] getTransitionsAbleToFireInInvariant(int[] invariant, int[] transitionsAbleToFire) {
        return Arrays.stream(transitionsAbleToFire)
                     .filter(transition -> Arrays.stream(invariant).anyMatch(t -> t == transition))
                     .toArray();
     }

     /**
      * Returns the transition with less transitions executed.
      * @param transitionsAbleToFire Array of transitions able to fire.
      * @param transitionsFiredCount Map of transitions and their fire count.
      * @return Transition with less transitions executed.
      */
    private int getTransitionWithLessTransitionsExecuted(int[] transitionsAbleToFire, Map<Integer, Integer> transitionsFiredCount) {
        return Arrays.stream(transitionsAbleToFire)
                     .boxed()
                     .min((t1, t2) -> Integer.compare(transitionsFiredCount.getOrDefault(t1, 0), transitionsFiredCount.getOrDefault(t2, 0)))
                     .orElse(-1);
    }
}
