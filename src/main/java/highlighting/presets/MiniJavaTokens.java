package highlighting.presets;

import highlighting.regex.Token;
import java.util.List;
import java.util.regex.Pattern;

public final class MiniJavaTokens {

  private MiniJavaTokens() {}

  public static List<Token> defaultTokens() {
    return List.of(
        // Reihenfolge ist wichtig:
        // Javadoc vor normalem Block-Kommentar, weil /** ... */ sonst auch als /* ... */ passen
        // kann.
        Token.of(
            Pattern.compile("/\\*\\*.*?\\*/", Pattern.DOTALL),
            MiniJavaColours.JAVADOC_COMMENT_COLOUR),
        Token.of(
            Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL), MiniJavaColours.BLOCK_COMMENT_COLOUR),
        Token.of(Pattern.compile("//.*"), MiniJavaColours.LINE_COMMENT_COLOUR),
        Token.of(Pattern.compile("\"([^\"\\\\]|\\\\.)*\""), MiniJavaColours.STRING_LITERAL_COLOUR),
        Token.of(Pattern.compile("'([^'\\\\]|\\\\.)'"), MiniJavaColours.CHAR_LITERAL_COLOUR),
        Token.of(
            Pattern.compile("\\b(package|import|class|public|private|final|return|null|new)\\b"),
            MiniJavaColours.KEYWORD_COLOUR),
        Token.of(Pattern.compile("@[A-Za-z][A-Za-z0-9_-]*"), MiniJavaColours.ANNOTATION_COLOUR));
  }
}
