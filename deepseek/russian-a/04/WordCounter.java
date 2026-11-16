import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.Collator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WordCounter {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8));
        PrintWriter out = new PrintWriter(
                new java.io.OutputStreamWriter(System.out, StandardCharsets.UTF_8));

        String line = reader.readLine();
        if (line == null) {
            out.flush();
            return;
        }

        Map<String, Integer> counts = new HashMap<>();
        StringBuilder word = new StringBuilder();
        boolean isRussian = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (Character.isLetter(c)) {
                char lower = Character.toLowerCase(c);
                if (lower >= 'а' && lower <= 'я' || lower == 'ё') {
                    isRussian = true;
                }
                word.append(lower);
            } else {
                if (word.length() > 0) {
                    counts.merge(word.toString(), 1, Integer::sum);
                    word.setLength(0);
                }
            }
        }
        if (word.length() > 0) {
            counts.merge(word.toString(), 1, Integer::sum);
        }

        List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());

        Collator collator = Collator.getInstance(
                isRussian ? new Locale("ru", "RU") : Locale.ENGLISH);
        collator.setStrength(Collator.IDENTICAL);
        collator.setDecomposition(Collator.CANONICAL_DECOMPOSITION);
        if (isRussian) {
            collator.setStrength(Collator.TERTIARY);
        }

        entries.sort((a, b) -> {
            int cmp = Integer.compare(b.getValue(), a.getValue());
            if (cmp != 0) return cmp;
            return collator.compare(a.getKey(), b.getKey());
        });

        for (Map.Entry<String, Integer> e : entries) {
            out.print(e.getKey());
            out.print(": ");
            out.print(e.getValue());
            out.print('\n');
        }
        out.flush();
    }
}