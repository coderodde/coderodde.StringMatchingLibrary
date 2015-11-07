
import java.util.function.BiFunction;
import net.coderodde.string.matching.StringMatchers;

public class PerformanceDemo {

    public static void main(final String... args) {
        int N = 10_000_000;
        int M = 300;
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
}
