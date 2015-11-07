import java.util.Random;
import net.coderodde.string.matching.ExactStringMatcher;
import net.coderodde.string.matching.ExactStringMatchers;

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
        return getRandomString(10_000_000, random);
    }

    private static String getRandomPattern(Random random) {
        return getRandomString(200, random);
    }

    private static String getRandomString(int size, Random random) {
        StringBuilder sb = new StringBuilder(size);

        for (int i = 0; i < size; ++i) {
            sb.append((char)('a' + random.nextInt(26)));
        }

        return sb.toString();
    }
    
    private static void profile(ExactStringMatcher matcher,
                                String text,
                                String pattern,
                                int expectedIndex) {
        long startTime = System.nanoTime();
        int index = matcher.match(text, pattern);
        long endTime = System.nanoTime();

        if (index != expectedIndex) {
            throw new IllegalStateException(
                    matcher.getClass() + " failed. Returned: " + index
                    + ", expected: " + expectedIndex);
        }

        System.out.printf("%s in %.3f milliseconds.\n", 
                          matcher.getClass().getSimpleName(),
                          (endTime - startTime) / 1e6);
    }

    private static void demo(String text, String pattern) {
        long startTime = System.nanoTime();
        int expectedIndex = text.indexOf(pattern);
        long endTime = System.nanoTime();

        System.out.printf("String.indexOf in %.3f millisecons.\n", 
                          (endTime - startTime) / 1e6);

        profile(ExactStringMatchers.knuthMorrisPrattMatcher(),
                text,
                pattern,
                expectedIndex);

        profile(ExactStringMatchers.finiteAutomatonMatcher(),
                text,
                pattern,
                expectedIndex);

        profile(ExactStringMatchers.rabinKarpMatcher(),
                text,
                pattern,
                expectedIndex);

        profile(ExactStringMatchers.zMatcher(),
                text,
                pattern,
                expectedIndex);
        
        profile(ExactStringMatchers.boyerMooreMatcher(),
                text,
                pattern,
                expectedIndex);
    }
}
