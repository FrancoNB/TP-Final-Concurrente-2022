package Monitor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

import PetriNet.PetriNet;
import PetriNet.Transition;
import Policy.Policy;

public class Monitor {
    private final PetriNet petriNet;

    private final ReentrantLock mutex;

    private final Condition[] waitQueue;

    private final Map<Integer, Integer> transitionsFiredCount;

    private final Map<int[], Integer> invariantsTransitionsFiredCount;

    private Policy policy;

    private int[] getWaitTransitions() {
        return IntStream.range(0, waitQueue.length - 1)
                        .map(i -> mutex.hasWaiters(waitQueue[i]) ? 1 : 0)
                        .toArray();
    }

    private int[] getTransitionsAbleToFire(int[] transitions) {
        return IntStream.range(0, waitQueue.length - 1)
                        .map(i -> (petriNet.getEnableTransitions()[i] == 1 && transitions[i] == 1) ? (i + 1) : 0)
                        .toArray();
    }

    private void UpdateFiredCounts(int transitionFired) {
        transitionsFiredCount.put(transitionFired, transitionsFiredCount.get(transitionFired) + 1);

        invariantsTransitionsFiredCount.keySet().stream()
            .filter(i -> Arrays.stream(i).anyMatch(t -> t == transitionFired))
            .findFirst()
            .ifPresent(invariant -> {
                if (Arrays.stream(invariant).allMatch(t -> transitionsFiredCount.get(t) > invariantsTransitionsFiredCount.get(invariant))) {
                    invariantsTransitionsFiredCount.put(invariant, invariantsTransitionsFiredCount.get(invariant) + 1);
                }
            });
    }

    public Monitor(PetriNet petriNet, Policy policy, List<int[]> invariantsTransitions) {
        this.petriNet = petriNet;
        this.policy = policy;

        this.mutex = new ReentrantLock();

        this.transitionsFiredCount = IntStream.range(1, petriNet.getNumberOfTransitions())
                                             .collect(HashMap::new, (m, v) -> m.put(v, 0), HashMap::putAll);

        this.invariantsTransitionsFiredCount = IntStream.range(0, invariantsTransitions.size())
                                              .collect(HashMap::new, (m, v) -> m.put(invariantsTransitions.get(v), 0), HashMap::putAll);

        this.waitQueue = IntStream.range(0, petriNet.getNumberOfTransitions() - 1)
                                  .mapToObj(i -> mutex.newCondition())
                                  .toArray(Condition[]::new);
    }

    public void changePolicy(Policy policy) {
        this.policy = policy;
    }

    public void fireTransition(int transition) {
        try {
            mutex.lock();

            Transition.TimedState timedState;

            do {
               timedState = petriNet.checkTimedStateTransition(transition);

                waitQueue[transition].await();
            } while (timedState != Transition.TimedState.NO_TIMED && timedState != Transition.TimedState.IN_WINDOW);
                

            while (!petriNet.isEnabled(transition))
                waitQueue[transition].await();
            
            petriNet.fireTransition(transition);

            int[] waitTransitions = getWaitTransitions();

            int[] transitionsAbleToFire = getTransitionsAbleToFire(waitTransitions);

            UpdateFiredCounts(transition);

            int nextTransition = policy.decide(transitionsAbleToFire, transitionsFiredCount, invariantsTransitionsFiredCount);

            if (nextTransition > 0)
                waitQueue[nextTransition - 1].signalAll();
        } catch (InterruptedException | IllegalMonitorStateException e) {
            e.printStackTrace();
        } finally {
            mutex.unlock();
        }
    }
}