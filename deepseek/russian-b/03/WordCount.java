import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WordCount {
    public static void main(String[] args) throws Exception {
        // Читаем одну строку в UTF-8
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8));
        String line = reader.readLine();
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        if (line == null) {
            return;
        }

        Map<String, Integer> counts = new HashMap<>();
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (Character.isLetter(c)) {
                current.append(Character.toLowerCase(c));
            } else {
                if (current.length() > 0) {
                    String w = current.toString();
                    counts.merge(w, 1, Integer::sum);
                    current.setLength(0);
                }
            }
        }
        if (current.length() > 0) {
            counts.merge(current.toString(), 1, Integer::sum);
        }

        // Компаратор: сначала по убыванию частоты, потом по алфавиту
        Comparator<Map.Entry<String, Integer>> cmp = (a, b) -> {
            int byCount = Integer.compare(b.getValue(), a.getValue());
            if (byCount != 0) return byCount;
            return compareAlphabetically(a.getKey(), b.getKey());
        };

        List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());
        entries.sort(cmp);

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> e : entries) {
            sb.append(e.getKey()).append(": ").append(e.getValue()).append('\n');
        }
        out.print(sb);
        out.flush();
    }

    /**
     * Сравнение строк в алфавитном порядке с учётом того, что «ё» идёт после «е».
     * Строки могут содержать только буквы одного алфавита (русского или английского).
     */
    private static int compareAlphabetically(String a, String b) {
        int n = Math.min(a.length(), b.length());
        for (int i = 0; i < n; i++) {
            int cmp = Integer.compare(rank(a.charAt(i)), rank(b.charAt(i)));
            if (cmp != 0) return cmp;
        }
        return Integer.compare(a.length(), b.length());
    }

    /**
     * Возвращает порядковый номер буквы в соответствующем алфавите.
     * Для русского алфавита «ё» идёт сразу после «е».
     */
    private static int rank(char c) {
        // Русские буквы (строчные)
        if (c >= 'а' && c <= 'я') {
            // а..е -> 0..5, ё -> 6, ж..я -> 7..32
            if (c == 'ё') return 6;
            if (c <= 'е') return c - 'а';
            // ж..я
            return c - 'а' + 1;
        }
        if (c == 'ё') return 6;
        // Английские буквы
        if (c >= 'a' && c <= 'z') {
            return c - 'a';
        }
        // На случай неожиданных символов — по коду
        return c;
    }
}