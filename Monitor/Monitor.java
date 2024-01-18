package Monitor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import Data.Logger;
import PetriNet.PetriNet;
import Policy.Policy;

public class Monitor {
    private final PetriNet petriNet;
    private final Policy policy;

    private final ReentrantLock mutex;

    private final Logger logger;

    public Monitor(PetriNet petriNet, Logger logger) {
        this.petriNet = petriNet;
        this.logger = logger;

        this.policy = new Policy(this);

        this.mutex = new ReentrantLock();
    }
}