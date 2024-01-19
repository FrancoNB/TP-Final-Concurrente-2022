package Policy;

public class PolicyB implements Policy {

    public int decide(int[] transitionsAbleToFire) {
        return transitionsAbleToFire[transitionsAbleToFire.length - 1];
    }
}
