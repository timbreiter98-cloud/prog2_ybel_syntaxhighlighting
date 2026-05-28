package highlighting.presets;

import static org.junit.jupiter.api.Assertions.*;

import highlighting.core.HighlightRegion;
import java.util.List;
import org.junit.jupiter.api.Test;

class MiniJavaTokensTest {

  @Test
  void stringLiteralIsFound() {
    List<HighlightRegion> regions = collect("\"Hallo\"");

    assertTrue(regions.contains(new HighlightRegion(0, 7, MiniJavaColours.STRING_LITERAL_COLOUR)));
  }

  @Test
  void charLiteralIsFound() {
    List<HighlightRegion> regions = collect("'a'");

    assertTrue(regions.contains(new HighlightRegion(0, 3, MiniJavaColours.CHAR_LITERAL_COLOUR)));
  }

  @Test
  void keywordIsFoundAsWholeWord() {
    List<HighlightRegion> regions = collect("class Test");

    assertTrue(regions.contains(new HighlightRegion(0, 5, MiniJavaColours.KEYWORD_COLOUR)));
  }

  @Test
  void keywordInsideIdentifierIsNotFound() {
    List<HighlightRegion> regions = collect("myclass");

    assertFalse(regions.contains(new HighlightRegion(2, 7, MiniJavaColours.KEYWORD_COLOUR)));
  }

  @Test
  void annotationIsFound() {
    List<HighlightRegion> regions = collect("@Override");

    assertTrue(regions.contains(new HighlightRegion(0, 9, MiniJavaColours.ANNOTATION_COLOUR)));
  }

  @Test
  void lineCommentIsFound() {
    List<HighlightRegion> regions = collect("// public class");

    assertTrue(regions.contains(new HighlightRegion(0, 15, MiniJavaColours.LINE_COMMENT_COLOUR)));
  }

  @Test
  void blockCommentIsFound() {
    List<HighlightRegion> regions = collect("/* public class */");

    assertTrue(regions.contains(new HighlightRegion(0, 18, MiniJavaColours.BLOCK_COMMENT_COLOUR)));
  }

  @Test
  void javadocCommentIsFound() {
    List<HighlightRegion> regions = collect("/** public class */");

    assertTrue(
        regions.contains(new HighlightRegion(0, 19, MiniJavaColours.JAVADOC_COMMENT_COLOUR)));
  }

  private List<HighlightRegion> collect(String text) {
    return MiniJavaTokens.defaultTokens().stream()
        .map(token -> token.test(text))
        .flatMap(List::stream)
        .toList();
  }
}
