package Policy;

import java.util.Map;

public interface Policy {
    public int decide(Map<Integer, Integer> transitionsAbleToFire);
}
