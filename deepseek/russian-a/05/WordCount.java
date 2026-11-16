import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WordCount {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8));
        PrintWriter writer = new PrintWriter(
                System.out, true, StandardCharsets.UTF_8);

        String line = reader.readLine();
        if (line == null || line.isEmpty()) {
            return;
        }

        Map<String, Integer> counts = new HashMap<>();
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (Character.isLetter(c)) {
                current.append(c);
            } else {
                if (current.length() > 0) {
                    addWord(counts, current.toString());
                    current.setLength(0);
                }
            }
        }
        if (current.length() > 0) {
            addWord(counts, current.toString());
        }

        if (counts.isEmpty()) {
            return;
        }

        // Определяем алфавит по первой букве первого слова
        String sample = counts.keySet().iterator().next();
        Locale locale;
        if (sample.codePointAt(0) < 128) {
            locale = Locale.ENGLISH;
        } else {
            locale = Locale.forLanguageTag("ru");
        }

        Collator collator = Collator.getInstance(locale);
        collator.setStrength(Collator.IDENTICAL);

        List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());
        entries.sort(new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b) {
                int cmp = Integer.compare(b.getValue(), a.getValue());
                if (cmp != 0) {
                    return cmp;
                }
                return collator.compare(a.getKey(), b.getKey());
            }
        });

        for (Map.Entry<String, Integer> e : entries) {
            writer.print(e.getKey());
            writer.print(": ");
            writer.print(e.getValue());
            writer.print('\n');
        }
    }

    private static void addWord(Map<String, Integer> counts, String word) {
        String lower = word.toLowerCase(Locale.ROOT);
        counts.merge(lower, 1, Integer::sum);
    }
}