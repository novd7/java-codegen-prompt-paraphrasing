import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WordCounter {

    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8));
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        String line = reader.readLine();
        if (line == null) {
            return;
        }

        // Подсчёт слов без учёта регистра
        Map<String, Integer> counts = new HashMap<>();
        StringBuilder word = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (Character.isLetter(c)) {
                word.append(Character.toLowerCase(c));
            } else {
                if (word.length() > 0) {
                    String w = word.toString();
                    counts.merge(w, 1, Integer::sum);
                    word.setLength(0);
                }
            }
        }
        if (word.length() > 0) {
            String w = word.toString();
            counts.merge(w, 1, Integer::sum);
        }

        // Компаратор: сначала по частоте (по убыванию), затем по алфавиту
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());
        entries.sort((a, b) -> {
            int cmp = Integer.compare(b.getValue(), a.getValue());
            if (cmp != 0) return cmp;
            return compareAlphabetically(a.getKey(), b.getKey());
        });

        for (Map.Entry<String, Integer> e : entries) {
            out.print(e.getKey());
            out.print(": ");
            out.print(e.getValue());
            out.print('\n');
        }
    }

    /**
     * Сравнение строк с учётом правил алфавитного порядка.
     * Для русского: «ё» идёт сразу после «е».
     * Для английского: обычный a–z.
     */
    private static int compareAlphabetically(String a, String b) {
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

    /**
     * Возвращает «ранг» символа для сортировки.
     * Для строчных русских букв «ё» ставится сразу после «е».
     */
    private static int rank(char c) {
        if (c == 'ё') {
            // 'е' = 0x0435, 'ж' = 0x0436 — ставим 'ё' между ними
            return 0x0435 * 2 + 1;
        }
        if (c >= 'а' && c <= 'я') {
            // обычные строчные русские буквы
            return c * 2;
        }
        // английские буквы и прочее — обычный код
        return c * 2;
    }
}