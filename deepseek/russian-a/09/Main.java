import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.Collator;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Main {
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

        Map<String, Integer> counts = new LinkedHashMap<>();
        StringBuilder word = new StringBuilder();
        boolean hasCyrillic = false;
        boolean hasLatin = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (isLetter(c)) {
                if (c >= 'А' && c <= 'я' || c == 'Ё' || c == 'ё') hasCyrillic = true;
                if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) hasLatin = true;
                word.append(Character.toLowerCase(c));
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

        Locale locale = hasCyrillic ? new Locale("ru") : Locale.ROOT;
        Collator collator = Collator.getInstance(locale);
        collator.setStrength(Collator.TERTIARY);

        List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());
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

    private static boolean isLetter(char c) {
        if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) return true;
        if (c >= 'А' && c <= 'я') return true;
        if (c == 'Ё' || c == 'ё') return true;
        return false;
    }
}