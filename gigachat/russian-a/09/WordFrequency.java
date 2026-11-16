import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WordFrequency {

    // Регулярное выражение для извлечения слов.
    // [A-Za-z] — английские буквы.
    // [\u0401\u0451\u0410-\u044F] — русские буквы, включая Ё (U+0401), ё (U+0451) и А-Я/а-я (U+0410-U+044F).
    private static final Pattern WORD_PATTERN = Pattern.compile("[A-Za-z]+|[\\u0401\\u0451\\u0410-\\u044F]+");

    public static void main(String[] args) throws IOException {
        // Читаем одну строку из System.in в UTF-8
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        String line = reader.readLine();

        if (line == null || line.isEmpty()) {
            // Если строка пустая или null, вывод оставляем пустым
            return;
        }

        Map<String, Integer> counts = new HashMap<>();
        Matcher matcher = WORD_PATTERN.matcher(line);

        // Извлекаем слова, приводим к нижнему регистру и считаем
        while (matcher.find()) {
            String word = matcher.group().toLowerCase(Locale.ROOT);
            counts.merge(word, 1, Integer::sum);
        }

        if (counts.isEmpty()) {
            // Если слов не найдено, вывод оставляем пустым
            return;
        }

        // Сортируем слова по правилам задачи
        List<Map.Entry<String, Integer>> sorted = counts.entrySet().stream()
                .sorted((e1, e2) -> {
                    int freqCompare = Integer.compare(e2.getValue(), e1.getValue()); // по убыванию частоты
                    if (freqCompare != 0) {
                        return freqCompare;
                    }
                    // При равной частоте — по алфавиту с учетом языка
                    return compareWords(e1.getKey(), e2.getKey());
                })
                .toList();

        // Выводим результат в UTF-8
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8));
        for (Map.Entry<String, Integer> entry : sorted) {
            writer.write(entry.getKey() + ": " + entry.getValue());
            writer.newLine();
        }
        writer.flush();
    }

    /**
     * Сравнивает два слова, которые гарантированно написаны в одном алфавите.
     * Для английских слов используется стандартный порядок.
     * Для русских слов 'ё' и 'Ё' считаются идущими сразу после 'е' и 'Е'.
     */
    private static int compareWords(String w1, String w2) {
        boolean isEnglish1 = isEnglish(w1);
        boolean isEnglish2 = isEnglish(w2);

        // По условию алфавиты не смешиваются внутри одной строки, 
        // но на всякий случай английские слова ставим перед русскими.
        if (isEnglish1 != isEnglish2) {
            return isEnglish1 ? -1 : 1;
        }

        if (isEnglish1) {
            return w1.compareTo(w2);
        } else {
            return compareRussian(w1, w2);
        }
    }

    private static boolean isEnglish(String word) {
        char c = word.charAt(0);
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    /**
     * Сравнение русских слов с учетом позиции буквы 'ё'.
     * 'е' -> 6, 'ё' -> 7, остальные буквы от 'а' до 'я' -> 0..32.
     */
    private static int compareRussian(String w1, String w2) {
        int len = Math.min(w1.length(), w2.length());
        for (int i = 0; i < len; i++) {
            char c1 = w1.charAt(i);
            char c2 = w2.charAt(i);
            if (c1 != c2) {
                return Integer.compare(russianCharOrder(c1), russianCharOrder(c2));
            }
        }
        return Integer.compare(w1.length(), w2.length());
    }

    private static int russianCharOrder(char c) {
        switch (c) {
            case 'е':
                return 6;
            case 'ё':
                return 7;
            default:
                // 'а' (U+0430) должна стать 0. 
                // Код 'а' - 1072. Код 'ё' - 1105, но мы её обработали выше.
                return c - '\u0430';
        }
    }
}