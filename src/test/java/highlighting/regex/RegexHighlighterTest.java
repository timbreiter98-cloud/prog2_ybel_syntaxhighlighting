package highlighting.regex;

import highlighting.core.HighlightRegion;
import highlighting.presets.MiniJavaColours;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RegexHighlighterTest {

    @Test
    void findsSimpleKeyword() {
        RegexHighlighter highlighter = new RegexHighlighter();

        List<HighlightRegion> regions = highlighter.computeRegions("class Test");

        assertTrue(
            regions.contains(
                new HighlightRegion(0, 5, MiniJavaColours.KEYWORD_COLOUR)));
    }

    @Test
    void keepsCommentAndDiscardsKeywordInsideComment() {
        RegexHighlighter highlighter = new RegexHighlighter();

        List<HighlightRegion> regions = highlighter.computeRegions("// public class");

        assertEquals(1, regions.size());
        assertEquals(
            new HighlightRegion(0, 15, MiniJavaColours.LINE_COMMENT_COLOUR),
            regions.get(0));
    }

    @Test
    void keepsJavadocInsteadOfBlockComment() {
        RegexHighlighter highlighter = new RegexHighlighter();

        List<HighlightRegion> regions = highlighter.computeRegions("/** comment */");

        assertEquals(1, regions.size());
        assertEquals(
            new HighlightRegion(0, 14, MiniJavaColours.JAVADOC_COMMENT_COLOUR),
            regions.get(0));
    }

    @Test
    void adjacentRegionsDoNotOverlap() {
        RegexHighlighter highlighter = new RegexHighlighter();

        List<HighlightRegion> regions = highlighter.computeRegions("\"a\"'b'");

        assertEquals(2, regions.size());
        assertEquals(
            new HighlightRegion(0, 3, MiniJavaColours.STRING_LITERAL_COLOUR),
            regions.get(0));
        assertEquals(
            new HighlightRegion(3, 6, MiniJavaColours.CHAR_LITERAL_COLOUR),
            regions.get(1));
    }

    @Test
    void emptyTextHasNoRegions() {
        RegexHighlighter highlighter = new RegexHighlighter();

        List<HighlightRegion> regions = highlighter.computeRegions("");

        assertTrue(regions.isEmpty());
    }

    @Test
    void textWithoutMatchesHasNoRegions() {
        RegexHighlighter highlighter = new RegexHighlighter();

        List<HighlightRegion> regions = highlighter.computeRegions("abc def ghi");

        assertTrue(regions.isEmpty());
    }
}
