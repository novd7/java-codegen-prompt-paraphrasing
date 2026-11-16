import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8));
        PrintWriter out = new PrintWriter(
                new OutputStreamWriter(System.out, StandardCharsets.UTF_8));

        String line = reader.readLine();
        if (line == null) {
            out.flush();
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

        List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());
        entries.sort((a, b) -> {
            int cmp = Integer.compare(b.getValue(), a.getValue());
            if (cmp != 0) return cmp;
            return compareWords(a.getKey(), b.getKey());
        });

        for (Map.Entry<String, Integer> e : entries) {
            out.print(e.getKey());
            out.print(": ");
            out.print(e.getValue());
            out.print('\n');
        }
        out.flush();
    }

    private static void addWord(Map<String, Integer> counts, String w) {
        counts.merge(w, 1, Integer::sum);
    }

    private static int compareWords(String a, String b) {
        int len = Math.min(a.length(), b.length());
        for (int i = 0; i < len; i++) {
            int cmp = compareChars(a.charAt(i), b.charAt(i));
            if (cmp != 0) return cmp;
        }
        return Integer.compare(a.length(), b.length());
    }

    private static int compareChars(char x, char y) {
        if (x == y) return 0;
        // ё идёт сразу после е
        int rx = ruOrder(x);
        int ry = ruOrder(y);
        boolean xRu = rx >= 0;
        boolean yRu = ry >= 0;
        if (xRu && yRu) {
            return Integer.compare(rx, ry);
        }
        // для латиницы a-z
        int lx = (x >= 'a' && x <= 'z') ? x - 'a' : -1;
        int ly = (y >= 'a' && y <= 'z') ? y - 'a' : -1;
        if (lx >= 0 && ly >= 0) {
            return Integer.compare(lx, ly);
        }
        return Character.compare(x, y);
    }

    // Возвращает индекс буквы в русском алфавите, где ё сразу после е.
    private static int ruOrder(char c) {
        if (c >= 'а' && c <= 'я') {
            int idx = c - 'а';
            // 'е' = 5 (а=0,б=1,в=2,г=3,д=4,е=5)
            // вставляем ё после е => буквы от ж до я сдвигаются на 1
            if (idx > 5) idx++;
            return idx;
        }
        if (c == 'ё') {
            return 6;
        }
        return -1;
    }
}