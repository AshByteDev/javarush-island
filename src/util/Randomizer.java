package util;

import java.util.concurrent.ThreadLocalRandom;

public class Randomizer {

    private Randomizer() {
    }

    public static int nextIntInclusive(int maxInclusive) {
        return ThreadLocalRandom.current().nextInt(maxInclusive + 1);
    }

    public static int nextIntInRange(int minInclusive, int maxInclusive) {
        return ThreadLocalRandom.current().nextInt(minInclusive, maxInclusive + 1);
    }

    public static int nextSignedDelta(int maxAbs) {
        return nextIntInRange(-maxAbs, maxAbs);
    }

    public static boolean chance(int percent) {
        if (percent <= 0) {
            return false;
        }
        if (percent >= 100) {
            return true;
        }
        int roll = nextIntInRange(1, 100);
        return roll <= percent;
    }
}
