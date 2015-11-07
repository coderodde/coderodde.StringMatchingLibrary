package net.coderodde.string.matching;

/**
 * This interface defines the API for exact string matching algorithms.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Nov 7, 2015)
 */
public interface ExactStringMatcher {
    
    /**
     * The index returned whenever there is no match.
     */
    public int NOT_FOUND_INDEX = -1;
    
    /**
     * Attempts to find an occurrence of {@code pattern} in the {@code text} 
     * omitting first {@code startIndex} characters from {@code text}.
     * 
     * @param text       the string in which to search for the pattern.
     * @param pattern    the string to find in the {@code text}.
     * @param startIndex the number of leftmost characters to omit from 
     *                   consideration.
     * @return the smallest index no less than {@code startIndex} where the 
     *         {@code pattern} occurs in {@code text}, or 
     *         {@code NOT_FOUND_INDEX} if there is no match.
     */
    public int match(String text, String pattern, int startIndex);
    
    /**
     * Attempts to find an occurrence of {@code pattern} in the {@code text} 
     * searching in the entire string {@code text}.
     * 
     * @param text    the string in which to search for the pattern.
     * @param pattern the string to find in the {@code text}.
     * @return the smallest index no less than {@code startIndex} where the 
     *         {@code pattern} occurs in {@code text}, or 
     *         {@code NOT_FOUND_INDEX} if there is no match.
     */
    public default int match(String text, String pattern) {
        return match(text, pattern, 0);
    }
}
