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
    // Map of transitions fired in invariant cicle
    private final Map<int[], Map<Integer, Integer>> transitionsFiredInInvariantCicle;
    // Policy for deciding which transition to fire next
    private Policy policy;
    // Flag to indicate if the monitor was interrupted
    private boolean interrupted;

    /**
     * Returns an array of transitions that are waiting to be fired.
     * @return Array of transitions that are waiting to be fired.
     */
    private int[] getWaitTransitions() {
        try {
            mutex.lock();

            return IntStream.range(0, waitQueue.length)
                            .map(i -> mutex.hasWaiters(waitQueue[i]) ? 1 : 0)
                            .toArray();
        } finally {
            mutex.unlock();
        }
    }

    /**
     * Returns an array of transitions that are able to fire.
     * @param transitions Array of transitions that are waiting to be fired.
     * @return Array of transitions that are able to fire.
     */
    private int[] getTransitionsAbleToFire(int[] transitions) {
        try {
            mutex.lock();

            return IntStream.range(0, waitQueue.length)
                            .map(i -> (petriNet.getEnableTransitions()[i] == 1 && transitions[i] == 1) ? (i + 1) : 0)
                            .filter(element -> element != 0)
                            .toArray();
        } finally {
            mutex.unlock();
        }
    }

    /**
     * Updates the count of fired transitions and invariants.
     * @param transitionFired Transition that was fired.
     */
    private void UpdateFiredCounts(int transitionFired) {
        try {
            mutex.lock();
            System.out.println("Thread " + Thread.currentThread().getName() + " in UpdateFiredCounts");

            transitionsFiredCount.put(transitionFired, transitionsFiredCount.get(transitionFired) + 1);

            transitionsFiredInInvariantCicle.keySet().stream()
                .filter(i -> Arrays.stream(i).anyMatch(t -> t == transitionFired))
                .forEach(invariant -> {
                    transitionsFiredInInvariantCicle.get(invariant).put(transitionFired, transitionsFiredInInvariantCicle.get(invariant).get(transitionFired) + 1);
                });

            invariantsTransitionsFiredCount.keySet().stream()
                .filter(i -> Arrays.stream(i).anyMatch(t -> t == transitionFired))
                .forEach(invariant -> {
                    if (Arrays.stream(invariant).allMatch(t -> transitionsFiredInInvariantCicle.get(invariant).get(t) > 0)) {
                        invariantsTransitionsFiredCount.put(invariant, invariantsTransitionsFiredCount.get(invariant) + 1);

                        transitionsFiredInInvariantCicle.get(invariant).keySet()
                            .forEach(t -> transitionsFiredInInvariantCicle.get(invariant).put(t, transitionsFiredInInvariantCicle.get(invariant).get(t) - 1));
                    }
                });
        } finally {
            System.out.println("Thread " + Thread.currentThread().getName() + " out UpdateFiredCounts");
            mutex.unlock();
        }
    }

    /**
     * Sets the interrupted flag to true and signals all the threads in the wait queue.
     */
    private void setInterrupted() {
        try {
            mutex.lock();

            interrupted = true;

            Arrays.stream(waitQueue).forEach(c -> c.signalAll());
        } finally {
            mutex.unlock();
        }
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
        this.interrupted = false;

        this.mutex = new ReentrantLock();

        this.transitionsFiredCount = IntStream.range(1, petriNet.getNumberOfTransitions() + 1)
                                              .collect(HashMap::new, (m, v) -> m.put(v, 0), HashMap::putAll);

        this.invariantsTransitionsFiredCount = IntStream.range(0, invariantsTransitions.size())
                                                        .collect(HashMap::new, (m, v) -> m.put(invariantsTransitions.get(v), 0), HashMap::putAll);

        this.transitionsFiredInInvariantCicle = IntStream.range(0, invariantsTransitions.size())
                                                         .collect(HashMap::new, (m, v) -> m.put(invariantsTransitions.get(v), new HashMap<Integer, Integer>()), HashMap::putAll);

        transitionsFiredInInvariantCicle.keySet().stream()
            .forEach(invariant -> {
                for (int transition : invariant) {
                    transitionsFiredInInvariantCicle.get(invariant).put(transition, 0);
                }
            });

        this.waitQueue = IntStream.range(0, petriNet.getNumberOfTransitions())
                                  .mapToObj(i -> mutex.newCondition())
                                  .toArray(Condition[]::new);
    }

    /**
     * Getter for the interrupted flag.
     * @return True if the thread was interrupted.
     *         False otherwise.
     */
    public boolean isInterrupted() {
        try {
            mutex.lock();
            
            return interrupted;
        } finally {
            mutex.unlock();
        }
    }

    /**
     * Returns the transitions fired count map.
     * @return Transitions fired count map.
     */
    public Map<Integer, Integer> getTransitionsFiredCount() {
        try {
            mutex.lock();
            
            return transitionsFiredCount;
        } finally {
            mutex.unlock();
        }
    }

    /**
     * Returns the invariants transitions fired count map.
     * @return Invariants transitions fired count map.
     */
    public Map<int[], Integer> getInvariantsTransitionsFiredCount() {
        try {
            mutex.lock();
            
            return invariantsTransitionsFiredCount;
        } finally {
            mutex.unlock();
        }
    }

    /**
     * Changes the policy for deciding which transition to fire next.
     * @param policy Policy for deciding which transition to fire next.
     */
    public void changePolicy(Policy policy) {
        try {
            mutex.lock(); 

            this.policy = policy;
        } finally {
            mutex.unlock();
        }
    }

    /**
     * Fires a transition. If the transition is not enabled, the thread sleeps until other thread fires a transition that enables the transition.
     * @param transition Transition to be fired.
     * @param endTransitions Flag to indicate if the transition is a final transition.
     */
    public boolean fireTransition(int transition, boolean endTransitions) {
        boolean fired = false;

        try {
            mutex.lock();
            
            try {
                while (!isInterrupted()) {               
                    Transition.TimedState timedState = petriNet.checkTimedStateTransition(transition);
                    
                    if (timedState == Transition.TimedState.NO_TIMED || timedState == Transition.TimedState.IN_WINDOW)
                        if (petriNet.isEnabled(transition))
                            break;
    
                    if (timedState == Transition.TimedState.BEFORE_WINDOW) {
                        long sleepTime = petriNet.getTransition(transition).getTimeStamp() + petriNet.getTransition(transition).getAlfaTime() - System.currentTimeMillis();
    
                        waitQueue[transition - 1].await(sleepTime, TimeUnit.MILLISECONDS);
    
                        continue;
                    }
    
                    waitQueue[transition - 1].await();
                }
            } catch (InterruptedException e) {
                setInterrupted();
            }

            if (isInterrupted() && !endTransitions)
                return false;

            fired = petriNet.fireTransition(transition);

            if (fired)
                UpdateFiredCounts(transition);

            if (!endTransitions) {
                int[] waitTransitions = getWaitTransitions();

                int[] transitionsAbleToFire = getTransitionsAbleToFire(waitTransitions);
                
                int nextTransition = policy.decide(transitionsAbleToFire, transitionsFiredCount, invariantsTransitionsFiredCount);

                if (nextTransition > 0)
                    waitQueue[nextTransition - 1].signalAll();
            }

            return fired;
        } catch (IllegalMonitorStateException e) {
            e.printStackTrace();
            System.exit(1);
            return fired;
        } finally {
            mutex.unlock();
        }  
    }
}