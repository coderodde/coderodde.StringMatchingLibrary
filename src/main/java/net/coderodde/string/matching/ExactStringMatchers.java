package net.coderodde.string.matching;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class contains different string matching algorithms as static methods.
 *
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Nov 7, 2015)
 */
public final class ExactStringMatchers {

    /**
     * Returns an exact string matcher implementation based on the 
     * <a href="https://en.wikipedia.org/wiki/Knuth%E2%80%93Morris%E2%80%93Pratt_algorithm">
     * Knuth-Morris-Pratt algorithm</a>, that runs in time {@code O(n + m)}, 
     * where {@code n} is the length of the string to search in, and {@code m} 
     * is the length of the pattern. The space complexity is {@code m} for 
     * holding a so-called "failure function".
     * 
     * @return an exact string matcher.
     */
    public static ExactStringMatcher getKnuthMorrisPrattMatcher() {
        return new KnuthMorrisPrattMatcher();
    }

    /**
     * Returns an exact string matcher implementation based on 
     * <a href="http://www.geeksforgeeks.org/pattern-searching-set-5-efficient-constructtion-of-finite-automata/">
     * finite automata</a>. Runs in time {@code O(n + ms)}, where {@code n} is
     * the length of the range to search, {@code m} is the length of the pattern
     * to search for, and {@code s} is the number of distinct characters in the
     * pattern. The space complexity is {@code O(ms)}.
     * 
     * @return an exact string matcher.
     */
    public static ExactStringMatcher getFiniteAutomatonMatcher() {
        return new FiniteAutomatonMatcher();
    }

    /**
     * Returns an exact string matcher implementation based on the
     * <a href="https://en.wikipedia.org/wiki/Rabin%E2%80%93Karp_algorithm">
     * Rabin-Karp algorithm</a>. Worst case running time is {@code O(nm)}, where 
     * {@code n} is the length of the range to search in, and {@code m} is the 
     * length of the pattern to search for. The space complexity is constant.
     * 
     * @return an exact string matcher.
     */
    public static ExactStringMatcher getRabinKarpMatcher() {
        return new RabinKarpMatcher();
    }

    /**
     * Returns an exact string matcher implementation based on the 
     * <a href="http://www.geeksforgeeks.org/z-algorithm-linear-time-pattern-searching-algorithm/">
     * Z-algorithm</a>. Runs in time {@code O(n + m)}, where {@code n} is the 
     * length of the range to search in and {@code m} is the length of the 
     * pattern to search for. Space complexity is {@code O(n + m)}.
     * 
     * @return an exact string matcher. 
     */
    public static ExactStringMatcher getZMatcher() {
        return new ZMatcher();
    }

    /**
     * Returns an exact string matcher implementation based on the 
     * <a href="https://en.wikipedia.org/wiki/Boyer%E2%80%93Moore_string_search_algorithm">
     * Boyer-Moore algorithm</a>. The worst case running time is {@code O(nm)}, 
     * where {@code n} is the length of the text being searched in, and 
     * {@code m} is the length of the pattern being searched for. However, this
     * matcher runs fast in practice. Space complexity is {@code O(m)}.
     * 
     * @return an exact string matcher.
     */
    public static ExactStringMatcher getBoyerMooreMatcher() {
        return new BoyerMooreMatcher(false);
    }
    
    /**
     * Returns an exact string matcher implementation based on the <b>naïve</b>
     * <a href="https://en.wikipedia.org/wiki/Boyer%E2%80%93Moore_string_search_algorithm">
     * Boyer-Moore algorithm</a>. The worst case running time is {@code O(nm)}, 
     * where {@code n} is the length of the text being searched in, and 
     * {@code m} is the length of the pattern being searched for. However, this
     * matcher runs fast in practice. Space complexity is {@code O(m)}.
     * 
     * @return an exact string matcher.
     */
    public static ExactStringMatcher getNaiveBoyerMooreMatcher() {
        return new BoyerMooreMatcher(true);
    }

    public static ExactStringMatcher getHorspoolMatcher() {
        return new HorspoolMatcher();
    }
    
    private static final class KnuthMorrisPrattMatcher 
    implements ExactStringMatcher {

        @Override
        public int match(String text, String pattern, int startIndex) {
            startIndex = Math.max(0, startIndex);
            
            if (pattern.isEmpty()) {
                return Math.min(startIndex, text.length());
            }

            int n = text.length();
            int m = pattern.length();

            if (m > n) {
                return -1;
            }

            int[] prefixFunction = computePrefixFunction(pattern);
            int q = 0;

            for (int i = Math.max(0, startIndex); i < n; ++i) {
                while (q > 0 && pattern.charAt(q) != text.charAt(i)) {
                    q = prefixFunction[q - 1];
                }

                if (pattern.charAt(q) == text.charAt(i)) {
                    ++q;
                }

                if (q == m) {
                    return i - m + 1;
                }
            }

            return NOT_FOUND_INDEX;
        }

        private static int[] computePrefixFunction(String pattern) {
            int m = pattern.length();
            int[] prefixFunction = new int[m];
            int k = 0;

            for (int q = 1; q < m; ++q) {
                while (k > 0 && pattern.charAt(k) != pattern.charAt(q)) {
                    k = prefixFunction[k - 1];
                }

                if (pattern.charAt(k) == pattern.charAt(q)) {
                    ++k;
                }

                prefixFunction[q] = k;
            }

            return prefixFunction;
        }
    }

    private static final class FiniteAutomatonMatcher 
    implements ExactStringMatcher {

        @Override
        public int match(String text, String pattern, int startIndex) {
            startIndex = Math.max(0, startIndex);
            
            if (pattern.isEmpty()) {
                return Math.min(startIndex, text.length());
            }

            int n = text.length();
            Integer m = pattern.length();

            if (m > n) {
                return NOT_FOUND_INDEX;
            }

            TransitionFunction transitionFunction = 
                    computeTransitionFunction(pattern);

            Integer j = 0;

            for (int i = Math.max(0, startIndex); i < n; ++i) {
                if (j == null) {
                    j = 0;
                }

                j = transitionFunction.getState(j, text.charAt(i));

                if (m.equals(j)) {
                    return i - m + 1;
                }
            }

            return NOT_FOUND_INDEX;
        }


        private static TransitionFunction 
        computeTransitionFunction(String pattern) {
            return new TransitionFunction(pattern);
        }

        private static final class TransitionFunction {

            private final Map<Character, Integer>[] function;

            TransitionFunction(String pattern) {
                int m = pattern.length();
                this.function = new HashMap[m + 1];

                Set<Character> filter = new HashSet(m);

                for (Character c : pattern.toCharArray()) {
                    filter.add(c);
                }

                int numberOfCharacters = filter.size();
                Character[] characterArray = new Character[numberOfCharacters];
                filter.toArray(characterArray);

                for (int i = 0; i < function.length; ++i) {
                    function[i] = new HashMap<>(numberOfCharacters);
                }

                for (int i = 0; i < numberOfCharacters; ++i) {
                    function[0].put(characterArray[i], 0);
                }

                function[0].put(pattern.charAt(0), 1);

                for (int i = 1, lps = 0; i <= m; ++i) {
                    for (int x = 0; x < numberOfCharacters; ++x) {
                        function[i].put(characterArray[x], 
                                        function[lps].get(characterArray[x]));
                    }


                    if (i < m) {
                        function[i].put(pattern.charAt(i), i + 1);
                        lps = function[lps].get(pattern.charAt(i));
                    }
                }
            }

            Integer getState(int currentState, char character) {
                return function[currentState].get(character);
            }

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                Set<Character> alphabet = new TreeSet<>(function[0].keySet());
                Character[] array = new Character[alphabet.size()];
                alphabet.toArray(array);

                for (Map<Character, Integer> map : function) {
                    for (Character c : array) {
                        sb.append(map.get(c)).append(' ');
                    }

                    sb.append('\n');
                }

                return sb.toString();
            }
        }
    }

    private static final class RabinKarpMatcher implements ExactStringMatcher {

        @Override
        public int match(String text, String pattern, int startIndex) {
            startIndex = Math.max(0, startIndex);
            
            int m = pattern.length();

            if (m == 0) {
                return Math.min(startIndex, text.length());
            }

            int n = text.length();
            startIndex = Math.max(0, startIndex);

            if (m + startIndex > n) {
                return NOT_FOUND_INDEX;
            }

            Set<Character> alphabet = new HashSet<>();

            for (char c : pattern.toCharArray()) {
                alphabet.add(c);
            }

            long d = alphabet.size();
            long q = 2;
            long h = intpow(d, m - 1) % q;

            long p = 0;
            long t = 0;

            // Beginning of preprocessing.
            for (int i = 0; i < m; ++i) {
                p = (d * p + (long)(pattern.charAt(i))) % q;
                t = (d * t + (long)(text.charAt(i + startIndex))) % q;
            }
            // End of preprocessing.

            // Beginning of matching.
            for (int s = startIndex; s <= n - m;  ++s) {
                if (p == t) {
                    if (hasMatch(pattern, text, s)) {
                        return s;
                    }
                }

                if (s < n - m) {
                    long save_t = t;
                    t = (d * (save_t - (long)(text.charAt(s)) * h) + 
                         (long)(text.charAt(s + m))) % q;

                    if (t < 0) {
                        t = (t + q);
                    }
                }
            }

            return NOT_FOUND_INDEX;
        }

        private static boolean hasMatch(String pattern, 
                                        String text, 
                                        int shift) {
            int m = pattern.length();

            for (int i = 0; i < m; ++i) {
                if (pattern.charAt(i) != text.charAt(i + shift)) {
                    return false;
                }
            }

            return true;
        }

        private static long intpow(long base, int exponent) {
            long ret = 1;

            for (int i = 0; i < exponent; ++i) {
                ret *= base;
            }

            return ret;
        }
    }

    private static final class ZMatcher implements ExactStringMatcher {

        @Override
        public int match(String text, String pattern, int startIndex) {
            startIndex = Math.max(0, startIndex);
            
            if (pattern.isEmpty()) {
                return Math.min(startIndex, text.length());
            }

            int n = text.length();
            int m = pattern.length();

            if (m > n) {
                return -1;
            }

            startIndex = Math.max(0, startIndex);

            if (startIndex != 0) {
                text = text.substring(startIndex);
            }

            StringBuilder sb = new StringBuilder(text.length() + 
                                                 pattern.length() + 1 -
                                                 startIndex);

            sb.append(pattern).append(Character.valueOf('\0')).append(text);
            // Do not create a new string from the StringBuilder, but rather
            // use the builder to access the data.
            int[] zArray = computeZArray(sb);

            for (int i = Math.max(0, startIndex); i < zArray.length; ++i) {
                if (zArray[i] == m) {
                    return i - m - 1 + startIndex;
                }
            }

            return NOT_FOUND_INDEX;
        }

        private static int[] computeZArray(StringBuilder sb) {
            int n = sb.length();
            int[] ret = new int[n];

            int l = 0;
            int r = 0;

            for (int i = 1; i < n; ++i) {
                if (i > r) {
                    l = i;
                    r = i;

                    while (r < n && sb.charAt(r - l) == sb.charAt(r)) {
                        ++r;
                    }

                    ret[i] = r - l;
                    --r;
                } else {
                    int k = i - l;

                    if (ret[k] < r - i + 1) {
                        ret[i] = ret[k];
                    } else {
                        l = i;

                        while (r < n && sb.charAt(r - l) == sb.charAt(r)) {
                            ++r;
                        }

                        ret[i] = r - l;
                        --r;
                    }
                }
            }

            return ret;
        }
    }

    private static final class BoyerMooreMatcher implements ExactStringMatcher {

        private final boolean naive;

        BoyerMooreMatcher(boolean naive) {
            this.naive = naive;
        }

        @Override
        public int match(String text, String pattern, int startIndex) {
            startIndex = Math.max(0, startIndex);
            
            if (pattern.isEmpty()) {
                return Math.min(startIndex, text.length());
            }

            int n = text.length();
            int m = pattern.length();

            if (m > n) {
                return -1;
            }

            startIndex = Math.max(0, startIndex);

            if (naive) {
                return matchNaiveImpl(text, pattern, startIndex);
            } else {
                return matchImpl(text, pattern, startIndex);
            }
        } 

        private int matchImpl(String text, String pattern, int startIndex) {
            int n = text.length();
            int m = pattern.length();

            Map<Character, Integer> charTable = createCharTable(pattern);
            int offsetTable[] = createOffsetTable(pattern);

            for (int i = m - 1 + startIndex, j; i < n;) {
                for (j = m - 1; pattern.charAt(j) == text.charAt(i); --i, --j) {
                    if (j == 0) {
                        return i;
                    }
                }

                i += Math.max(offsetTable[m - j - 1], 
                              charTable.getOrDefault(text.charAt(i), m));
            }

            return NOT_FOUND_INDEX;
        }

        private int matchNaiveImpl(String text, 
                                   String pattern, 
                                   int startIndex) {
            int n = text.length();
            int m = pattern.length();

            for (int i = m - 1 + startIndex, j; i < n;) {
                for (j = m - 1; pattern.charAt(j) == text.charAt(i); --i, --j) {
                    if (j == 0) {
                        return i;
                    }
                }

                i += m - j; 
            }

            return NOT_FOUND_INDEX;
        }

        private static Map<Character, Integer> createCharTable(String pattern) {
            int m = pattern.length();
            Map<Character, Integer> table = new HashMap<>(m);

            for (char c : pattern.toCharArray()) {
                table.put(c, m);
            }

            for (int i = 0; i < m - 1; ++i) {
                table.put(pattern.charAt(i), m - 1 - i);
            }

            return table;
        }

        private static int[] createOffsetTable(String pattern) {
            int m = pattern.length();
            int[] table = new int[m];
            int lastPrefixPosition = m;

            for (int i = m - 1; i >= 0; --i) {
                if (isPrefix(pattern, i + 1)) {
                    lastPrefixPosition = i + 1;
                }

                table[m - 1 - i] = lastPrefixPosition - i + m - 1;
            }

            for (int i = 0; i < m - 1; ++i) {
                int suffixLength = suffixLength(pattern, i);
                table[suffixLength] = m - 1 - i + suffixLength;
            }

            return table;
        }

        private static boolean isPrefix(String pattern, int p) {
            int m = pattern.length();

            for (int i = p, j = 0; i < m; ++i, ++j) {
                if (pattern.charAt(i) != pattern.charAt(j)) {
                    return false;
                }
            }

            return true;
        }

        private static int suffixLength(String pattern, int p) {
            int length = 0;
            int m = pattern.length();

            for (int i = p, j = m - 1; 
                    i >= 0 && pattern.charAt(i) == pattern.charAt(j); 
                    --i, --j) {
                ++length;
            }

            return length;
        }
    }
    
    private static final class HorspoolMatcher implements ExactStringMatcher {

        @Override
        public int match(String text, String pattern, int startIndex) {
            startIndex = Math.max(0, startIndex);
            
            if (pattern.isEmpty()) {
                return Math.min(startIndex, text.length());
            }
            
            int n = text.length();
            int m = pattern.length();
            
            Map<Character, Integer> shiftMap = new HashMap<>(m);
            
            for (int i = 0; i < m - 1; ++i) {
                shiftMap.put(pattern.charAt(i), m - 1 - i);
            }
            
            int j = startIndex;
            
            while (j + m <= n) {
                if (pattern.charAt(m - 1) == text.charAt(j + m - 1)) {
                    int i = m - 2;
                    
                    while (i >= 0 && pattern.charAt(i) == text.charAt(j + i)) {
                        --i;
                    }
                    
                    if (i == -1) {
                        return j;
                    }
                }
                
                char tmp = text.charAt(j + m - 1);
                j += shiftMap.getOrDefault(tmp, m);
            }
            
            return NOT_FOUND_INDEX;
        }
    }
}
