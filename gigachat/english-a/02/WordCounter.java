import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Java 21 console program that counts word occurrences in a single UTF-8 input line.
 * 
 * Rules:
 * - A word is a consecutive sequence of letters (A-Z, a-z or А-Я, а-я, Ё, ё).
 * - Any non-letter character is a separator.
 * - Counting is case-insensitive; output is in lowercase.
 * - Sorting: by decreasing frequency, then alphabetically.
 *   - Russian alphabetical order places 'ё' immediately after 'е'.
 *   - English uses standard a-z order.
 * - Output format: "word: count" on separate lines.
 * - If no words are found, the program produces no output.
 */
public class WordCounter {

    /**
     * Checks if a character is a letter from the allowed English or Russian alphabets.
     * 
     * @param c character to check
     * @return true if the character is a valid letter, false otherwise
     */
    private static boolean isLetter(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')
                || (c >= 'А' && c <= 'Я') || (c >= 'а' && c <= 'я')
                || c == 'Ё' || c == 'ё';
    }

    /**
     * Converts a character to its lowercase equivalent without relying on Locale.
     * This ensures consistent behavior for both English and Russian characters.
     * 
     * @param c character to convert
     * @return lowercase character
     */
    private static char toLower(char c) {
        if (c >= 'A' && c <= 'Z') {
            return (char) (c + ('a' - 'A'));
        }
        if (c >= 'А' && c <= 'Я') {
            return (char) (c + ('а' - 'А'));
        }
        if (c == 'Ё') {
            return 'ё';
        }
        return c;
    }

    /**
     * Compares two words according to the specific Russian and English alphabetical rules.
     * 'ё' is treated as coming immediately after 'е'.
     * 
     * @param a first word
     * @param b second word
     * @return negative if a < b, positive if a > b, 0 if equal
     */
    private static int compareWords(String a, String b) {
        int len = Math.min(a.length(), b.length());
        for (int i = 0; i < len; i++) {
            char ca = a.charAt(i);
            char cb = b.charAt(i);
            if (ca != cb) {
                // Handle Russian 'е'/'ё' specific ordering
                if (ca == 'е' && cb == 'ё') return -1;
                if (ca == 'ё' && cb == 'е') return 1;
                if (cb == 'е' && ca == 'ё') return 1;
                if (cb == 'ё' && ca == 'е') return -1;
                // Standard character comparison for all other cases
                return ca - cb;
            }
        }
        // If one word is a prefix of the other, the shorter one comes first
        return a.length() - b.length();
    }

    public static void main(String[] args) {
        // Read the single input line in UTF-8
        String line;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            line = reader.readLine();
        } catch (Exception e) {
            throw new UncheckedIOException("Failed to read input", e);
        }

        if (line == null || line.isEmpty()) {
            return;
        }

        // Parse words and count occurrences
        Map<String, Integer> counts = new HashMap<>();
        StringBuilder currentWord = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (isLetter(c)) {
                currentWord.append(toLower(c));
            } else {
                if (currentWord.length() > 0) {
                    String word = currentWord.toString();
                    counts.merge(word, 1, Integer::sum);
                    currentWord.setLength(0);
                }
            }
        }

        // Add the last word if the line ends with a letter
        if (currentWord.length() > 0) {
            String word = currentWord.toString();
            counts.merge(word, 1, Integer::sum);
        }

        if (counts.isEmpty()) {
            return;
        }

        // Sort words by frequency (descending) and then alphabetically
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());
        entries.sort((e1, e2) -> {
            int freqCompare = Integer.compare(e2.getValue(), e1.getValue());
            if (freqCompare != 0) {
                return freqCompare;
            }
            return compareWords(e1.getKey(), e2.getKey());
        });

        // Print results in UTF-8
        StringBuilder output = new StringBuilder();
        for (Map.Entry<String, Integer> entry : entries) {
            output.append(entry.getKey()).append(": ").append(entry.getValue()).append('\n');
        }

        System.out.print(output.toString());
    }
}