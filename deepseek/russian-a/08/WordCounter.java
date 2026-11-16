import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WordCounter {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8));
        String line = reader.readLine();
        PrintWriter out = new PrintWriter(
                new OutputStreamWriter(System.out, StandardCharsets.UTF_8));

        if (line == null) {
            out.flush();
            return;
        }

        Map<String, Integer> counts = new HashMap<>();
        StringBuilder word = new StringBuilder();

        for (int i = 0; i < line.length(); ) {
            int cp = line.codePointAt(i);
            i += Character.charCount(cp);
            if (Character.isLetter(cp)) {
                word.appendCodePoint(Character.toLowerCase(cp));
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

        // Компаратор: по убыванию частоты, затем по алфавиту с учётом ё после е
        Comparator<Map.Entry<String, Integer>> cmp =
                Comparator.<Map.Entry<String, Integer>>comparingInt(Map.Entry::getValue).reversed()
                        .thenComparing(Map.Entry::getKey, WordCounter::alphabeticalCompare);

        counts.entrySet().stream()
                .sorted(cmp)
                .forEach(e -> out.print(e.getKey() + ": " + e.getValue() + "\n"));

        out.flush();
    }

    // Сравнение строк по алфавиту, где 'ё' идёт сразу после 'е'
    private static int alphabeticalCompare(String a, String b) {
        int n = Math.min(a.length(), b.length());
        for (int i = 0; i < n; i++) {
            char ca = a.charAt(i);
            char cb = b.charAt(i);
            if (ca == cb) continue;
            int ra = rank(ca);
            int rb = rank(cb);
            if (ra != rb) return Integer.compare(ra, rb);
            return Character.compare(ca, cb);
        }
        return Integer.compare(a.length(), b.length());
    }

    // «Ранг» символа в алфавите: ё сдвигаем в позицию сразу после е
    private static int rank(char c) {
        if (c == 'ё') return 'е' + 1;      // между 'е' и 'ж'
        if (c > 'е' && c <= 'я') return c + 1; // сдвигаем остальные русские буквы
        return c;
    }
}