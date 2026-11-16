import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Java 21 console program.
 * Reads one line from standard input in UTF-8 and prints word frequencies.
 * Words are sequences of letters from a single alphabet (English or Russian).
 * Counting is case-insensitive, output is in lowercase.
 * Sorting: by decreasing frequency, then alphabetically.
 * Russian alphabetical order places 'ё' immediately after 'е'.
 */
public class WordFrequency {

    public static void main(String[] args) throws IOException {
        // Read one line in UTF-8
        InputStreamReader reader = new InputStreamReader(System.in, StandardCharsets.UTF_8);
        StringBuilder inputBuilder = new StringBuilder();
        int c;
        while ((c = reader.read()) != -1 && c != '\n') {
            inputBuilder.append((char) c);
        }
        String line = inputBuilder.toString();

        // Extract words and count them
        Map<String, Integer> counts = extractAndCountWords(line);

        if (counts.isEmpty()) {
            // Empty output if no words found
            return;
        }

        // Prepare list for sorting
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());

        // Sort by frequency (descending), then alphabetically
        Collections.sort(entries, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                int freqCompare = Integer.compare(o2.getValue(), o1.getValue());
                if (freqCompare != 0) {
                    return freqCompare;
                }
                return compareWords(o1.getKey(), o2.getKey());
            }
        });

        // Print results in UTF-8
        OutputStreamWriter writer = new OutputStreamWriter(System.out, StandardCharsets.UTF_8);
        for (Map.Entry<String, Integer> entry : entries) {
            writer.write(entry.getKey() + ": " + entry.getValue() + "\n");
        }
        writer.flush();
    }

    /**
     * Extracts words from the input line and counts their occurrences.
     * A word is a consecutive sequence of English or Russian letters.
     * Non-letter characters act as separators.
     */
    private static Map<String, Integer> extractAndCountWords(String line) {
        Map<String, Integer> counts = new HashMap<>();
        StringBuilder currentWord = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (isLetter(ch)) {
                currentWord.append(ch);
            } else {
                if (currentWord.length() > 0) {
                    String word = currentWord.toString().toLowerCase();
                    counts.merge(word, 1, Integer::sum);
                    currentWord.setLength(0);
                }
            }
        }

        // Handle the last word if the line ends with a letter
        if (currentWord.length() > 0) {
            String word = currentWord.toString().toLowerCase();
            counts.merge(word, 1, Integer::sum);
        }

        return counts;
    }

    /**
     * Checks if a character is a letter from the supported alphabets.
     */
    private static boolean isLetter(char ch) {
        return (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') ||
               (ch >= 'А' && ch <= 'Я') || (ch >= 'а' && ch <= 'я') ||
               ch == 'Ё' || ch == 'ё';
    }

    /**
     * Compares two lowercase words according to the specified alphabetical rules.
     * English words use standard a-z order.
     * Russian words use а-я order with 'ё' placed immediately after 'е'.
     */
    private static int compareWords(String w1, String w2) {
        int len = Math.min(w1.length(), w2.length());
        for (int i = 0; i < len; i++) {
            char c1 = w1.charAt(i);
            char c2 = w2.charAt(i);
            if (c1 == c2) {
                continue;
            }

            boolean isEng1 = isEnglish(c1);
            boolean isEng2 = isEnglish(c2);

            if (isEng1 && isEng2) {
                return Character.compare(c1, c2);
            }

            if (!isEng1 && !isEng2) {
                int r1 = getRussianOrder(c1);
                int r2 = getRussianOrder(c2);
                if (r1 != r2) {
                    return Integer.compare(r1, r2);
                }
            }

            // Fallback for mixed alphabets (though problem guarantees single alphabet per line)
            return Character.compare(c1, c2);
        }
        return Integer.compare(w1.length(), w2.length());
    }

    private static boolean isEnglish(char ch) {
        return (ch >= 'a' && ch <= 'z');
    }

    /**
     * Returns a numeric order for Russian letters.
     * 'ё' (and 'Ё') gets the index right after 'е'.
     */
    private static int getRussianOrder(char ch) {
        if (ch == 'ё' || ch == 'Ё') {
            return 7; // Index after 'е' (which is 6)
        }
        // Normalize to lowercase to handle 'Ё'
        char lower = Character.toLowerCase(ch);
        return lower - 'а' + 1; // 'а' -> 1, 'б' -> 2, ...
    }
}