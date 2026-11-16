import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WordFrequency {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8));
        PrintWriter writer = new PrintWriter(System.out, false);
        writer.flush();

        String line = reader.readLine();
        if (line == null) {
            line = "";
        }

        Map<String, Integer> counts = new LinkedHashMap<>();
        StringBuilder currentWord = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (Character.isLetter(c)) {
                currentWord.append(Character.toLowerCase(c));
            } else {
                if (currentWord.length() > 0) {
                    counts.merge(currentWord.toString(), 1, Integer::sum);
                    currentWord.setLength(0);
                }
            }
        }
        if (currentWord.length() > 0) {
            counts.merge(currentWord.toString(), 1, Integer::sum);
        }

        if (counts.isEmpty()) {
            writer.flush();
            return;
        }

        List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());
        Collections.sort(entries, (a, b) -> {
            int cmp = Integer.compare(b.getValue(), a.getValue());
            if (cmp != 0) return cmp;
            return compareAlphabetically(a.getKey(), b.getKey());
        });

        for (Map.Entry<String, Integer> e : entries) {
            writer.print(e.getKey());
            writer.print(": ");
            writer.print(e.getValue());
            writer.print('\n');
        }
        writer.flush();
    }

    private static int compareAlphabetically(String a, String b) {
        int len = Math.min(a.length(), b.length());
        for (int i = 0; i < len; i++) {
            char ca = a.charAt(i);
            char cb = b.charAt(i);
            int ra = rank(ca);
            int rb = rank(cb);
            if (ra != rb) {
                return Integer.compare(ra, rb);
            }
        }
        return Integer.compare(a.length(), b.length());
    }

    // Custom alphabetical rank: Latin a-z (0-25), then Russian а-я (26+),
    // with ё placed immediately after е.
    private static int rank(char c) {
        if (c >= 'a' && c <= 'z') {
            return c - 'a';
        }
        if (c >= 'а' && c <= 'я') {
            // Russian 'а' = 0x430, 'ё' = 0x451, 'е' = 0x435
            if (c == 'ё') {
                return 100 + ('е' - 'а') + 1; // right after е
            }
            int idx = c - 'а';
            // For letters after 'е', add 1 to make room for 'ё'
            if (c > 'е') {
                idx += 1;
            }
            return 100 + idx;
        }
        return 1000 + c; // fallback for unexpected characters
    }
}