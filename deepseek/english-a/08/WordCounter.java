import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordCounter {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8));
        PrintWriter out = new PrintWriter(System.out, false, StandardCharsets.UTF_8);

        String line = reader.readLine();
        if (line != null) {
            Map<String, Integer> counts = new HashMap<>();
            StringBuilder word = new StringBuilder();

            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);
                if (Character.isLetter(c)) {
                    word.append(c);
                } else {
                    if (word.length() > 0) {
                        addWord(counts, word);
                        word.setLength(0);
                    }
                }
            }
            if (word.length() > 0) {
                addWord(counts, word);
            }

            List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());
            entries.sort(new Comparator<>() {
                @Override
                public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b) {
                    int cmp = Integer.compare(b.getValue(), a.getValue());
                    if (cmp != 0) return cmp;
                    return compareWords(a.getKey(), b.getKey());
                }
            });

            for (Map.Entry<String, Integer> e : entries) {
                out.print(e.getKey());
                out.print(": ");
                out.print(e.getValue());
                out.print('\n');
            }
        }
        out.flush();
    }

    private static void addWord(Map<String, Integer> counts, StringBuilder word) {
        String w = word.toString().toLowerCase();
        counts.merge(w, 1, Integer::sum);
    }

    /**
     * Compare two words character-by-character according to the rules:
     * - Russian alphabet: order А-Я with «ё» immediately after «е»
     * - English alphabet: a-z
     */
    private static int compareWords(String a, String b) {
        int len = Math.min(a.length(), b.length());
        for (int i = 0; i < len; i++) {
            int cmp = compareChars(a.charAt(i), b.charAt(i));
            if (cmp != 0) return cmp;
        }
        return Integer.compare(a.length(), b.length());
    }

    private static int compareChars(char x, char y) {
        if (x == y) return 0;
        int rx = rank(x);
        int ry = rank(y);
        return Integer.compare(rx, ry);
    }

    /**
     * Returns a rank for sorting. For English a-z, ranks are 1..26.
     * For Russian а-я with ё after е, ranks are 100+.
     */
    private static int rank(char c) {
        // English
        if (c >= 'a' && c <= 'z') {
            return 1 + (c - 'a');
        }
        // Russian
        if (c >= 'а' && c <= 'я') {
            // а..е = 0..5, ж..я = 7..32, ё = 6 (immediately after е)
            int idx = c - 'а';
            if (idx <= 5) { // а б в г д е
                return 100 + idx;
            } else { // ж б.. (idx 6 is ж, should be 7; ё goes to 6)
                return 100 + idx + 1;
            }
        }
        if (c == 'ё') {
            return 100 + 6;
        }
        // Fallback (shouldn't happen given problem constraints)
        return 1000 + c;
    }
}