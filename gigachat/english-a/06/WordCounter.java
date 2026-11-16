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
 * - A word is a consecutive sequence of letters from a single alphabet (English A-Z/a-z or Russian А-Я/а-я/Ё/ё).
 * - Non-letter characters act as separators.
 * - Counting is case-insensitive; output is in lowercase.
 * - Sorting: by decreasing frequency, then alphabetically.
 *   - Russian alphabetical order: а, б, в, г, д, е, ё, ж...
 *   - English alphabetical order: a, b, c...
 * - Output format: "word: count" on separate lines.
 * - If no words are found, the program produces no output.
 */
public class WordCounter {

    // Regex to match sequences of English or Russian letters.
    // [A-Za-z] covers English. [А-Яа-яЁё] covers Russian including Ё/ё.
    private static final Pattern WORD_PATTERN = Pattern.compile("[A-Za-z]+|[А-Яа-яЁё]+");

    public static void main(String[] args) throws IOException {
        // Read the single line from standard input using UTF-8
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            String line = reader.readLine();
            
            if (line == null || line.isEmpty()) {
                return;
            }

            // Count occurrences
            Map<String, Integer> counts = new HashMap<>();
            Matcher matcher = WORD_PATTERN.matcher(line);
            while (matcher.find()) {
                String word = matcher.group().toLowerCase(StandardCharsets.UTF_8);
                counts.merge(word, 1, Integer::sum);
            }

            if (counts.isEmpty()) {
                return;
            }

            // Move entries to a list for custom sorting
            List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());

            // Sort by frequency (descending), then by custom alphabetical order (ascending)
            Collections.sort(entries, new Comparator<Map.Entry<String, Integer>>() {
                @Override
                public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
                    // 1. Frequency: higher counts come first
                    int freqCompare = Integer.compare(e2.getValue(), e1.getValue());
                    if (freqCompare != 0) {
                        return freqCompare;
                    }

                    // 2. Alphabetical order
                    String w1 = e1.getKey();
                    String w2 = e2.getKey();

                    // Determine language by checking the first character's script
                    boolean isRussian1 = isRussian(w1);
                    boolean isRussian2 = isRussian(w2);

                    // If languages differ, English words come before Russian words
                    if (isRussian1 != isRussian2) {
                        return isRussian1 ? 1 : -1;
                    }

                    // Same language: compare character by character using the correct alphabet
                    int len = Math.min(w1.length(), w2.length());
                    for (int i = 0; i < len; i++) {
                        char c1 = w1.charAt(i);
                        char c2 = w2.charAt(i);
                        if (c1 != c2) {
                            return isRussian1 ? compareRussianChars(c1, c2) : Character.compare(c1, c2);
                        }
                    }
                    // If one word is a prefix of another, the shorter one comes first
                    return Integer.compare(w1.length(), w2.length());
                }
            });

            // Print results in UTF-8
            for (Map.Entry<String, Integer> entry : entries) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }
    }

    private static boolean isRussian(String word) {
        char first = word.charAt(0);
        return (first >= 'А' && first <= 'Я') || (first >= 'а' && first <= 'я') || first == 'Ё' || first == 'ё';
    }

    /**
     * Compares two Russian characters according to the order: а, б, в, г, д, е, ё, ж...
     */
    private static int compareRussianChars(char c1, char c2) {
        int i1 = getRussianIndex(c1);
        int i2 = getRussianIndex(c2);
        return Integer.compare(i1, i2);
    }

    /**
     * Returns the 0-based index of a Russian letter in the custom alphabet.
     * 'е' -> 5, 'ё' -> 6, 'ж' -> 7, and so on.
     */
    private static int getRussianIndex(char c) {
        // Handle Ё/ё first as they are out of the main contiguous block
        if (c == 'ё' || c == 'Ё') {
            return 6;
        }

        // Normalize to lowercase to handle both cases uniformly
        char lower = Character.toLowerCase(c);
        
        if (lower >= 'а' && lower <= 'е') {
            return lower - 'а';
        } else {
            // 'ж' is the 7th letter (index 6 + 1), so we shift the index by 1
            return lower - 'а' + 1;
        }
    }
}