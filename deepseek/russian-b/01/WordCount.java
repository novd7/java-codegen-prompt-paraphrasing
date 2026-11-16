import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WordCount {

    public static void main(String[] args) throws Exception {
        // Читаем одну строку из stdin в UTF-8
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8));
        String line = reader.readLine();
        if (line == null) {
            line = "";
        }

        // Map сохраняет порядок первого появления; ключ — слово в нижнем регистре
        Map<String, Integer> counts = new LinkedHashMap<>();

        StringBuilder word = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (isLetter(c)) {
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

        // Сортировка: по убыванию частоты, затем по алфавиту
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());
        entries.sort(Comparator
                .comparingInt((Map.Entry<String, Integer> e) -> e.getValue()).reversed()
                .thenComparing(Map.Entry::getKey, WordCount::compareAlphabet));

        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
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

    private static boolean isLetter(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')
                || (c >= 'А' && c <= 'Я') || (c >= 'а' && c <= 'я')
                || c == 'Ё' || c == 'ё';
    }

    // Алфавитное сравнение с учётом того, что 'ё' идёт после 'е' (и после 'Е' в верхнем регистре,
    // но здесь слова уже в нижнем регистре)
    private static int compareAlphabet(String a, String b) {
        int len = Math.min(a.length(), b.length());
        for (int i = 0; i < len; i++) {
            int r = compareChar(a.charAt(i), b.charAt(i));
            if (r != 0) {
                return r;
            }
        }
        return Integer.compare(a.length(), b.length());
    }

    private static int compareChar(char x, char y) {
        return Integer.compare(order(x), order(y));
    }

    // Функция порядка: для русских букв 'ё' получает код 'е'+1, сдвигая всё, что после 'е'
    private static int order(char c) {
        // Русский алфавит: а-я (0x430-0x44F), ё (0x451)
        if (c == 'ё') {
            return 'е' + 1;
        }
        if (c >= 'а' && c <= 'я') {
            int code = c;
            if (c > 'е') {
                code += 1; // освобождаем место для 'ё'
            }
            return code;
        }
        return c;
    }
}