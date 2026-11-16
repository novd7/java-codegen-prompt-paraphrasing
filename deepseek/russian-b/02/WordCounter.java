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

public class WordCounter {

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
        int n = line.length();
        int i = 0;
        while (i < n) {
            if (Character.isLetter(line.charAt(i))) {
                int start = i;
                while (i < n && Character.isLetter(line.charAt(i))) {
                    i++;
                }
                String word = line.substring(start, i).toLowerCase(Locale.ROOT);
                counts.merge(word, 1, Integer::sum);
            } else {
                i++;
            }
        }

        List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());
        Collator collator = Collator.getInstance(Locale.ROOT);
        collator.setStrength(Collator.IDENTICAL);
        entries.sort((a, b) -> {
            int cmp = Integer.compare(b.getValue(), a.getValue());
            if (cmp != 0) {
                return cmp;
            }
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
}