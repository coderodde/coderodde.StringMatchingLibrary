package net.coderodde.string.matching;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class ExactStringMatchersTest {

    @Parameterized.Parameters
    public static List<Object[]> getParameters() {
        return Arrays.asList(
            new Object[]{ ExactStringMatchers.getKnuthMorrisPrattMatcher() },
            new Object[]{ ExactStringMatchers.getFiniteAutomatonMatcher()  },
            new Object[]{ ExactStringMatchers.getRabinKarpMatcher()        },
            new Object[]{ ExactStringMatchers.getZMatcher()                },
            new Object[]{ ExactStringMatchers.getBoyerMooreMatcher()       },
            new Object[]{ ExactStringMatchers.getNaiveBoyerMooreMatcher()  },
            new Object[]{ ExactStringMatchers.getHorspoolMatcher()         }
        );
    }

    private final ExactStringMatcher matcher;

    public ExactStringMatchersTest(ExactStringMatcher matcher) {
        this.matcher = matcher;
    }

    @Test
    public void testMatcher() {
        assertEquals(0,  matcher.match("acacaga", "acacaga"));
        assertEquals(-1, matcher.match("aaa", "aaaa"));
        assertEquals(0,  matcher.match("aaaa", "aaaa"));
        assertEquals(-1, matcher.match("aaaa", "bb"));
        assertEquals(1,  matcher.match("abbb", "bb"));
        assertEquals(2,  matcher.match("abcc", "cc"));

        assertEquals(5, matcher.match("aaaaaaab", "aab"));
        assertEquals(4, matcher.match("ababababaca", "ababaca"));

        assertTrue("".indexOf("")  == matcher.match("", ""));
        assertTrue("".indexOf("a") == matcher.match("", "a"));
        assertTrue("a".indexOf("") == matcher.match("a", ""));
        assertTrue("hello".indexOf("ello", -2) == 
                matcher.match("hello", "ello", -2));

        assertEquals(-1, matcher.match("aabaab", "aab", 5));
        assertEquals(-1, matcher.match("aabaab", "aab", 4));
        assertEquals(3,  matcher.match("aabaab", "aab", 3));
        assertEquals(3,  matcher.match("aabaab", "aab", 2));
        assertEquals(3,  matcher.match("aabaab", "aab", 1));
        assertEquals(0,  matcher.match("aabaab", "aab", 0));
        assertEquals(0,  matcher.match("aabaab", "aab", -1));
        assertEquals(0,  matcher.match("aabaab", "aab", -2));
        assertEquals(6,  matcher.match("aaaaaaaab", "aab"));
        
        for (int i = -2; i <= 3; ++i) {
            assertTrue("fun".indexOf("", i) == matcher.match("fun", "", i));
        }
        
        assertTrue("fun".indexOf("", 4) == matcher.match("fun", "", 4));
    }
}
