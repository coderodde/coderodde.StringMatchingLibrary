package net.coderodde.string.matching;

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
public final class StringMatchers {

    public static final int NOT_FOUND_INDEX = -1;
    
    public static final class KnuthMorrisPrattMatcher {

        /**
         * This static method implements the 
         * <a href="https://en.wikipedia.org/wiki/Knuth%E2%80%93Morris%E2%80%93Pratt_algorithm">
         * Knuth-Morris-Pratt</a> pattern matching algorithm that runs in time
         * {@code O(n + m)}, where {@code n} is the length of the text to search
         * and {@code m} is the length of the pattern to search.
         * 
         * @param text       the text to search in.
         * @param pattern    the pattern to search for.
         * @param startIndex the character index from which to start matching.
         * @return the smallest index no less than {@code startIndex} of the
         *         position where the pattern occurs if there is a match, or
         *         {@code NOT_FOUND_INDEX} if there is no match.
         */
        public static int match(String text, String pattern, int startIndex) {
            if (pattern.isEmpty()) {
                return 0;
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

        public static int match(String text, String pattern) {
            return match(text, pattern, 0);
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
    
    public static final class AutomatonMatcher {
        
        /**
         * This static method implements the algorithm for exact string matching
         * that constructs a finite automaton, and uses it in order to detect
         * a pattern. The running time is {@code n + sm}, where {@code n} is the
         * length of the text to search, {@code m} is the length of the pattern,
         * and {@code s} is the amount of distinct characters in the pattern.
         * 
         * @param text       the text to search in.
         * @param pattern    the pattern to search for.
         * @param startIndex the character index from which to start matching.
         * @return the smallest index no less than {@code startIndex} of the
         *         position where the pattern occurs if there is a match, or
         *         {@code NOT_FOUND_INDEX} if there is no match.
         */
        public static int match(String text, String pattern, int startIndex) {
            if (pattern.isEmpty()) {
                return 0;
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
        
        public static int match(String text, String pattern) {
            return match(text, pattern, 0);
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
    
    public static final class RabinKarpMatcher {
        
        public static int match(String text, String pattern, int startIndex) {
            int m = pattern.length();
            
            if (m == 0) {
                return 0;
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
        
        public static int match(String text, String pattern) {
            return match(text, pattern, 0);
        }
    }
    
    public static final class ZMatcher {
        
        public static int match(String text, String pattern, int startIndex) {
            StringBuilder sb = new StringBuilder(text.length() + 
                                                 pattern.length() + 1);
            
            sb.append(pattern).append(Character.valueOf('\0')).append(text);
            // Do not create a new string from the StringBuilder, but rather
            // use the builder to access the data.
            int[] zArray = computeZArray(sb);
            int m = pattern.length();
            
            for (int i = Math.max(0, startIndex); i < zArray.length; ++i) {
                if (zArray[i] == m) {
                    return i - m - 1;
                }
            }
            
            return NOT_FOUND_INDEX;
        }
        
        public static int match(String text, String pattern) {
            return match(text, pattern, 0);
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
}
