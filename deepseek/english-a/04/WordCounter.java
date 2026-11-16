import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WordCounter {

    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8));
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        String line = reader.readLine();
        if (line == null) {
            line = "";
        }

        // Custom comparator for Russian letters: 'е' < 'ё' < 'ж' < 'з' ...
        // In Unicode, 'ё' (U+0451) comes after 'я' (U+044F), so we need to remap it
        // to sit immediately after 'е' (U+0435).
        // Similarly uppercase 'Ё' (U+0401) vs 'Е' (U+0415) and 'Я' (U+042F).
        // Since we lowercase everything anyway, only lowercase matters.

        Map<String, Integer> counts = new LinkedHashMap<>();
        StringBuilder word = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (isWordChar(c)) {
                word.append(Character.toLowerCase(c));
            } else {
                flush(word, counts);
            }
        }
        flush(word, counts);

        List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());

        entries.sort((a, b) -> {
            int cmp = Integer.compare(b.getValue(), a.getValue());
            if (cmp != 0) {
                return cmp;
            }
            return compareWords(a.getKey(), b.getKey());
        });

        for (Map.Entry<String, Integer> e : entries) {
            out.print(e.getKey() + ": " + e.getValue() + "\n");
        }
    }

    private static boolean isWordChar(char c) {
        return Character.isLetter(c);
    }

    private static void flush(StringBuilder word, Map<String, Integer> counts) {
        if (word.length() > 0) {
            String w = word.toString();
            counts.merge(w, 1, Integer::sum);
            word.setLength(0);
        }
    }

    /**
     * Compares two lowercase words. Handles Russian specially so that 'ё'
     * is treated as coming immediately after 'е'. For English, standard
     * code-point order equals a-z order for lowercase letters.
     */
    private static int compareWords(String a, String b) {
        int len = Math.min(a.length(), b.length());
        for (int i = 0; i < len; i++) {
            int ca = sortKey(a.charAt(i));
            int cb = sortKey(b.charAt(i));
            if (ca != cb) {
                return Integer.compare(ca, cb);
            }
        }
        return Integer.compare(a.length(), b.length());
    }

    /**
     * Maps a character to a sort key. For Russian, remaps 'ё' (U+0451) to
     * sit right after 'е' (U+0435). Other characters keep their code point.
     */
    private static int sortKey(char c) {
        if (c == '\u0451') { // 'ё'
            // Place it between 'е' (0x0435) and 'ж' (0x0436).
            // Use a value that compares greater than 'е' but less than 'ж'.
            return 0x0435 * 2 + 1; // odd offset -> between 0x0435 and 0x0436 if doubled
        }
        // To make the remap consistent, double other code points.
        return c * 2;
    }
}