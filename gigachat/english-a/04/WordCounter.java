import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Java 21 console program that counts word occurrences in a single UTF-8 input line.
 * 
 * Rules:
 * - A word is a consecutive sequence of letters (A-Z, a-z or А-Я, а-я, Ё, ё).
 * - Non-letter characters act as delimiters.
 * - Counting is case-insensitive; output is in lowercase.
 * - Sorting: by decreasing frequency, then alphabetically.
 *   - Russian alphabetical order places 'ё' immediately after 'е'.
 *   - English uses standard a-z order.
 * - Output format: "word: count" on separate lines.
 */
public class WordCounter {

    // Regex to match one or more consecutive letters from either Latin or Cyrillic alphabets.
    // [A-Za-z] covers English, [А-Яа-яЁё] covers Russian (including Ё/ё).
    private static final Pattern WORD_PATTERN = Pattern.compile("[A-Za-z]+|[А-Яа-яЁё]+");

    public static void main(String[] args) throws IOException {
        // Read a single line from standard input using UTF-8 encoding.
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        String line = reader.readLine();

        // If the input is null (e.g., empty stream) or contains no words, produce empty output.
        if (line == null || line.isEmpty()) {
            return;
        }

        // Count occurrences. HashMap preserves insertion order, but we will sort later anyway.
        Map<String, Integer> counts = new HashMap<>();
        Matcher matcher = WORD_PATTERN.matcher(line);
        while (matcher.find()) {
            String word = matcher.group().toLowerCase(StandardCharsets.UTF_8);
            counts.merge(word, 1, Integer::sum);
        }

        if (counts.isEmpty()) {
            return;
        }

        // Move entries to a list for custom sorting.
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());

        // Custom comparator:
        // 1. Decreasing frequency (higher count first).
        // 2. Alphabetical order respecting Russian 'ё' placement.
        Collections.sort(entries, new Comparator<>() {
            @Override
            public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
                int freqCompare = Integer.compare(e2.getValue(), e1.getValue());
                if (freqCompare != 0) {
                    return freqCompare;
                }
                return compareWords(e1.getKey(), e2.getKey());
            }

            private int compareWords(String w1, String w2) {
                int len = Math.min(w1.length(), w2.length());
                for (int i = 0; i < len; i++) {
                    char c1 = w1.charAt(i);
                    char c2 = w2.charAt(i);
                    if (c1 == c2) {
                        continue;
                    }
                    // If both are Russian letters, apply the specific 'ё' rule.
                    if (isRussianLetter(c1) && isRussianLetter(c2)) {
                        return compareRussianChars(c1, c2);
                    }
                    // For all other cases (English vs English, or mixed), use standard UTF-16 order.
                    // This correctly handles a-z for English.
                    return Character.compare(c1, c2);
                }
                return Integer.compare(w1.length(), w2.length());
            }

            private boolean isRussianLetter(char c) {
                return (c >= 'а' && c <= 'я') || c == 'ё';
            }

            private int compareRussianChars(char c1, char c2) {
                // Normalize 'ё' to a virtual code point right after 'е' (code point of 'е' is 1077).
                // 'ё' (1105) becomes 1078.
                int cp1 = (c1 == 'ё') ? 1078 : c1;
                int cp2 = (c2 == 'ё') ? 1078 : c2;
                return Integer.compare(cp1, cp2);
            }
        });

        // Print the result. System.out uses UTF-8 by default in modern JVMs, 
        // but explicit specification ensures compliance if the charset is overridden.
        for (Map.Entry<String, Integer> entry : entries) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}