import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8));
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(System.out, StandardCharsets.UTF_8));

        String line = reader.readLine();
        if (line == null) {
            line = "";
        }

        Map<String, Integer> counts = new HashMap<>();
        StringBuilder word = new StringBuilder();
        boolean isRussian = false;
        boolean alphabetDetected = false;

        int len = line.length();
        for (int i = 0; i < len; i++) {
            char c = line.charAt(i);
            if (isLetter(c)) {
                if (!alphabetDetected) {
                    isRussian = isRussianLetter(c);
                    alphabetDetected = true;
                }
                word.append(Character.toLowerCase(c));
            } else {
                flushWord(word, counts);
            }
        }
        flushWord(word, counts);

        List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());

        Comparator<Map.Entry<String, Integer>> byCount =
                Comparator.comparingInt(Map.Entry::getValue);

        Comparator<Map.Entry<String, Integer>> comparator;
        if (isRussian) {
            comparator = byCount
                    .thenComparing(Map.Entry::getKey, Main::compareRussian);
        } else {
            comparator = byCount
                    .thenComparing(Map.Entry::getKey);
        }

        entries.sort(comparator.reversed());

        for (Map.Entry<String, Integer> e : entries) {
            writer.write(e.getKey());
            writer.write(": ");
            writer.write(Integer.toString(e.getValue()));
            writer.write('\n');
        }
        writer.flush();
    }

    private static void flushWord(StringBuilder word, Map<String, Integer> counts) {
        if (word.length() > 0) {
            String w = word.toString();
            counts.merge(w, 1, Integer::sum);
            word.setLength(0);
        }
    }

    private static boolean isLetter(char c) {
        return (c >= 'A' && c <= 'Z')
                || (c >= 'a' && c <= 'z')
                || (c >= 'А' && c <= 'Я')
                || (c >= 'а' && c <= 'я')
                || c == 'Ё' || c == 'ё';
    }

    private static boolean isRussianLetter(char c) {
        return (c >= 'А' && c <= 'Я')
                || (c >= 'а' && c <= 'я')
                || c == 'Ё' || c == 'ё';
    }

    private static int compareRussian(String a, String b) {
        int n = Math.min(a.length(), b.length());
        for (int i = 0; i < n; i++) {
            int ka = russianKey(a.charAt(i));
            int kb = russianKey(b.charAt(i));
            if (ka != kb) {
                return Integer.compare(ka, kb);
            }
        }
        return Integer.compare(a.length(), b.length());
    }

    private static int russianKey(char c) {
        // 'ё' should come right after 'е'
        if (c == 'ё') {
            return ('е' - 'а') + 1;
        }
        if (c >= 'а' && c <= 'я') {
            int k = c - 'а';
            if (c > 'е') {
                k += 1; // reserve a slot for 'ё'
            }
            return k;
        }
        return c;
    }
}