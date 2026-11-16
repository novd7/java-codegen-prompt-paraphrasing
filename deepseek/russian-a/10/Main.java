import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8));
        PrintWriter out = new PrintWriter(System.out, false, StandardCharsets.UTF_8);

        String line = reader.readLine();
        if (line == null) line = "";

        Map<String, Integer> counts = new HashMap<>();
        StringBuilder word = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (Character.isLetter(c)) {
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

        List<Map.Entry<String, Integer>> list = new ArrayList<>(counts.entrySet());
        list.sort((a, b) -> {
            int cmp = Integer.compare(b.getValue(), a.getValue());
            if (cmp != 0) return cmp;
            return compareWords(a.getKey(), b.getKey());
        });

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> e : list) {
            sb.append(e.getKey()).append(": ").append(e.getValue()).append('\n');
        }
        out.print(sb);
        out.flush();
    }

    private static int compareWords(String a, String b) {
        int n = Math.min(a.length(), b.length());
        for (int i = 0; i < n; i++) {
            char ca = a.charAt(i), cb = b.charAt(i);
            if (ca != cb) return Integer.compare(rank(ca), rank(cb));
        }
        return Integer.compare(a.length(), b.length());
    }

    // Определяет порядок символа с учётом правил: ё после е, а для английских — a-z.
    private static int rank(char c) {
        switch (c) {
            case 'ё': return 1077 * 2 + 1; // после 'е' (1077)
            case 'е': return 1077 * 2;
        }
        return c * 2;
    }
}