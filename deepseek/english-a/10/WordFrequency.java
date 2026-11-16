import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.Collator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WordFrequency {
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
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (isLetter(c)) {
                current.append(c);
            } else {
                flushWord(current, counts);
            }
        }
        flushWord(current, counts);

        List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());

        boolean hasCyrillic = entries.stream()
                .anyMatch(e -> containsCyrillic(e.getKey()));

        Collator collator = hasCyrillic
                ? Collator.getInstance(new Locale("ru", "RU"))
                : Collator.getInstance(Locale.ROOT);

        entries.sort((a, b) -> {
            int cmp = Integer.compare(b.getValue(), a.getValue());
            if (cmp != 0) return cmp;
            return collator.compare(a.getKey(), b.getKey());
        });

        for (Map.Entry<String, Integer> e : entries) {
            writer.write(e.getKey());
            writer.write(": ");
            writer.write(Integer.toString(e.getValue()));
            writer.write('\n');
        }
        writer.flush();
    }

    private static void flushWord(StringBuilder sb, Map<String, Integer> counts) {
        if (sb.length() == 0) return;
        String word = sb.toString().toLowerCase(Locale.ROOT);
        counts.merge(word, 1, Integer::sum);
        sb.setLength(0);
    }

    private static boolean isLetter(char c) {
        return (c >= 'A' && c <= 'Z')
                || (c >= 'a' && c <= 'z')
                || (c >= 'А' && c <= 'Я')
                || (c >= 'а' && c <= 'я')
                || c == 'Ё' || c == 'ё';
    }

    private static boolean containsCyrillic(String s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if ((c >= 'А' && c <= 'Я') || (c >= 'а' && c <= 'я')
                    || c == 'Ё' || c == 'ё') {
                return true;
            }
        }
        return false;
    }
}