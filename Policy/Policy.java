package Policy;

public interface Policy {
    public int decide(int[] transitionsAbleToFire, Object ... args);
}
