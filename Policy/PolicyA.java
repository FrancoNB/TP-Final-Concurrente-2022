package Policy;

public class PolicyA implements Policy {
    
    public int decide(int[] transitionsAbleToFire) {
        return transitionsAbleToFire[0];
    }
}
