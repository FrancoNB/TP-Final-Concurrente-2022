package PetriNet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import Data.Logger;

/**
 * PetriNet class Represents a Petri Net.
 */
public class PetriNet extends PetriNetElement {
    // List of places in the Petri Net.
    private final List<Place> places;
    // List of transitions in the Petri Net.
    private final List<Transition> transitions;
    // List of arcs in the Petri Net.
    private final List<Arc> arcs;

    // Incidence matrix of the Petri Net.
    private int[][] incidenceMatrix;
    // Markings of the Petri Net.
    private int[] markings;
    // Initial markings of the Petri Net.
    private int[] initialMarking;
    // Enabled transitions of the Petri Net.
    private int[] enabledTransitions;

    // All markings states of the Petri Net.
    final Set<int[]> states;

    /**
     * Add new transition to the Petri Net.
     * @param name Name of the transition.
     */
    private void addTransition(String name) {
        transitions.add(new Transition(name));
    }

    /**
     * Add new place to the Petri Net.
     * @param name Name of the place.
     * @param tokens Number of tokens in the place.
     */
    private void addPlace(String name, int tokens) {
        Place p = new Place(name, tokens);
        places.add(p);
    }

    /**
     * Add new arc to the Petri Net.
     * @param name Name of the arc.
     * @param place Place of the arc.
     * @param transition Transition of the arc.
     */
    private void addArc(String name, Place place, Transition transition) {
        Arc a = new Arc(name, place, transition);
        arcs.add(a);
    }

    /**
     * Add new arc to the Petri Net.
     * @param name Name of the arc.
     * @param transition Transition of the arc.
     * @param place Place of the arc.
     */
    private void addArc(String name, Transition transition, Place place) {
        Arc a = new Arc(name, transition, place);
        arcs.add(a);
    }

    /**
     * Generate places of the Petri Net with the initial markings.
     * @param initialMarks Initial markings of the Petri Net.
     * @param cantPlaces Number of places to generate.
     */
    private void generatePlaces(int[] initialMarks, int cantPlaces) {
        for (int i = 0; i < cantPlaces; i++)
            this.addPlace("Place" + (i + 1), initialMarks[i]);

        this.markings = initialMarks;
    }

    /**
     * Generate transitions of the Petri Net.
     * @param cantTransitions Number of transitions to generate.
     */
    private void generateTransitions(int cantTransitions) {
        for (int i = 0; i < cantTransitions; i++)
            this.addTransition("Transition" + (i + 1));
    }

    /**
     * Generate arc of the Petri Net with the incidence matrix passed as argument.
     * @param incidenceMatrix Incidence matrix of the Petri Net.
     * @param cantPlaces Number of places.
     * @param cantTransitions Number of transitions.
     */
    private void generateIncidences(int[][] incidenceMatrix, int cantPlaces, int cantTransitions) {
        for (int i = 0; i < cantPlaces; i++) {
            for (int j = 0; j < cantTransitions; j++) {
                if (incidenceMatrix[i][j] == 1)
                    addArc("Arc" + (i + j + 1), transitions.get(j), places.get(i));
                else if (incidenceMatrix[i][j] == -1)
                    addArc("Arc" + (i + j + 1), places.get(i), transitions.get(j));
            }
        }

        this.incidenceMatrix = incidenceMatrix;
    }

    /**
     * Update the enabled transitions of the Petri Net according to the markings.
     */
    private void updateTransitionsAbleToFire() {
        enabledTransitions = new int[transitions.size()];

        for (int i = 0; i < transitions.size(); i++) {
            int[] s = new int[places.size()];

            for (int j = 0; j < places.size(); j++) {
                s[j] = markings[j] + incidenceMatrix[j][i];

                if (s[j] < 0) {
                    enabledTransitions[i] = 0;
                    break;
                }

                enabledTransitions[i] = 1;
                transitions.get(i).setTimeStamp();
            }
        }
    }

    /**
     * Update the markings of the Petri Net according to the transition fired.
     * @param transition Transition fired.
     */
    private void updateNet(int transition) {
        for(int i = 0; i < places.size(); i++)
            markings[i] += incidenceMatrix[i][transition - 1];

        this.states.add(markings.clone());

        updateTransitionsAbleToFire();
    }

    /**
     * Check if the Petri Net is in deadlock.
     * @return True if the Petri Net is in deadlock.
     *         False otherwise.
     */
    private boolean checkDeadlock() {
        int[] enabled = getEnableTransitions();
        int test = 0;

        for(int i = 0; i < transitions.size(); i++)
            if(enabled[i] == 0)
                test++;

        if(test == transitions.size())
            return true;

        return false;
    }

    /**
     * Constructor for PetriNet class.
     * @param name Name of the Petri Net.
     * @param initialMarks Initial markings of the Petri Net.
     * @param incidenceMatrix Incidence matrix of the Petri Net.
     * @param cantPlaces Number of places.
     * @param cantTransitions Number of transitions.
     */
    public PetriNet(String name, int[] initialMarks, int[][] incidenceMatrix, int cantPlaces, int cantTransitions) {
        super(name);

        places = new ArrayList<>();
        transitions = new ArrayList<>();
        arcs = new ArrayList<>();
        states = new HashSet<>();

        generatePlaces(initialMarks, cantPlaces);

        generateTransitions(cantTransitions);

        initialMarking = new int[cantPlaces];
        System.arraycopy(initialMarks, 0, initialMarking, 0, cantPlaces);

        this.states.add(initialMarks.clone());

        generateIncidences(incidenceMatrix, cantPlaces, cantTransitions);

        updateTransitionsAbleToFire();
    }

    /**
     * Check if the transition passed as argument is enabled.
     * @param transition Transition to check.
     * @return True if the transition is enabled.
     *         False otherwise.
     */
    public boolean isEnabled(int transition) {
        return enabledTransitions[transition - 1] == 1;
    }

    /**
     * Check if the transition passed as argument is timed.
     * @param transition Transition to check.
     * @return True if the transition is timed.
     *         False otherwise.
     */ 
    public boolean isTimedTransition(int transition) {
        return (transitions.get((transition - 1)).isTimed());
    }

    /**
     * Get the timed transitions of the Petri Net.
     * @return Array with all transitions of the Petri Net.
     *         1 if the transition is timed, 0 otherwise.
     */
    public int[] getTimeSensibleTransitions() {
        int[] arr = new int[transitions.size()];

        for (int i = 0; i < transitions.size(); i++) {
            if (transitions.get(i).isTimed()) 
                arr[i] = 1;
            else
                arr[i] = 0;
        }

        return arr;
    }

    /**
     * Get the time frame of the transition in the Petri Net.
     * @return Bidimensional array with the time frame of each transition.
     *         The first column is the alfa time and the second column is the beta time.
     *         If the transition is not timed, the time frame is set to 0.
     */
    public long[][] getTransitionsTimeRange() {
        long[][] arr = new long[transitions.size()][2];

        for (int i = 0; i < transitions.size(); i++) {
            arr[i][0] = transitions.get(i).getAlfaTime();
            arr[i][1] = transitions.get(i).getBetaTime();
        }

        return arr;
    }

    /**
     * Get the enabled transitions of the Petri Net.
     * @return Array with all enabled transitions of the Petri Net.
     */
    public int[] getEnableTransitions() {
        return enabledTransitions;
    }

    /**
     * Getter for the places of the Petri Net.
     * @return List of places of the Petri Net.
     */
    public List<Place> getPlaces() {
        return places;
    }

    /**
     * Getter for the transitions of the Petri Net.
     * @return List of transitions of the Petri Net.
     */
    public List<Transition> getTransitions() {
        return transitions;
    }

    /**
     * Getter for the arcs of the Petri Net.
     * @return List of arcs of the Petri Net.
     */
    public List<Arc> getArcs() {
        return arcs;
    }

    /**
     * Getter for the states of the Petri Net.
     * @return Set of states of the Petri Net.
     */
    public Set<int[]> getStates() {
        return states;
    }

    /**
     * Getter for the incidence matrix of the Petri Net.
     * @return Bidimensional array with the incidence matrix of the Petri Net.
     */
    public int[][] getIncidenceMatrix() {
        return incidenceMatrix;
    }

    /**
     * Getter for the markings of the Petri Net.
     * @return Array with the markings of the Petri Net.
     */
    public int[] getMarkings() {
        return markings;
    }

    /**
     * Getter for the initial markings of the Petri Net.
     * @return Array with the initial markings of the Petri Net.
     */
    public int[] getInitialMarking() {
        return initialMarking;
    }

    /**
     * Change the name of the place passed as argument.
     * @param oldName Old name of the place.
     * @param newName New name of the place.
     */
    public void setPlaceName(String oldName, String newName) {
        for(Place p : places)
            if(p.getName().equals(oldName)) {
                p.setName(newName);
                break;
            }
    }

    /**
     * Change the name of the transition passed as argument.
     * @param oldName Old name of the transition.
     * @param newName New name of the transition.
     */
    public void setTransitionName(String oldName, String newName) {
        for(Transition t : transitions)
            if(t.getName().equals(oldName)) {
                t.setName(newName);
                break;
            }
    }

    /**
     * Set time frame of the transition passed as argument.
     * @param name Name of the transition.
     * @param alfa Initial instant of the time frame.
     * @param beta Final instant of the time frame.
     */
    public void setTransitionTime(String name, long alfa, long beta) {
        for(Transition t : transitions)
            if(t.getName().equals(name)) {
                t.setTimeFrame(alfa, beta);
                break;
            }
    }

    /**
     * Fire the transition passed as argument and log the transition fired.
     * @param transition Transition to fire. 
     * @param log Logger to log the transition fired.
     */
    public void fireTransition(int transition, Logger log) {
        Transition t = transitions.get(transition - 1);

        if (t.canFire())
        {
            t.fire();

            log.logTransitions(getName());
            
            updateNet(transition);
        }
    }

    /**
     * Fire randomly a transition of the Petri Net until the time passed as argument is reached or the Petri Net is in deadlock. Log the transitions fired.
     * @param time Maximum time to fire transitions.
     * @param log Logger to log the transitions fired.
     */
    public void fireContinuously(int time, Logger log) {
        long startTime = System.currentTimeMillis();
        long endTime = startTime + time * 1000;

        while(System.currentTimeMillis() <  endTime) {
            int i = ThreadLocalRandom.current().nextInt(1, transitions.size());

            fireTransition(i, log);

            if (checkDeadlock())
            {
                System.out.println("---------- DEADLOCK ----------");
                break;
            }
        }
    }
}
