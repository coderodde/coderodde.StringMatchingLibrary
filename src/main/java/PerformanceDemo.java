import java.util.Random;
import net.coderodde.string.matching.ExactStringMatcher;
import net.coderodde.string.matching.ExactStringMatchers;

public class PerformanceDemo {

    public static void main(String[] args) {
        int N = 5_000_000;

        String text = getWorstCaseText(N);

        System.out.println("[WORST CASE OF String.indexOf]");

        for (int i = 3000; i > 0; i -= 500) {
            System.out.println();
            System.out.println("[Pattern length: " + i + "]");
            String pattern = getWorstCaseText(i);
            demo(text, pattern);
        }

        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        text = getRandomText(random);
        String pattern = getRandomPattern(random);

        System.out.println();
        System.out.println("[RANDOM STRINGS]");
        System.out.println("[SEED: " + seed + "]");

        demo(text, pattern);
    }

    private static String getWorstCaseText(int length) {
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length - 1; ++i) {
            sb.append('a');
        }

        return sb.append('b').toString();
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

        profile(ExactStringMatchers.getKnuthMorrisPrattMatcher(),
                text,
                pattern,
                expectedIndex);

        profile(ExactStringMatchers.getFiniteAutomatonMatcher(),
                text,
                pattern,
                expectedIndex);

        profile(ExactStringMatchers.getRabinKarpMatcher(),
                text,
                pattern,
                expectedIndex);

        profile(ExactStringMatchers.getZMatcher(),
                text,
                pattern,
                expectedIndex);

        profile(ExactStringMatchers.getBoyerMooreMatcher(),
                text,
                pattern,
                expectedIndex);

        System.out.print("Naïve ");
        profile(ExactStringMatchers.getNaiveBoyerMooreMatcher(),
                text,
                pattern,
                expectedIndex);
        
        profile(ExactStringMatchers.getShiftOrMatcher(),
                text,
                pattern,
                expectedIndex);
    }
}
