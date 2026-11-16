import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8));
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        String line = reader.readLine();
        if (line == null) {
            return;
        }

        Map<String, Integer> counts = new HashMap<>();
        StringBuilder word = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (Character.isLetter(c)) {
                word.append(Character.toLowerCase(c));
            } else {
                flush(word, counts);
            }
        }
        flush(word, counts);

        List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());
        entries.sort((a, b) -> {
            int cmp = Integer.compare(b.getValue(), a.getValue());
            if (cmp != 0) return cmp;
            return compareAlphabet(a.getKey(), b.getKey());
        });

        for (Map.Entry<String, Integer> e : entries) {
            out.print(e.getKey() + ": " + e.getValue() + "\n");
        }
    }

    private static void flush(StringBuilder word, Map<String, Integer> counts) {
        if (word.length() > 0) {
            counts.merge(word.toString(), 1, Integer::sum);
            word.setLength(0);
        }
    }

    /**
     * Сравнение строк по алфавиту с учётом правил:
     * - русский: ё идёт сразу после е (а не в конце, как в обычном Unicode);
     * - английский: a–z.
     * Остальные символы сравниваем по code point.
     */
    private static int compareAlphabet(String a, String b) {
        int i = 0, j = 0;
        while (i < a.length() && j < b.length()) {
            int ca = a.codePointAt(i);
            int cb = b.codePointAt(j);
            int ka = sortKey(ca);
            int kb = sortKey(cb);
            if (ka != kb) {
                return Integer.compare(ka, kb);
            }
            i += Character.charCount(ca);
            j += Character.charCount(cb);
        }
        return Integer.compare(a.length(), b.length());
    }

    private static int sortKey(int cp) {
        // Русские строчные: 'а'(0x430)..'я'(0x44F), 'ё'(0x451)
        // Ставим ё (0x451) сразу после е (0x435), т.е. между 'е' и 'ж'.
        if (cp == 0x451) {
            return 0x435 + 1; // сразу после 'е'
        }
        if (cp > 0x435 && cp <= 0x44F) {
            // сдвигаем 'ж'..'я' на +1, чтобы освободить место для 'ё'
            return cp + 1;
        }
        return cp;
    }
}