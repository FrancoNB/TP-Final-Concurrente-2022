package PetriNet;

public class InhibitorArc extends Arc {

    /**
     * Constructor for InhibitorArc class with weight set to 1 and direction from place to transition
     * @param name Name of the arc
     * @param place Place of the arc
     * @param transition Transition of the arc
     */
    protected InhibitorArc(String name, Place place, Transition transition) {
        super(name, place, transition);
    }

    @Override
    public boolean canFire() {
        return place.getTokens() < this.getWeight();
    }

    @Override
    public void fire() {
        // Do nothing
    }
}
