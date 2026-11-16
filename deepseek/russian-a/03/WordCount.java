import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WordCount {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8));
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        String line = reader.readLine();
        if (line == null) {
            line = "";
        }

        Map<String, Integer> counts = new LinkedHashMap<>();
        StringBuilder word = new StringBuilder();
        boolean isRussian = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (Character.isLetter(c)) {
                if (word.isEmpty()) {
                    isRussian = isRussianLetter(c);
                }
                word.append(Character.toLowerCase(c));
            } else {
                flush(word, counts);
            }
        }
        flush(word, counts);

        if (counts.isEmpty()) {
            return;
        }

        Collator collator = Collator.getInstance(
                isRussian ? new Locale("ru", "RU") : Locale.ENGLISH);
        collator.setStrength(Collator.PRIMARY);
        collator.setDecomposition(Collator.CANONICAL_DECOMPOSITION);

        List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());
        entries.sort(Comparator
                .comparingInt((Map.Entry<String, Integer> e) -> e.getValue()).reversed()
                .thenComparing(Map.Entry::getKey, collator));

        for (Map.Entry<String, Integer> e : entries) {
            out.print(e.getKey());
            out.print(": ");
            out.print(e.getValue());
            out.print('\n');
        }
        out.flush();
    }

    private static void flush(StringBuilder word, Map<String, Integer> counts) {
        if (word.length() > 0) {
            counts.merge(word.toString(), 1, Integer::sum);
            word.setLength(0);
        }
    }

    private static boolean isRussianLetter(char c) {
        return (c >= 'А' && c <= 'я') || c == 'Ё' || c == 'ё';
    }
}