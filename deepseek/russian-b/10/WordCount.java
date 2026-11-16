import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WordCount {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8));
        String line = reader.readLine();
        if (line == null) line = "";

        Map<String, Integer> counts = new HashMap<>();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i <= line.length(); i++) {
            char c = i < line.length() ? line.charAt(i) : ' ';
            if (Character.isLetter(c)) {
                sb.append(Character.toLowerCase(c));
            } else {
                if (sb.length() > 0) {
                    String word = sb.toString();
                    counts.merge(word, 1, Integer::sum);
                    sb.setLength(0);
                }
            }
        }

        // Компаратор: русский алфавит с «ё» сразу после «е», английский — a–z
        Comparator<String> alphabetOrder = WordCount::compareWords;

        List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());
        entries.sort((a, b) -> {
            int cmp = Integer.compare(b.getValue(), a.getValue());
            if (cmp != 0) return cmp;
            return alphabetOrder.compare(a.getKey(), b.getKey());
        });

        StringBuilder out = new StringBuilder();
        for (Map.Entry<String, Integer> e : entries) {
            out.append(e.getKey()).append(": ").append(e.getValue()).append('\n');
        }

        PrintStream ps = new PrintStream(
                new FileOutputStream(FileDescriptor.out), false, StandardCharsets.UTF_8);
        ps.print(out);
        ps.flush();
    }

    private static final String RU_ORDER = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя";

    private static int compareWords(String a, String b) {
        int len = Math.min(a.length(), b.length());
        for (int i = 0; i < len; i++) {
            char ca = a.charAt(i);
            char cb = b.charAt(i);
            if (ca != cb) {
                return Integer.compare(rank(ca), rank(cb));
            }
        }
        return Integer.compare(a.length(), b.length());
    }

    private static int rank(char c) {
        if (c >= 'a' && c <= 'z') {
            return c - 'a';
        }
        int idx = RU_ORDER.indexOf(c);
        if (idx >= 0) {
            // Ставим русские буквы после английских (алфавиты не смешиваются во входе)
            return 1000 + idx;
        }
        return 10000 + c;
    }
}