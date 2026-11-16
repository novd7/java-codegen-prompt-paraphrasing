import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(System.out, StandardCharsets.UTF_8));

        String line = reader.readLine();
        if (line == null) {
            writer.flush();
            return;
        }

        Map<String, Integer> counts = new HashMap<>();
        StringBuilder word = new StringBuilder();
        boolean isRussian = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (Character.isLetter(c)) {
                if (word.length() == 0) {
                    isRussian = isRussianLetter(c);
                }
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

        List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());

        // Коллатор для русского языка с учётом порядка «ё» после «е»
        Collator ruCollator = Collator.getInstance(new Locale("ru", "RU"));
        ruCollator.setStrength(Collator.IDENTICAL);
        // В Java Collator для русского по умолчанию ставит "ё" после "е".
        // Убедимся через Primary разницу, но используем TERTIARY для стабильности.

        Comparator<Map.Entry<String, Integer>> comparator = (a, b) -> {
            int cmp = Integer.compare(b.getValue(), a.getValue());
            if (cmp != 0) return cmp;
            String wa = a.getKey();
            String wb = b.getKey();
            // Определяем алфавит по первой букве (в строке только один алфавит)
            boolean ru = isRussianLetter(wa.charAt(0));
            if (ru) {
                return ruCollator.compare(wa, wb);
            } else {
                return wa.compareTo(wb);
            }
        };

        entries.sort(comparator);

        for (Map.Entry<String, Integer> e : entries) {
            writer.write(e.getKey());
            writer.write(": ");
            writer.write(Integer.toString(e.getValue()));
            writer.write('\n');
        }
        writer.flush();
    }

    private static void addWord(Map<String, Integer> counts, String w) {
        counts.merge(w, 1, Integer::sum);
    }

    private static boolean isRussianLetter(char c) {
        return (c >= 'А' && c <= 'я') || c == 'Ё' || c == 'ё';
    }
}