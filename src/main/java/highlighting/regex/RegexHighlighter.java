package highlighting.regex;

import highlighting.core.HighlightRegion;
import highlighting.core.SyntaxHighlighter;
import highlighting.presets.MiniJavaTokens;
import java.util.ArrayList;
import java.util.List;

public class RegexHighlighter extends SyntaxHighlighter {

  @Override
  public List<HighlightRegion> collectMatches(String text) {
    List<HighlightRegion> result = new ArrayList<>();

    for (Token token : MiniJavaTokens.defaultTokens()) {
      result.addAll(token.test(text));
    }

    return result;
  }

  @Override
  public List<HighlightRegion> resolveConflicts(List<HighlightRegion> regions) {
    List<HighlightRegion> result = new ArrayList<>();

    for (HighlightRegion candidate : regions) {
      boolean overlaps = false;

      for (HighlightRegion chosen : result) {
        if (overlaps(candidate, chosen)) {
          overlaps = true;
          break;
        }
      }

      if (!overlaps) {
        result.add(candidate);
      }
    }

    return result;
  }

  private boolean overlaps(HighlightRegion a, HighlightRegion b) {
    return a.start() < b.end() && b.start() < a.end();
  }
}
