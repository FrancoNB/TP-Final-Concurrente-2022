package Monitor;

import java.util.HashMap;
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

    private final Map<Integer, Integer> transitionsFireCount;

    private Policy policy;

    public Monitor(PetriNet petriNet, Policy policy) {
        this.petriNet = petriNet;
        this.policy = policy;

        this.mutex = new ReentrantLock();

        this.transitionsFireCount = IntStream.range(0, petriNet.getNumberOfTransitions() - 1)
                                             .collect(HashMap::new, (m, v) -> m.put(v, 0), HashMap::putAll);

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

            transitionsFireCount.put(transition, transitionsFireCount.get(transition) + 1);

            int [] waitTransitions = IntStream.range(0, waitQueue.length - 1)
                                              .map(i -> mutex.hasWaiters(waitQueue[i]) ? 1 : 0)
                                              .toArray();

            int [] transitionsAbleToFire = IntStream.range(0, waitQueue.length - 1)
                                                    .map(i -> (petriNet.getEnableTransitions()[i] == 1 && waitTransitions[i] == 1) ? (i + 1) : 0)
                                                    .toArray();

            int nextTransition = policy.decide(transitionsAbleToFire);

            if (nextTransition > 0)
                waitQueue[nextTransition - 1].signalAll();
            
            mutex.unlock();
        } catch (InterruptedException | IllegalMonitorStateException e) {
            e.printStackTrace();
        } finally {
            mutex.unlock();
        }
    }
}