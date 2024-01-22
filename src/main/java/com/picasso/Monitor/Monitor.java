package com.picasso.Monitor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

import com.picasso.PetriNet.PetriNet;
import com.picasso.PetriNet.Transition;
import com.picasso.Policy.Policy;

/**
 * Monitor class is used to synchronize the threads that fire transitions.
 */
public class Monitor {
    // Petri net to be synchronized
    private final PetriNet petriNet;
    // Mutex for synchronization
    private final ReentrantLock mutex;
    // Condition queue for each transition
    private final Condition[] waitQueue;
    // Map of fired transitions and their count
    private final Map<Integer, Integer> transitionsFiredCount;
    // Map of invariants and their count
    private final Map<int[], Integer> invariantsTransitionsFiredCount;
    // Policy for deciding which transition to fire next
    private Policy policy;
    // Flag for interrupting threads
    private boolean isInterrupted;

    /**
     * Returns an array of transitions that are waiting to be fired.
     * @return Array of transitions that are waiting to be fired.
     */
    private int[] getWaitTransitions() {
        return IntStream.range(0, waitQueue.length)
                        .map(i -> mutex.hasWaiters(waitQueue[i]) ? 1 : 0)
                        .toArray();
    }

    /**
     * Returns an array of transitions that are able to fire.
     * @param transitions Array of transitions that are waiting to be fired.
     * @return Array of transitions that are able to fire.
     */
    private int[] getTransitionsAbleToFire(int[] transitions) {
        return IntStream.range(0, waitQueue.length)
                        .map(i -> (petriNet.getEnableTransitions()[i] == 1 && transitions[i] == 1) ? (i + 1) : 0)
                        .filter(element -> element != 0)
                        .toArray();
    }

    /**
     * Updates the count of fired transitions and invariants.
     * @param transitionFired Transition that was fired.
     */
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

    /**
     * Constructor for Monitor class. Initializes the monitor.
     * @param petriNet Petri net to be synchronized.
     * @param policy Policy for deciding which transition to fire next.
     * @param invariantsTransitions List of invariants and their transitions.
     */
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

    /**
     * Returns the transitions fired count map.
     * @return Transitions fired count map.
     */
    public Map<Integer, Integer> getTransitionsFiredCount() {
        return transitionsFiredCount;
    }

    /**
     * Returns the invariants transitions fired count map.
     * @return Invariants transitions fired count map.
     */
    public Map<int[], Integer> getInvariantsTransitionsFiredCount() {
        return invariantsTransitionsFiredCount;
    }

    /**
     * Changes the policy for deciding which transition to fire next.
     * @param policy Policy for deciding which transition to fire next.
     */
    public void changePolicy(Policy policy) {
        this.policy = policy;
    }

    /**
     * Fires a transition. If the transition is not enabled, the thread sleeps until other thread fires a transition that enables the transition.
     * @param transition Transition to be fired.
     * @throws InterruptedException If the thread is interrupted.
     */
    public void fireTransition(int transition) throws InterruptedException {
        try {
            mutex.lock();

            Transition.TimedState timedState;

            while (true) {
                timedState = petriNet.checkTimedStateTransition(transition);
                
                if (timedState == Transition.TimedState.NO_TIMED || timedState == Transition.TimedState.IN_WINDOW)
                    break;

                if (timedState == Transition.TimedState.BEFORE_WINDOW) {
                    long sleepTime = petriNet.getTransition(transition).getTimeStamp() + petriNet.getTransition(transition).getAlfaTime() - System.currentTimeMillis();

                    waitQueue[transition - 1].await(sleepTime, TimeUnit.MILLISECONDS);

                    if (isInterrupted)
                        throw new InterruptedException();

                    continue;
                }

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