package com.picasso.PetriNet;

/**
 * Arc class represents an arc in a Petri Net
 */
public class Arc extends PetriNetElement {
    // Place connected to the arc
    protected final Place place;
    // Transition connected to the arc
    private final Transition transition;
    // Direction of the arc
    private final Direction direction;
    // Weight of the arc
    private int weight;

    /**
     * Enum representing the direction of the arc
     */
    enum Direction {

        // Place to transition fire
        PLACE_TO_TRANSITION {

            /**
             * Checks if the arc can fire from the place passed as argument
             * @param place Place from fire the arc
             * @param weight Weight of the arc
             * @return True  if the arc can fire from the place passed as argument
             *         False otherwise
             */
            @Override
            public boolean canFire(Place place, int weight) {
                return place.hasAtLeastTokens(weight);
            }
            
            /**
             * Fires the arc from the place passed as argument
             * @param place Place from fire the arc
             * @param weight Weight of the arc
             */
            @Override
            public void fire(Place place, int weight) {
                place.removeTokens(weight);
            }
        },

        // Transition to place fire
        TRANSITION_TO_PLACE {

            /**
             * Checks if the arc can fire to the place passed as argument
             * @param place Place to fire the arc
             * @param weight Weight of the arc
             * @return True  if the arc can fire to the place passed as argument
             *         False otherwise
             */
            @Override
            public boolean canFire(Place place, int weight) {
                return !(place.hasMaxTokensReached(weight));
            }
            
            /**
             * Fires the arc to the place passed as argument
             * @param place Place to fire the arc
             * @param weight Weight of the arc
             */
            @Override
            public void fire(Place place, int weight) {
                place.addTokens(weight);
            }
        };

        public abstract boolean canFire(Place place, int weight);

        public abstract void fire(Place place, int weight);
    }

    /**
     * Constructor for Arc class with weight set to 1
     * @param name Name of the arc
     * @param direction Direction of the arc
     * @param place Place connected to the arc
     * @param transition Transition connected to the arc
     */
    private Arc(String name, Direction direction, Place place, Transition transition) {
        super(name);
        this.place = place;
        this.transition = transition;
        this.direction = direction;
        this.weight = 1;
    }

    /**
     * Constructor for Arc class with weight set to 1 and direction from place to transition
     * @param name Name of the arc
     * @param place Place connected to the arc
     * @param transition Transition connected to the arc
     */
    protected Arc(String name, Place place, Transition transition) {
        this(name, Direction.PLACE_TO_TRANSITION, place, transition);
        this.transition.addInputArc(this);
    }

    /**
     * Constructor for Arc class with weight set to 1 and direction from transition to place
     * @param name Name of the arc
     * @param transition Transition connected to the arc
     * @param place Place connected to the arc
     */
    protected Arc(String name, Transition transition, Place place) {
        this(name, Direction.TRANSITION_TO_PLACE, place, transition);
        this.transition.addOutputArc(this);
    }

    /**
     * Checks if the arc can fire
     * @return True  if the arc can fire
     *         False otherwise
     */
    public boolean canFire() {
        return direction.canFire(place, weight);
    }

    /**
     * Fires the arc
     */
    public void fire() {
        direction.fire(place, weight);
    }

    /**
     * Setter for the weight of the arc
     * @param weight Weight of the arc
     */
    public void setWeight(int weight) {
        if (weight > 0)
            this.weight = weight;
    }

    /**
     * Getter for the weight of the arc
     * @return Weight of the arc
     */
    public int getWeight() {
        return weight;
    }
}
