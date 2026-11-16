import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
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
public class Main {

    // Checks if a character is a letter from the allowed alphabets.
    private static boolean isLetter(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || 
               (c >= 'А' && c <= 'Я') || (c >= 'а' && c <= 'я') || 
               c == 'Ё' || c == 'ё';
    }

    // Custom comparator for Russian and English words.
    // 'ё' is placed immediately after 'е'.
    private static int compareWords(String s1, String s2) {
        int len = Math.min(s1.length(), s2.length());
        for (int i = 0; i < len; i++) {
            char c1 = s1.charAt(i);
            char c2 = s2.charAt(i);
            if (c1 == c2) {
                continue;
            }
            int v1 = getOrderValue(c1);
            int v2 = getOrderValue(c2);
            return Integer.compare(v1, v2);
        }
        return Integer.compare(s1.length(), s2.length());
    }

    // Maps characters to their sort order values.
    private static int getOrderValue(char c) {
        if (c >= 'a' && c <= 'z') {
            return c - 'a';
        }
        if (c >= 'а' && c <= 'е') {
            return c - 'а';
        }
        if (c == 'ё') {
            return ('е' - 'а') + 1; // 6
        }
        if (c >= 'ж' && c <= 'я') {
            return c - 'а';
        }
        // Fallback for any unexpected characters (should not occur for valid words)
        return c;
    }

    public static void main(String[] args) {
        String line;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            line = reader.readLine();
        } catch (Exception e) {
            // If reading fails, treat as empty input to produce empty output
            line = "";
        }

        if (line == null) {
            line = "";
        }

        Map<String, Integer> counts = new HashMap<>();
        StringBuilder currentWord = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (isLetter(c)) {
                // Convert to lowercase for case-insensitive counting
                currentWord.append(Character.toLowerCase(c));
            } else {
                if (currentWord.length() > 0) {
                    String word = currentWord.toString();
                    counts.merge(word, 1, Integer::sum);
                    currentWord.setLength(0);
                }
            }
        }

        // Handle the last word if the line ends with a letter
        if (currentWord.length() > 0) {
            String word = currentWord.toString();
            counts.merge(word, 1, Integer::sum);
        }

        if (counts.isEmpty()) {
            // Empty output if no words found
            return;
        }

        List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());

        // Sort by frequency (descending) and then alphabetically
        entries.sort((e1, e2) -> {
            int freqCompare = Integer.compare(e2.getValue(), e1.getValue());
            if (freqCompare != 0) {
                return freqCompare;
            }
            return compareWords(e1.getKey(), e2.getKey());
        });

        StringBuilder output = new StringBuilder();
        for (Map.Entry<String, Integer> entry : entries) {
            output.append(entry.getKey()).append(": ").append(entry.getValue()).append('\n');
        }

        // Print using UTF-8. 
        // System.out is typically UTF-8 on modern JVMs, but we enforce it explicitly.
        try {
            System.out.write(output.toString().getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new UncheckedIOException("Failed to write output", e);
        }
    }
}