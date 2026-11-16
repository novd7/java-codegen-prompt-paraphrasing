import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Консольная программа на Java 21 для подсчета частоты слов.
 * Читает одну строку из stdin в UTF-8 и выводит статистику в stdout в UTF-8.
 * Слово — непрерывная последовательность букв одного алфавита (русского или английского).
 * Слова сортируются по убыванию частоты, затем по алфавиту (с учетом специфики 'ё').
 */
public class Main {

    // Регулярное выражение для поиска слов: только латиница или только кириллица (включая Ё/ё)
    private static final Pattern WORD_PATTERN = Pattern.compile("[A-Za-z]+|[А-Яа-яЁё]+");

    // Сортировка с учетом специфики русского алфавита (ё после е)
    private static final Comparator<String> ALPHABETICAL_ORDER = (s1, s2) -> {
        boolean isRus1 = isRussian(s1);
        boolean isRus2 = isRussian(s2);

        // Гарантируем, что сравнение идет только внутри одного алфавита
        if (isRus1 != isRus2) {
            throw new IllegalArgumentException("Слова разных алфавитов не должны сравниваться: " + s1 + " vs " + s2);
        }

        if (isRus1) {
            // Для русского используем специфический порядок букв
            return compareRussian(s1, s2);
        } else {
            // Для английского стандартный лексикографический порядок
            return s1.compareTo(s2);
        }
    };

    private static boolean isRussian(String s) {
        // Проверяем по первой букве, так как по условию слово состоит из букв одного алфавита
        char c = s.charAt(0);
        return (c >= 'А' && c <= 'Я') || (c >= 'а' && c <= 'я') || c == 'Ё' || c == 'ё';
    }

    private static int compareRussian(String s1, String s2) {
        int len = Math.min(s1.length(), s2.length());
        for (int i = 0; i < len; i++) {
            char c1 = normalizeRussian(s1.charAt(i));
            char c2 = normalizeRussian(s2.charAt(i));
            if (c1 != c2) {
                return c1 - c2;
            }
        }
        return s1.length() - s2.length();
    }

    /**
     * Приводит русскую букву к базовому виду для сравнения.
     * 'Ё' и 'ё' превращаются в 'е' и 'Е' соответственно.
     * Это позволяет отсортировать 'е' и 'ё' рядом, а затем финальный порядок
     * слов с одинаковым написанием (кроме ё/е) определит compareTo.
     */
    private static char normalizeRussian(char c) {
        if (c == 'Ё') return 'Е';
        if (c == 'ё') return 'е';
        return c;
    }

    public static void main(String[] args) throws Exception {
        // Читаем одну строку из stdin в UTF-8
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            String line = reader.readLine();
            if (line == null) {
                return;
            }

            Map<String, Integer> counts = new HashMap<>();
            Matcher matcher = WORD_PATTERN.matcher(line);

            // Находим слова и считаем их вхождения без учета регистра
            while (matcher.find()) {
                String word = matcher.group().toLowerCase(StandardCharsets.UTF_8);
                counts.merge(word, 1, Integer::sum);
            }

            if (counts.isEmpty()) {
                // Если слов нет — выводим пустую строку (только символ \n)
                try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8))) {
                    writer.println();
                }
                return;
            }

            // Переносим данные в список для сортировки
            List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());

            // Сортируем: 1. По убыванию количества, 2. По алфавиту
            entries.sort((e1, e2) -> {
                int freqCompare = Integer.compare(e2.getValue(), e1.getValue());
                if (freqCompare != 0) {
                    return freqCompare;
                }
                return ALPHABETICAL_ORDER.compare(e1.getKey(), e2.getKey());
            });

            // Выводим результат в stdout в UTF-8
            try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8))) {
                for (Map.Entry<String, Integer> entry : entries) {
                    writer.println(entry.getKey() + ": " + entry.getValue());
                }
            }
        }
    }
}