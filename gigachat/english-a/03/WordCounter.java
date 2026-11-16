import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
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
 * A word is a consecutive sequence of letters from either the English or Russian alphabet.
 * Non-letter characters act as delimiters. Counting is case-insensitive, output is in lowercase.
 * Words are sorted by decreasing frequency, then alphabetically.
 * Russian alphabetical order places 'ё' immediately after 'е'.
 */
public class WordCounter {

    // Matches sequences of English letters OR Russian letters (including Ё/ё).
    // The alternation ensures we don't mix alphabets within a single word.
    private static final Pattern WORD_PATTERN = Pattern.compile("[A-Za-z]+|[А-Яа-яЁё]+");

    public static void main(String[] args) throws Exception {
        // Explicitly enforce UTF-8 for standard I/O as required by the task
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8), true);

        String line = reader.readLine();
        if (line == null) {
            // End of stream reached without any data
            return;
        }

        // 1. Extract words and count occurrences
        Map<String, Integer> counts = new HashMap<>();
        Matcher matcher = WORD_PATTERN.matcher(line);
        while (matcher.find()) {
            String word = matcher.group().toLowerCase();
            counts.merge(word, 1, Integer::sum);
        }

        if (counts.isEmpty()) {
            // No words found, produce empty output
            return;
        }

        // 2. Transfer map entries to a list for sorting
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());

        // 3. Sort by frequency (descending) and then alphabetically
        Collections.sort(entries, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
                // Primary sort: frequency descending
                int freqCompare = Integer.compare(e2.getValue(), e1.getValue());
                if (freqCompare != 0) {
                    return freqCompare;
                }
                // Secondary sort: alphabetical (custom for Russian/English)
                return compareWordsAlphabetically(e1.getKey(), e2.getKey());
            }
        });

        // 4. Print results
        for (Map.Entry<String, Integer> entry : entries) {
            writer.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    /**
     * Compares two lowercase words alphabetically.
     * Handles Russian and English words separately.
     * For Russian, ensures 'ё' comes immediately after 'е'.
     */
    private static int compareWordsAlphabetically(String w1, String w2) {
        boolean isRus1 = isRussian(w1);
        boolean isRus2 = isRussian(w2);

        // The task guarantees words are from a single alphabet, 
        // but we handle mixed cases defensively by falling back to Unicode order.
        if (isRus1 && isRus2) {
            return compareRussian(w1, w2);
        }
        if (!isRus1 && !isRus2) {
            return w1.compareTo(w2);
        }
        return w1.compareTo(w2);
    }

    private static boolean isRussian(String word) {
        // If the word contains any Cyrillic character, we treat it as Russian.
        return word.matches(".*[\\u0400-\\u04FF].*");
    }

    /**
     * Character-by-character comparison for Russian words.
     * Maps 'ё' and 'Ё' to the code point immediately following 'е' (1077).
     */
    private static int compareRussian(String w1, String w2) {
        int len = Math.min(w1.length(), w2.length());
        for (int i = 0; i < len; i++) {
            int cp1 = w1.codePointAt(i);
            int cp2 = w2.codePointAt(i);

            // Normalize 'ё' (1105) to 1078 (right after 'е' which is 1077)
            if (cp1 == 1077 || cp1 == 1105) cp1 = 1078;
            if (cp2 == 1077 || cp2 == 1105) cp2 = 1078;

            if (cp1 != cp2) {
                return Integer.compare(cp1, cp2);
            }
        }
        // If one word is a prefix of the other, the shorter one comes first
        return Integer.compare(w1.length(), w2.length());
    }
}