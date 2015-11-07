
import java.util.Random;
import java.util.function.BiFunction;
import net.coderodde.string.matching.StringMatchers;

public class PerformanceDemo {

    public static void main(final String... args) {
        int N = 3_000_000;
        int M = 3000;
        StringBuilder sb = new StringBuilder(N);

        for (int i = 0; i < N; ++i) {
            sb.append('a');
        }

        String text = sb.append('b').toString();

        sb.delete(0, sb.length());

        for (int i = 0; i < M; ++i) {
            sb.append('a');
        }

        String pattern = sb.append('b').toString();

        System.out.println("[WORST CASE OF String.indexOf]");
        demo(text, pattern);

        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        text = getRandomText(random);
        pattern = getRandomPattern(random);

        System.out.println();
        System.out.println("[RANDOM STRINGS]");
        System.out.println("[SEED: " + seed + "]");

        demo(text, pattern);
    }

    private static String getRandomText(Random random) {
        int n = 10_000_000;
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; ++i) {
            sb.append('a' + random.nextInt(26));
        }

        return sb.toString();
    }

    private static String getRandomPattern(Random random) {
        int n = 1_000;
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; ++i) {
            sb.append('a' + random.nextInt(26));
        }

        return sb.toString();
    }

    private static void profile(BiFunction<String, String, Integer> matcher,
            String text,
            String pattern,
            int expectedIndex,
            String matcherName) {
        long startTime = System.currentTimeMillis();
        int index = matcher.apply(text, pattern);
        long endTime = System.currentTimeMillis();

        if (index != expectedIndex) {
            throw new IllegalStateException(
                    matcher.getClass() + " failed. Returned: " + index
                    + ", expected: " + expectedIndex);
        }

        System.out.println(matcherName + " in "
                + (endTime - startTime) + " milliseconds.");
    }

    private static void demo(String text, String pattern) {
        long startTime = System.currentTimeMillis();
        int expectedIndex = text.indexOf(pattern);
        long endTime = System.currentTimeMillis();

        System.out.println("String.indexOf in " + (endTime - startTime)
                + " milliseconds.");

        profile(StringMatchers.KnuthMorrisPrattMatcher::match,
                text,
                pattern,
                expectedIndex,
                "Knuth-Morris-Pratt matcher");

        profile(StringMatchers.AutomatonMatcher::match,
                text,
                pattern,
                expectedIndex,
                "Finite automaton matcher");

        profile(StringMatchers.RabinKarpMatcher::match,
                text,
                pattern,
                expectedIndex,
                "Rabin-Karp matcher");

        profile(StringMatchers.ZMatcher::match,
                text,
                pattern,
                expectedIndex,
                "Z matcher");
    }
}
