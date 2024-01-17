package PetriNet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Data.Logger;

public class PetriNet extends PetriNetElement {
    private final List<Place> places;
    private final List<Transition> transitions;
    private final List<Arc> arcs;
    private final List<InhibitorArc> inhibitorArcs;

    private int[][] incidenceMatrix;
    private int[] markings;
    private int[] initialState;
    private int[] enabledTransitions;

    List<int[]> invariantsTransitions;
    Map<String[], Integer> invariantsPlace;
    final Set<List<Integer>> states;

    public PetriNet(String name) {
        super(name);
        places = new ArrayList<>();
        transitions = new ArrayList<>();
        arcs = new ArrayList<>();
        inhibitorArcs = new ArrayList<>();
        invariantsTransitions = new ArrayList<>();
        invariantsPlace = new HashMap<>();
        states = new HashSet<>();
    }

    public void add(PetriNetElement o) {
        if (o instanceof Place)
            places.add((Place) o);
        else if (o instanceof Transition)
            transitions.add((Transition) o);
        else if (o instanceof Arc)
            arcs.add((Arc) o);
        else if (o instanceof InhibitorArc)
            inhibitorArcs.add((InhibitorArc) o);
    }

    public void getTransitionsAbleToFire() {
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

    public long[][] getTransitionsTimeRange() {
        long[][] arr = new long[transitions.size()][2];

        for (int i = 0; i < transitions.size(); i++) {
            arr[i][0] = transitions.get(i).getAlfaTime();
            arr[i][1] = transitions.get(i).getBetaTime();
        }

        return arr;
    }

    public int[] getTimeEnabledTransitions() {
        return null;
    }

    public boolean isEnabled(int transition) {
        return enabledTransitions[transition - 1] == 1;
    }

    public int[] getEnableTransitions() {
        return enabledTransitions;
    }

    public void transition(String name) {
        transitions.add(new Transition(name));
    }

    public Place place(String name) {
        Place p = new Place(name);
        places.add(p);
        return p;
    }

    public void place(String name, int tokens) {
        Place p = new Place(name);
        p.setTokens(tokens);
        places.add(p);
    }

    public Arc arc(String name, Place place, Transition transition) {
        Arc a = new Arc(name, place, transition);
        arcs.add(a);
        return a;
    }

    public Arc arc(String name, Transition transition, Place place) {
        Arc a = new Arc(name, transition, place);
        arcs.add(a);
        return a;
    }

    public void printVectorE() {
        System.out.printf("------------- Enabled Transitions ---------------\n");

        for (Transition transition : transitions) {
            System.out.printf("%s ", transition.getName());
        }

        System.out.println("");

        for (int enabledTransition : enabledTransitions) {
            System.out.printf(" %d ", enabledTransition);
        }
    }
}
