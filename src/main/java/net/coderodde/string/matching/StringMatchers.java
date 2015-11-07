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

            return -1;
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
        
        public static int match(String text, String pattern, int startIndex) {
            if (pattern.isEmpty()) {
                return 0;
            }
            
            int n = text.length();
            Integer m = pattern.length();
            
            if (m > n) {
                return -1;
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
                
                System.out.println("Transition function:");
                System.out.println(this);
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
}
