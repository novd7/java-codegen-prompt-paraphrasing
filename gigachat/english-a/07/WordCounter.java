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
 * - A word is a consecutive sequence of letters from either the English (A-Z, a-z) 
 *   or Russian (А-Я, а-я, Ё, ё) alphabets.
 * - Any non-letter character acts as a separator.
 * - Counting is case-insensitive; output is in lowercase.
 * - Output is sorted by decreasing frequency, then alphabetically.
 * - Russian alphabetical order places 'ё' immediately after 'е'.
 */
public class WordCounter {

    /**
     * Checks if a character is a letter from the allowed English or Russian alphabets.
     */
    private static boolean isAllowedLetter(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')
            || (c >= 'А' && c <= 'Я') || (c >= 'а' && c <= 'я')
            || c == 'Ё' || c == 'ё';
    }

    /**
     * Converts a character to its lowercase equivalent without relying on Locale.
     * This ensures 'Ё' correctly becomes 'ё' and avoids locale-dependent behavior.
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
     * Compares two strings according to the specific alphabetical rules:
     * - If both are purely English (a-z), use standard lexicographical order.
     * - If both are purely Russian (а-я, ё), use Russian order where 'е' < 'ё'.
     * - Otherwise, fall back to standard Unicode lexicographical order.
     */
    private static int compareWords(String s1, String s2) {
        boolean isEng1 = s1.matches("[a-z]+");
        boolean isEng2 = s2.matches("[a-z]+");
        boolean isRus1 = s1.matches("[а-яё]+");
        boolean isRus2 = s2.matches("[а-яё]+");

        if (isEng1 && isEng2) {
            return s1.compareTo(s2);
        }
        if (isRus1 && isRus2) {
            // Russian alphabetical order: 'е' comes before 'ё'
            for (int i = 0; i < Math.min(s1.length(), s2.length()); i++) {
                char c1 = s1.charAt(i);
                char c2 = s2.charAt(i);
                if (c1 != c2) {
                    if (c1 == 'ё') return 1;
                    if (c2 == 'ё') return -1;
                    return c1 - c2;
                }
            }
            return Integer.compare(s1.length(), s2.length());
        }
        return s1.compareTo(s2);
    }

    public static void main(String[] args) {
        String line;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            line = reader.readLine();
        } catch (Exception e) {
            throw new UncheckedIOException("Failed to read input", e);
        }

        if (line == null || line.isEmpty()) {
            return;
        }

        Map<String, Integer> counts = new HashMap<>();
        StringBuilder currentWord = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (isAllowedLetter(c)) {
                currentWord.append(toLower(c));
            } else {
                if (currentWord.length() > 0) {
                    String word = currentWord.toString();
                    counts.merge(word, 1, Integer::sum);
                    currentWord.setLength(0);
                }
            }
        }

        // Process the last word if the line ends with a letter
        if (currentWord.length() > 0) {
            String word = currentWord.toString();
            counts.merge(word, 1, Integer::sum);
        }

        if (counts.isEmpty()) {
            return;
        }

        List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());
        entries.sort(Comparator.<Map.Entry<String, Integer>>comparingInt(Map.Entry::getValue).reversed()
                .thenComparing(e -> e.getKey(), WordCounter::compareWords));

        StringBuilder output = new StringBuilder();
        for (Map.Entry<String, Integer> entry : entries) {
            output.append(entry.getKey()).append(": ").append(entry.getValue()).append('\n');
        }

        System.out.print(output.toString());
    }
}