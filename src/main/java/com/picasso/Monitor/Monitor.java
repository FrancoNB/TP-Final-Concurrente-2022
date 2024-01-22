package com.picasso.Monitor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

import com.picasso.PetriNet.PetriNet;
import com.picasso.PetriNet.Transition;
import com.picasso.Policy.Policy;

public class Monitor {
    private final PetriNet petriNet;

    private final ReentrantLock mutex;

    private final Condition[] waitQueue;

    private final Map<Integer, Integer> transitionsFiredCount;

    private final Map<int[], Integer> invariantsTransitionsFiredCount;

    private Policy policy;

    private boolean isInterrupted;

    private int[] getWaitTransitions() {
        return IntStream.range(0, waitQueue.length)
                        .map(i -> mutex.hasWaiters(waitQueue[i]) ? 1 : 0)
                        .toArray();
    }

    private int[] getTransitionsAbleToFire(int[] transitions) {
        return IntStream.range(0, waitQueue.length)
                        .map(i -> (petriNet.getEnableTransitions()[i] == 1 && transitions[i] == 1) ? (i + 1) : 0)
                        .filter(element -> element != 0)
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

        isInterrupted = false;

        this.mutex = new ReentrantLock();

        this.transitionsFiredCount = IntStream.range(1, petriNet.getNumberOfTransitions() + 1)
                                             .collect(HashMap::new, (m, v) -> m.put(v, 0), HashMap::putAll);

        this.invariantsTransitionsFiredCount = IntStream.range(0, invariantsTransitions.size())
                                              .collect(HashMap::new, (m, v) -> m.put(invariantsTransitions.get(v), 0), HashMap::putAll);

        this.waitQueue = IntStream.range(0, petriNet.getNumberOfTransitions())
                                  .mapToObj(i -> mutex.newCondition())
                                  .toArray(Condition[]::new);
    }

    public Map<Integer, Integer> getTransitionsFiredCount() {
        return transitionsFiredCount;
    }

    public Map<int[], Integer> getInvariantsTransitionsFiredCount() {
        return invariantsTransitionsFiredCount;
    }

    public void changePolicy(Policy policy) {
        this.policy = policy;
    }

    public void fireTransition(int transition) throws InterruptedException {
        try {
            mutex.lock();

            Transition.TimedState timedState;

            while (true) {
                timedState = petriNet.checkTimedStateTransition(transition);
                
                if (timedState == Transition.TimedState.NO_TIMED || timedState == Transition.TimedState.IN_WINDOW)
                    break;

                waitQueue[transition - 1].await();

                if (isInterrupted)
                    throw new InterruptedException();
            }

            while (!petriNet.isEnabled(transition)) {
                waitQueue[transition - 1].await();

                if (isInterrupted)
                    throw new InterruptedException();
            }
            
            petriNet.fireTransition(transition);

            int[] waitTransitions = getWaitTransitions();

            int[] transitionsAbleToFire = getTransitionsAbleToFire(waitTransitions);

            UpdateFiredCounts(transition);

            int nextTransition = policy.decide(transitionsAbleToFire, transitionsFiredCount, invariantsTransitionsFiredCount);

            if (nextTransition > 0)
                waitQueue[nextTransition - 1].signalAll();
        } catch (IllegalMonitorStateException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            for (Condition condition : waitQueue)
                condition.signalAll();

            isInterrupted = true;

            throw e;
        } finally {
            mutex.unlock();
        }
    }
}