package PetriNet;

/**
 * PetriNetElement class is the base class for all the elements of a Petri Net
 */
public abstract class PetriNetElement {
    // Name of the element
    private String name;

    /**
     * Constructor for PetriNetElement class
     * @param name Name of the element
     */
    public PetriNetElement(String name) {
        this.name = name;
    }

    /**
     * Getter for the name of the element
     * @return Name of the element
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the name of the element
     * @param name Name of the element
     */
    public void setName(String name) {
        this.name = name;
    }
}