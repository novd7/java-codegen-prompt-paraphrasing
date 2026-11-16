import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class WordFrequency {
    private static final Pattern WORD = Pattern.compile("[А-Яа-яЁёA-Za-z]+");

    public static String summarize(String text) {
        Map<String, Integer> counts = new HashMap<>();
        var words = WORD.matcher(text);
        while (words.find()) {
            counts.merge(words.group().toLowerCase(Locale.ROOT), 1, Integer::sum);
        }

        var result = new StringBuilder();
        counts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey(Comparator.comparing(WordFrequency::alphabeticalKey)
                                .thenComparing(Comparator.naturalOrder()))))
                .forEach(entry -> result.append(entry.getKey()).append(": ")
                        .append(entry.getValue()).append('\n'));
        return result.toString();
    }

    private static String alphabeticalKey(String word) {
        // Insert ё immediately after е in the Russian alphabet.
        var key = new StringBuilder();
        for (char letter : word.toCharArray()) {
            if (letter == 'ё') {
                key.append('ж');
            } else if (letter >= 'ж' && letter <= 'я') {
                key.append((char) (letter + 1));
            } else {
                key.append(letter);
            }
        }
        return key.toString();
    }

    public static void main(String[] args) throws IOException {
        var input = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        var output = new PrintWriter(System.out, false, StandardCharsets.UTF_8);
        String line = input.readLine();
        if (line != null) {
            output.print(summarize(line));
        }
        output.flush();
    }
}
