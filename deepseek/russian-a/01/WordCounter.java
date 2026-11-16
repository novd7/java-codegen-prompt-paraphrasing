import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordCounter {

    private static final Pattern WORD_PATTERN = Pattern.compile("\\p{L}+");

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8));
        PrintStream out = new PrintStream(System.out, false, StandardCharsets.UTF_8);

        String line = reader.readLine();
        if (line == null) {
            out.flush();
            return;
        }

        // Collator for Russian with 'ё' right after 'е'
        Collator collator = Collator.getInstance(Locale.of("ru", "RU"));
        collator.setStrength(Collator.PRIMARY);

        Map<String, Integer> counts = new TreeMap<>(collator);

        Matcher matcher = WORD_PATTERN.matcher(line);
        while (matcher.find()) {
            String word = matcher.group().toLowerCase(Locale.ROOT);
            counts.merge(word, 1, Integer::sum);
        }

        List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());
        entries.sort((a, b) -> {
            int cmp = Integer.compare(b.getValue(), a.getValue());
            if (cmp != 0) return cmp;
            return collator.compare(a.getKey(), b.getKey());
        });

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> e : entries) {
            sb.append(e.getKey()).append(": ").append(e.getValue()).append('\n');
        }
        out.print(sb);
        out.flush();
    }
}