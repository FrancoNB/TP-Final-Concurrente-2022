package main.java.com.picasso.PetriNet;

/**
 * Place class
 * Represents a place in a Petri Net
 */
public class Place extends PetriNetElement {
    // -1 means unlimited
    private static final int UNLIMITED = -1;
    // Number of tokens in the place
    private int tokens;
    // Maximum number of tokens in the place
    private int maxTokens;


    /**
     * Constructor for Place class initial tokens and maxTokens are set to 0
     * @param name Name of the place
     */
    public Place(String name) {
        super(name);
        tokens = 0;
        maxTokens = UNLIMITED;
    }

    /**
     * Constructor for Place class maxTokens is set to 0
     * @param name Name of the place
     * @param tokens Number of initial tokens
     */
    public Place(String name, int tokens) {
        super(name);
        this.tokens = tokens;
        maxTokens = UNLIMITED;
    }

    /**
     * Constructor for Place class
     * @param name Name of the place
     * @param tokens Number of initial tokens
     * @param maxTokens Maximum number of tokens
     */
    public Place(String name, int tokens, int maxTokens) {
        super(name);
        this.tokens = tokens;
        this.maxTokens = maxTokens;
    }

    /**
     * Checks if the place has at least the number of tokens passed as argument
     * @param tokens Number of tokens to check
     * @return True  if the place has at least the number of tokens passed as argument
     *         False otherwise
     */
    public boolean hasAtLeastTokens(int tokens) {
        return this.tokens >= tokens;
    }

    /**
     * Checks the number of tokens in the place is greater than maxTokens when tokens passed as argument are added
     * @param tokens Number of tokens to add for checking
     * @return True  if maxTokens is reached when tokens passed as argument are added
     *         False otherwise
     */
    public boolean hasMaxTokensReached(int tokens) {
        if (isUnlimited())
            return false;

        return (this.tokens + tokens) > maxTokens;
    }


    /**
     * Checks if the place is unlimited
     * @return True  if the place is unlimited
     *         False otherwise
     */
    public boolean isUnlimited() {
        return maxTokens == UNLIMITED;
    }
    
    /**
     * Returns the number of tokens in the place
     * @return Number of tokens in the place
     */
    public int getTokens() {
        return tokens;
    }

    /**
     * Set the number of tokens in the place
     * @param tokens Number of tokens to set
     */
    public void setTokens(int tokens) {
        if (tokens >= 0) {
            this.tokens = tokens;
        }
    }

    /**
     * Set the maximum number of tokens in the place
     * @param maxTokens Maximum number of tokens to set
     */
    public void setMaxTokens(int maxTokens) {
        if (maxTokens >= 0 || maxTokens == UNLIMITED) {
            this.maxTokens = maxTokens;
        }
    }

    /**
     * Add tokens to the place
     * @param tokens Number of tokens to add
     */
    public void addTokens(int tokens) {
        if (tokens >= 0) {
            this.tokens += tokens;
        }
    }

    /**
     * Remove tokens from the place
     * @param tokens Number of tokens to remove
     */
    public void removeTokens(int tokens) {
        if (tokens >= 0) {
            this.tokens -= tokens;
        }
    }

    /**
     * Returns a string representation of the place
     * @return String with the name, tokens and maxTokens of the place
     */
    @Override
    public String toString() {
        return "Place {" + 
                    "name=" + getName() +
                    ", tokens=" + tokens + 
                    ", maxTokens=" + (isUnlimited() ? "Unlimited" : maxTokens) + '}';
    }

}
