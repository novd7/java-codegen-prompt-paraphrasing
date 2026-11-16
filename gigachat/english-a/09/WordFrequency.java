import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Java 21 console program.
 * Reads one line from standard input in UTF-8 and prints word frequencies.
 * A word is a consecutive sequence of letters from either the English or Russian alphabet.
 * Non-letter characters are delimiters. Counting is case-insensitive, output is lowercase.
 * Sorting: by decreasing frequency, then alphabetically. 
 * Russian alphabetical order places 'ё' immediately after 'е'.
 */
public class WordFrequency {

    // Russian letters (including Ё/ё) in the required alphabetical order
    private static final String RUSSIAN_ALPHABET = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя";

    public static void main(String[] args) throws IOException {
        // Explicitly enforce UTF-8 for the console
        var reader = new InputStreamReader(System.in, StandardCharsets.UTF_8);
        var writer = new OutputStreamWriter(System.out, StandardCharsets.UTF_8);

        StringBuilder lineBuilder = new StringBuilder();
        int c;
        // Read exactly one line (up to \n or EOF)
        while ((c = reader.read()) != -1 && c != '\n') {
            lineBuilder.append((char) c);
        }
        String line = lineBuilder.toString();

        Map<String, Integer> frequencies = new HashMap<>();
        StringBuilder wordBuilder = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (isLetter(ch)) {
                wordBuilder.append(ch);
            } else {
                if (wordBuilder.length() > 0) {
                    String word = wordBuilder.toString().toLowerCase();
                    frequencies.merge(word, 1, Integer::sum);
                    wordBuilder.setLength(0);
                }
            }
        }
        // Handle the last word if the line ends with a letter
        if (wordBuilder.length() > 0) {
            String word = wordBuilder.toString().toLowerCase();
            frequencies.merge(word, 1, Integer::sum);
        }

        if (!frequencies.isEmpty()) {
            List<Map.Entry<String, Integer>> entries = new ArrayList<>(frequencies.entrySet());
            entries.sort(Comparator
                    // Primary: frequency descending
                    .comparingInt((Map.Entry<String, Integer> e) -> e.getValue()).reversed()
                    // Secondary: custom alphabetical order
                    .thenComparing(e -> e.getKey(), WordFrequency::compareWords));

            for (Map.Entry<String, Integer> entry : entries) {
                writer.write(entry.getKey() + ": " + entry.getValue() + "\n");
            }
        }
        writer.flush();
    }

    private static boolean isLetter(char ch) {
        return (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch >= 'А' && ch <= 'Я') 
               || (ch >= 'а' && ch <= 'я') || ch == 'Ё' || ch == 'ё';
    }

    /**
     * Compares two words according to the problem's alphabetical rules.
     * Assumes both words are already in lowercase.
     */
    private static int compareWords(String w1, String w2) {
        int len = Math.min(w1.length(), w2.length());
        for (int i = 0; i < len; i++) {
            char c1 = w1.charAt(i);
            char c2 = w2.charAt(i);
            if (c1 == c2) {
                continue;
            }
            boolean isRus1 = isRussian(c1);
            boolean isRus2 = isRussian(c2);

            // If alphabets differ, English letters come before Russian letters
            if (isRus1 != isRus2) {
                return isRus1 ? 1 : -1;
            }

            if (isRus1) {
                // Both are Russian: use the custom index map
                int idx1 = RUSSIAN_ALPHABET.indexOf(c1);
                int idx2 = RUSSIAN_ALPHABET.indexOf(c2);
                return Integer.compare(idx1, idx2);
            } else {
                // Both are English: standard Unicode order works for a-z
                return Character.compare(c1, c2);
            }
        }
        // One word is a prefix of the other
        return Integer.compare(w1.length(), w2.length());
    }

    private static boolean isRussian(char ch) {
        return (ch >= 'а' && ch <= 'я') || ch == 'ё';
    }
}