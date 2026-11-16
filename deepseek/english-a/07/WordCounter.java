import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.*;

public class WordCounter {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(System.in, StandardCharsets.UTF_8));
        PrintWriter writer = new PrintWriter(
            new OutputStreamWriter(System.out, StandardCharsets.UTF_8));

        String line = reader.readLine();
        if (line == null) {
            writer.flush();
            return;
        }

        // Determine which alphabet we're dealing with, and build the collator/map
        boolean hasRussian = line.codePoints().anyMatch(cp ->
            (cp >= 'А' && cp <= 'я') || cp == 'Ё' || cp == 'ё');
        boolean hasEnglish = line.codePoints().anyMatch(cp ->
            (cp >= 'A' && cp <= 'Z') || (cp >= 'a' && cp <= 'z'));

        Map<String, Integer> counts = new HashMap<>();
        StringBuilder currentWord = new StringBuilder();

        for (int i = 0; i < line.length(); ) {
            int cp = line.codePointAt(i);
            int charCount = Character.charCount(cp);

            if (isLetter(cp, hasRussian, hasEnglish)) {
                currentWord.appendCodePoint(Character.toLowerCase(cp));
            } else {
                if (currentWord.length() > 0) {
                    String w = currentWord.toString();
                    counts.merge(w, 1, Integer::sum);
                    currentWord.setLength(0);
                }
            }
            i += charCount;
        }
        if (currentWord.length() > 0) {
            counts.merge(currentWord.toString(), 1, Integer::sum);
        }

        // Comparator: by count descending, then alphabetically
        Comparator<String> alphaComparator = buildAlphaComparator(hasRussian);

        List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());
        entries.sort((a, b) -> {
            int cmp = Integer.compare(b.getValue(), a.getValue());
            if (cmp != 0) return cmp;
            return alphaComparator.compare(a.getKey(), b.getKey());
        });

        for (Map.Entry<String, Integer> e : entries) {
            writer.print(e.getKey());
            writer.print(": ");
            writer.print(e.getValue());
            writer.print('\n');
        }
        writer.flush();
    }

    private static boolean isLetter(int cp, boolean hasRussian, boolean hasEnglish) {
        if (hasRussian) {
            return (cp >= 'А' && cp <= 'я') || cp == 'Ё' || cp == 'ё';
        }
        if (hasEnglish) {
            return (cp >= 'A' && cp <= 'Z') || (cp >= 'a' && cp <= 'z');
        }
        return false;
    }

    private static Comparator<String> buildAlphaComparator(boolean russian) {
        if (!russian) {
            return Comparator.naturalOrder(); // a-z lowercase order
        }
        // Russian alphabet order with ё immediately after е
        String order = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя";
        Map<Character, Integer> rank = new HashMap<>();
        for (int i = 0; i < order.length(); i++) {
            rank.put(order.charAt(i), i);
        }
        return (s1, s2) -> {
            int len = Math.min(s1.length(), s2.length());
            for (int i = 0; i < len; i++) {
                char c1 = s1.charAt(i);
                char c2 = s2.charAt(i);
                int r1 = rank.getOrDefault(c1, Integer.MAX_VALUE);
                int r2 = rank.getOrDefault(c2, Integer.MAX_VALUE);
                if (r1 != r2) return Integer.compare(r1, r2);
            }
            return Integer.compare(s1.length(), s2.length());
        };
    }
}