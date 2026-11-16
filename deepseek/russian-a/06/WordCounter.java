import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.Collator;
import java.util.*;

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

        Map<String, Integer> counts = new HashMap<>();
        StringBuilder word = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (Character.isLetter(c)) {
                word.append(Character.toLowerCase(c));
            } else {
                if (word.length() > 0) {
                    addWord(counts, word.toString());
                    word.setLength(0);
                }
            }
        }
        if (word.length() > 0) {
            addWord(counts, word.toString());
        }

        if (counts.isEmpty()) {
            writer.flush();
            return;
        }

        // Определяем алфавит по первой букве любого слова
        boolean russian = isRussian(counts.keySet().iterator().next());

        Collator collator = russian
                ? Collator.getInstance(new Locale("ru", "RU"))
                : Collator.getInstance(Locale.ROOT);
        collator.setStrength(Collator.PRIMARY);
        collator.setDecomposition(Collator.CANONICAL_DECOMPOSITION);

        List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());
        entries.sort((a, b) -> {
            int cmp = Integer.compare(b.getValue(), a.getValue());
            if (cmp != 0) return cmp;
            return collator.compare(a.getKey(), b.getKey());
        });

        for (Map.Entry<String, Integer> e : entries) {
            writer.print(e.getKey());
            writer.print(": ");
            writer.print(e.getValue());
            writer.print('\n');
        }
        writer.flush();
    }

    private static void addWord(Map<String, Integer> counts, String w) {
        counts.merge(w, 1, Integer::sum);
    }

    private static boolean isRussian(String word) {
        if (word.isEmpty()) return false;
        char c = word.charAt(0);
        return c >= 'а' && c <= 'я' || c == 'ё';
    }
}