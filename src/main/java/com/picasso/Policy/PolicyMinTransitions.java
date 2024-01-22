package com.picasso.Policy;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PolicyMinTransitions implements Policy {
    @SuppressWarnings("unchecked")
    public int decide(int[] transitionsAbleToFire, Object ... args) {    
        if (transitionsAbleToFire.length == 0)
            return -1;
        
        Map<Integer, Integer> transitionsFiredCount = (Map<Integer, Integer>) args[0];
        Map<int[], Integer> invariantsTransitionsFiredCount = (Map<int[], Integer>) args[1];

        int[] minInvariant = getInvariantWithLessTransitionsExecuted(invariantsTransitionsFiredCount);
        
        int[] transitionsAbleToFireInInvariant = getTransitionsAbleToFireInInvariant(minInvariant, transitionsAbleToFire);

        if (transitionsAbleToFireInInvariant[0] == -1)
            return -1;

        int minTransition = getTransitionWithLessTransitionsExecuted(transitionsAbleToFireInInvariant, transitionsFiredCount);

        return minTransition;
    }

    private int[] getInvariantWithLessTransitionsExecuted(Map<int[], Integer> invariantsTransitionsFiredCount) {
        return invariantsTransitionsFiredCount.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private int[] getTransitionsAbleToFireInInvariant(int[] invariant, int[] transitionsAbleToFire) {
        List<Integer> transitionsAbleToFireList = Arrays.stream(transitionsAbleToFire).boxed().collect(Collectors.toList());

        List<Integer> transitionsAbleToFireInInvariantList = transitionsAbleToFireList.stream()
                .filter(transition -> Arrays.stream(invariant).anyMatch(place -> place == transition))
                .collect(Collectors.toList());

        if (transitionsAbleToFireInInvariantList.isEmpty())
            return new int[]{-1};

        return transitionsAbleToFireInInvariantList.stream().mapToInt(Integer::intValue).toArray();
    }

    private int getTransitionWithLessTransitionsExecuted(int[] transitionsAbleToFire, Map<Integer, Integer> transitionsFiredCount) {
        return Arrays.stream(transitionsAbleToFire)
                .boxed()
                .min((t1, t2) -> Integer.compare(transitionsFiredCount.getOrDefault(t1, 0), transitionsFiredCount.getOrDefault(t2, 0)))
                .orElse(-1);
    }
}
