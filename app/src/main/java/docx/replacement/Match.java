package docx.replacement;

import java.util.Comparator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Match {

  private Integer startIndex;
  private Integer endIndex;
  private String matchingText;

  public static Comparator<Match> getMatchingTextLengthComparator() {
    return (a, b) ->
        Integer.valueOf(a.getMatchingText().length())
            .compareTo(Integer.valueOf(b.getMatchingText().length()));
  }
}
