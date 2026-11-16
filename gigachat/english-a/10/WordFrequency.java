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
 * Words are sequences of letters from a single alphabet (Russian or English).
 * Counting is case-insensitive, output is in lowercase.
 * Sorting: by decreasing frequency, then alphabetically.
 * Russian alphabetical order places 'ё' immediately after 'е'.
 */
public class WordFrequency {

    public static void main(String[] args) throws IOException {
        // Explicitly enforce UTF-8 for standard streams
        InputStreamReader reader = new InputStreamReader(System.in, StandardCharsets.UTF_8);
        OutputStreamWriter writer = new OutputStreamWriter(System.out, StandardCharsets.UTF_8);

        String line = reader.readLine();
        if (line == null) {
            writer.flush();
            return;
        }

        // 1. Extract words
        List<String> words = extractWords(line);

        if (words.isEmpty()) {
            writer.flush();
            return;
        }

        // 2. Count frequencies (case-insensitive)
        Map<String, Integer> counts = new HashMap<>();
        for (String w : words) {
            String lower = w.toLowerCase(Locale.ROOT);
            counts.merge(lower, 1, Integer::sum);
        }

        // 3. Prepare list for sorting
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());

        // 4. Sort by frequency (desc) then alphabetically
        entries.sort(new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
                int freqCompare = Integer.compare(e2.getValue(), e1.getValue());
                if (freqCompare != 0) {
                    return freqCompare;
                }
                return compareWordsAlphabetically(e1.getKey(), e2.getKey());
            }
        });

        // 5. Print results
        for (Map.Entry<String, Integer> e : entries) {
            writer.write(e.getKey() + ": " + e.getValue() + "\n");
        }
        writer.flush();
    }

    /**
     * Splits the input line into words. A word is a consecutive sequence of letters.
     * Non-letter characters act as delimiters.
     */
    private static List<String> extractWords(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (isLetter(c)) {
                current.append(c);
            } else {
                if (current.length() > 0) {
                    result.add(current.toString());
                    current.setLength(0);
                }
            }
        }
        if (current.length() > 0) {
            result.add(current.toString());
        }
        return result;
    }

    /**
     * Checks if a character is a letter from either the English or Russian alphabet.
     */
    private static boolean isLetter(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') ||
               (c >= 'А' && c <= 'Я') || (c >= 'а' && c <= 'я') ||
               c == 'Ё' || c == 'ё';
    }

    /**
     * Compares two lowercase words alphabetically.
     * Handles mixed alphabets by comparing character by character.
     * For Russian letters, ensures 'ё' comes immediately after 'е'.
     */
    private static int compareWordsAlphabetically(String w1, String w2) {
        int len = Math.min(w1.length(), w2.length());
        for (int i = 0; i < len; i++) {
            char c1 = w1.charAt(i);
            char c2 = w2.charAt(i);
            if (c1 == c2) {
                continue;
            }
            int rank1 = getCharRank(c1);
            int rank2 = getCharRank(c2);
            return Integer.compare(rank1, rank2);
        }
        return Integer.compare(w1.length(), w2.length());
    }

    /**
     * Returns a numeric rank for a lowercase letter to facilitate alphabetical sorting.
     * English 'a'-'z' get ranks 0-25.
     * Russian 'а'-'е' get ranks 26-31.
     * Russian 'ё' gets rank 32 (immediately after 'е').
     * Russian 'ж'-'я' get ranks 33-61.
     */
    private static int getCharRank(char c) {
        if (c >= 'a' && c <= 'z') {
            return c - 'a';
        }
        // At this point c is guaranteed to be a Russian letter
        if (c >= 'а' && c <= 'е') {
            return 26 + (c - 'а');
        }
        if (c == 'ё') {
            return 32;
        }
        // 'ж' (code 0x436 / 1078) should get rank 33
        return 33 + (c - 'ж');
    }
}