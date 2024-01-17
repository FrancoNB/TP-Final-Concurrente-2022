package PetriNet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Transition class represents a transition in a Petri Net
 */
public class Transition extends PetriNetElement {
    // List of input arcs
    private final List<Arc> inputArcs;
    // List of output arcs
    private final List<Arc> outputArcs;
    // Instant when the transition is sensibilized
    private long alfaTime;
    // Instant when the transition is desensibilized
    private long betaTime;
    // Last time the transition was fired
    private long time;
    // True if the transition is timed
    private boolean timed;

    /**
     * Constructor for Transition class set timed to false
     * @param name Name of the transition
     */
    protected Transition(String name) {
        super(name);
        inputArcs = new ArrayList<>();
        outputArcs = new ArrayList<>();
        timed = false;
    }

    /**
     * Sets the time frame of the transition, setting timed to true
     * @param alfaTime Instant when the transition is sensibilized
     * @param betaTime Instant when the transition is desensibilized
     */
    public void setTimeFrame(long alfaTime, long betaTime) {
        this.alfaTime = alfaTime;
        this.betaTime = betaTime;
        timed = true;
    }

    /**
     * Checks if the transition is timed
     * @return True  if the transition is timed
     *         False otherwise
     */
    public boolean isTimed() {
        return timed;
    }

    /**
     * Sets the last time the transition was fired to the current time
     */
    public void setTimeStamp() {
        if (timed)
            time = new Date().getTime();
    }

    /**
     * Getter for the last time the transition was fired
     * @return Last time the transition was fired
     */
    public long getTimeStamp() {
        return time;
    }

    /**
     * Getter for the instant when the transition is sensibilized
     * @return Instant when the transition is sensibilized
     */
    public long getAlfaTime() {
        return isTimed() ? alfaTime : 0;
    }

    /**
     * Getter for the instant when the transition is desensibilized
     * @return Instant when the transition is desensibilized
     */
    public long getBetaTime() {
        return isTimed() ? betaTime : 0;
    }

    /**
     * Checks if the transition can fire. 
     * If the transition is not connected, it can't fire.
     * A transition can fire if all the input arcs can fire and all the output arcs can fire. 
     * in case the transition is timed, it can fire if the current time is between alfaTime and betaTime.
     * @return True  if the transition can fire
     *         False otherwise
     */
    public boolean canFire() {
        if (this.isNotConnected())
            return false;

        for (Arc arc : inputArcs)
            if (!arc.canFire())
                return false;

        for (Arc arc : outputArcs)
            if (!arc.canFire())
                return false;

        return true;
    }

    /**
     * Fires all the input arcs and all the output arcs of the transition
     */
    public void fire() {
        if (this.isNotConnected())
            return;

        for (Arc arc : inputArcs)
            arc.fire();

        for (Arc arc : outputArcs)
            arc.fire();
    }

    /**
     * Adds an input arc to the transition
     * @param arc Input arc to add
     */
    public void addInputArc(Arc arc) {
        inputArcs.add(arc);
    }

    /**
     * Adds an output arc to the transition
     * @param arc Output arc to add
     */
    public void addOutputArc(Arc arc) {
        outputArcs.add(arc);
    }

    /**
     * Checks if the transition is not connected
     * @return True  if the transition is not connected
     *         False otherwise
     */
    public boolean isNotConnected() {
        return inputArcs.isEmpty() && outputArcs.isEmpty();
    }

    /**
     * Returns a string representation of the transition
     * @return String with the name, connected, timed and canFire of the transition
     */
    @Override
    public String toString() {
        return "Transition {" +
                "name='" + getName() +
                ", connected=" + !isNotConnected() +
                ", timed=" + isTimed() +
                ", canFire=" + canFire();
    }
}
