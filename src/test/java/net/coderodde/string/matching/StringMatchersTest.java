package net.coderodde.string.matching;

import org.junit.Test;
import static org.junit.Assert.*;
import static net.coderodde.string.matching.StringMatchers.*;

public class StringMatchersTest {

    @Test
    public void testKnuthMorrisPrattMatcher() {
        assertEquals(-1, KnuthMorrisPrattMatcher.match("aaa", "aaaa"));
        assertEquals(0,  KnuthMorrisPrattMatcher.match("aaaa", "aaaa"));
        assertEquals(-1, KnuthMorrisPrattMatcher.match("aaaa", "bb"));
        assertEquals(1,  KnuthMorrisPrattMatcher.match("abbb", "bb"));
        assertEquals(2,  KnuthMorrisPrattMatcher.match("abcc", "cc"));
        
        assertEquals(5, KnuthMorrisPrattMatcher.match("aaaaaaab", "aab"));
        assertEquals(4, KnuthMorrisPrattMatcher.match("ababababaca", 
                                                      "ababaca"));
        
        assertTrue("".indexOf("") == KnuthMorrisPrattMatcher.match("", ""));
        assertTrue("".indexOf("a") == KnuthMorrisPrattMatcher.match("", "a"));
        assertTrue("a".indexOf("") == KnuthMorrisPrattMatcher.match("a", ""));
        assertTrue("hello".indexOf("ello", -2) == 
                KnuthMorrisPrattMatcher.match("hello", "ello", -2));
        
        assertEquals(-1, KnuthMorrisPrattMatcher.match("aabaab", "aab", 5));
        assertEquals(-1, KnuthMorrisPrattMatcher.match("aabaab", "aab", 4));
        assertEquals(3, KnuthMorrisPrattMatcher.match("aabaab", "aab", 3));
        assertEquals(3, KnuthMorrisPrattMatcher.match("aabaab", "aab", 2));
        assertEquals(3, KnuthMorrisPrattMatcher.match("aabaab", "aab", 1));
        assertEquals(0, KnuthMorrisPrattMatcher.match("aabaab", "aab", 0));
        assertEquals(0, KnuthMorrisPrattMatcher.match("aabaab", "aab", -1));
        assertEquals(0, KnuthMorrisPrattMatcher.match("aabaab", "aab", -2));
    }

    @Test
    public void testAutomatonMatcher() {
        assertEquals(0, AutomatonMatcher.match("acacaga", "acacaga"));
        assertEquals(-1, AutomatonMatcher.match("aaa", "aaaa"));
        assertEquals(0,  AutomatonMatcher.match("aaaa", "aaaa"));
        assertEquals(-1, AutomatonMatcher.match("aaaa", "bb"));
        assertEquals(1,  AutomatonMatcher.match("abbb", "bb"));
        assertEquals(2,  AutomatonMatcher.match("abcc", "cc"));
        
        assertEquals(5, AutomatonMatcher.match("aaaaaaab", "aab"));
        assertEquals(4, AutomatonMatcher.match("ababababaca", "ababaca"));
        
        assertTrue("".indexOf("") == AutomatonMatcher.match("", ""));
        assertTrue("".indexOf("a") == AutomatonMatcher.match("", "a"));
        assertTrue("a".indexOf("") == AutomatonMatcher.match("a", ""));
        assertTrue("hello".indexOf("ello", -2) == 
                AutomatonMatcher.match("hello", "ello", -2));
        
        assertEquals(-1, AutomatonMatcher.match("aabaab", "aab", 5));
        assertEquals(-1, AutomatonMatcher.match("aabaab", "aab", 4));
        assertEquals(3, AutomatonMatcher.match("aabaab", "aab", 3));
        assertEquals(3, AutomatonMatcher.match("aabaab", "aab", 2));
        assertEquals(3, AutomatonMatcher.match("aabaab", "aab", 1));
        assertEquals(0, AutomatonMatcher.match("aabaab", "aab", 0));
        assertEquals(0, AutomatonMatcher.match("aabaab", "aab", -1));
        assertEquals(0, AutomatonMatcher.match("aabaab", "aab", -2));
    }

    @Test
    public void testRabinKarpMatcher() {
        assertEquals(0, RabinKarpMatcher.match("acacaga", "acacaga"));
        assertEquals(-1, RabinKarpMatcher.match("aaa", "aaaa"));
        assertEquals(0,  RabinKarpMatcher.match("aaaa", "aaaa"));
        assertEquals(-1, RabinKarpMatcher.match("aaaa", "bb"));
        assertEquals(1,  RabinKarpMatcher.match("abbb", "bb"));
        assertEquals(2,  RabinKarpMatcher.match("abcc", "cc"));
        
        assertEquals(5, RabinKarpMatcher.match("aaaaaaab", "aab"));
        assertEquals(4, RabinKarpMatcher.match("ababababaca", "ababaca"));
        
        assertTrue("".indexOf("") == RabinKarpMatcher.match("", ""));
        assertTrue("".indexOf("a") == RabinKarpMatcher.match("", "a"));
        assertTrue("a".indexOf("") == RabinKarpMatcher.match("a", ""));
        assertTrue("hello".indexOf("ello", -2) == 
                RabinKarpMatcher.match("hello", "ello", -2));
        
        assertEquals(-1, RabinKarpMatcher.match("aabaab", "aab", 5));
        assertEquals(-1, RabinKarpMatcher.match("aabaab", "aab", 4));
        assertEquals(3, RabinKarpMatcher.match("aabaab", "aab", 3));
        assertEquals(3, RabinKarpMatcher.match("aabaab", "aab", 2));
        assertEquals(3, RabinKarpMatcher.match("aabaab", "aab", 1));
        assertEquals(0, RabinKarpMatcher.match("aabaab", "aab", 0));
        assertEquals(0, RabinKarpMatcher.match("aabaab", "aab", -1));
        assertEquals(0, RabinKarpMatcher.match("aabaab", "aab", -2));
        assertEquals(6, RabinKarpMatcher.match("aaaaaaaab", "aab"));
    }
}
