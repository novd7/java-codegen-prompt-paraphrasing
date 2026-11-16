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
 * A word is a consecutive sequence of letters from either the English (A-Z, a-z)
 * or Russian (А-Я, а-я, Ё, ё) alphabets. Non-letter characters separate words.
 * Output is sorted by decreasing frequency, then alphabetically.
 * Russian alphabetical order places 'ё' immediately after 'е'.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        // Read one line from standard input in UTF-8
        var reader = new InputStreamReader(System.in, StandardCharsets.UTF_8);
        var writer = new OutputStreamWriter(System.out, StandardCharsets.UTF_8);
        StringBuilder inputBuilder = new StringBuilder();
        int c;
        while ((c = reader.read()) != -1 && c != '\n') {
            inputBuilder.append((char) c);
        }
        String line = inputBuilder.toString();

        // Extract words and count them
        Map<String, Integer> counts = new HashMap<>();
        StringBuilder wordBuilder = new StringBuilder();
        boolean inWord = false;
        boolean isRussian = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (isLetter(ch)) {
                if (!inWord) {
                    inWord = true;
                    isRussian = isRussianLetter(ch);
                    wordBuilder.setLength(0);
                } else {
                    // If a letter of a different alphabet appears inside a word,
                    // treat it as a separator according to the strict "one alphabet" rule.
                    if (isRussianLetter(ch) != isRussian) {
                        String word = wordBuilder.toString().toLowerCase();
                        counts.merge(word, 1, Integer::sum);
                        inWord = false;
                        i--; // Re-evaluate the current character as a potential start of a new word
                        continue;
                    }
                }
                wordBuilder.append(ch);
            } else {
                if (inWord) {
                    String word = wordBuilder.toString().toLowerCase();
                    counts.merge(word, 1, Integer::sum);
                    inWord = false;
                }
            }
        }

        // Handle the last word if the line ends with a letter
        if (inWord) {
            String word = wordBuilder.toString().toLowerCase();
            counts.merge(word, 1, Integer::sum);
        }

        // Sort and print
        if (!counts.isEmpty()) {
            List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());
            Collections.sort(entries, new WordComparator());
            for (Map.Entry<String, Integer> entry : entries) {
                writer.write(entry.getKey() + ": " + entry.getValue() + "\n");
            }
        }
        writer.flush();
    }

    private static boolean isLetter(char ch) {
        return (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') ||
               (ch >= 'А' && ch <= 'Я') || (ch >= 'а' && ch <= 'я') ||
               ch == 'Ё' || ch == 'ё';
    }

    private static boolean isRussianLetter(char ch) {
        return (ch >= 'А' && ch <= 'Я') || (ch >= 'а' && ch <= 'я') || ch == 'Ё' || ch == 'ё';
    }

    /**
     * Comparator for Map.Entry<String, Integer>.
     * Primary sort: frequency (descending).
     * Secondary sort: alphabetical.
     *   - English: standard a-z.
     *   - Russian: custom order where 'ё' follows 'е'.
     */
    private static class WordComparator implements Comparator<Map.Entry<String, Integer>> {
        @Override
        public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
            int freqCompare = Integer.compare(e2.getValue(), e1.getValue());
            if (freqCompare != 0) {
                return freqCompare;
            }

            String w1 = e1.getKey();
            String w2 = e2.getKey();

            boolean w1Russian = isRussianWord(w1);
            boolean w2Russian = isRussianWord(w2);

            if (w1Russian && w2Russian) {
                return compareRussian(w1, w2);
            }
            if (!w1Russian && !w2Russian) {
                return w1.compareTo(w2);
            }
            // Fallback for mixed alphabets (should not happen per problem statement,
            // but ensures deterministic behavior if it does)
            return w1.compareTo(w2);
        }

        private boolean isRussianWord(String word) {
            return word.codePoints().anyMatch(cp -> 
                (cp >= 0x0410 && cp <= 0x042F) || // А-Я
                (cp >= 0x0430 && cp <= 0x044F) || // а-я
                cp == 0x0401 || cp == 0x0451      // Ё, ё
            );
        }

        private int compareRussian(String w1, String w2) {
            int len = Math.min(w1.length(), w2.length());
            for (int i = 0; i < len; i++) {
                char c1 = w1.charAt(i);
                char c2 = w2.charAt(i);
                if (c1 != c2) {
                    return Integer.compare(russianOrder(c1), russianOrder(c2));
                }
            }
            return Integer.compare(w1.length(), w2.length());
        }

        private int russianOrder(char ch) {
            switch (ch) {
                case 'ё': return 7;
                case 'Ё': return 7;
                default:
                    // Normalize to lowercase to handle potential uppercase input safely
                    char lower = Character.toLowerCase(ch);
                    if (lower == 'е') return 6;
                    if (lower >= 'а' && lower <= 'я') {
                        int base = lower - 'а';
                        // 'ё' is skipped in the base sequence, so letters after 'е' shift back by 1
                        return (lower > 'е') ? base - 1 : base;
                    }
                    return (int) ch;
            }
        }
    }
}