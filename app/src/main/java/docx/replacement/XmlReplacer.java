package docx.replacement;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class XmlReplacer {
  private File input;

  public String replace(Map<String, String> replacementPerVariable) throws IOException {
    String content = getFileContent();

    String[] variables = new String[replacementPerVariable.keySet().size()];
    replacementPerVariable.keySet().toArray(variables);

    Map<String, List<Match>> matchesPerVariable = getMatchesFromText(variables, content);

    Map<String, Match> bestMatchPerVariable = new HashMap<>();
    Comparator<Match> comparator = Match.getMatchingTextLengthComparator();

    matchesPerVariable.forEach(
        (variable, matches) -> {
          bestMatchPerVariable.put(variable, matches.stream().min(comparator).get());
        });

    for (String variable : variables) {
      String replacement = new String(replacementPerVariable.get(variable).getBytes(), UTF_8);
      content = content.replace(bestMatchPerVariable.get(variable).getMatchingText(), replacement);
    }

    return content;
  }

  private String getFileContent() throws IOException {
    return Files.lines(getInput().toPath()).collect(Collectors.joining());
  }

  private Map<String, List<Match>> getMatchesFromText(String[] variables, String content) {
    Map<String, List<Match>> matchesPerVariable = new HashMap<>();
    for (String variable : variables) matchesPerVariable.put(variable, new ArrayList<>());

    for (String variable : variables) {
      int startAt = 0;
      StringBuilder patternBuilder = new StringBuilder();

      patternBuilder.append("\\$.*?\\{");
      for (Character character : variable.toCharArray()) patternBuilder.append(".*?" + character);
      patternBuilder.append(".*?\\}");

      Pattern pattern = Pattern.compile(patternBuilder.toString());
      Matcher matcher = pattern.matcher(content);

      while (matcher.find(startAt)) {
        matchesPerVariable
            .get(variable)
            .add(new Match(matcher.start(), matcher.end(), matcher.group()));
        startAt = matcher.start() + 1;
      }
    }

    return matchesPerVariable;
  }
}
