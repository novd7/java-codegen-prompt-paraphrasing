import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) throws Exception {
        // Читаем одну строку из stdin в UTF-8
        String line;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            line = reader.readLine();
        }

        if (line == null || line.isEmpty()) {
            return;
        }

        // Регулярное выражение для извлечения слов (английские + русские, включая ё/Ё)
        Pattern wordPattern = Pattern.compile("[A-Za-zА-Яа-яЁё]+");
        Matcher matcher = wordPattern.matcher(line);

        Map<String, Integer> counts = new HashMap<>();

        // Находим все слова, приводим к нижнему регистру и считаем
        while (matcher.find()) {
            String word = matcher.group().toLowerCase(Locale.ROOT);
            counts.merge(word, 1, Integer::sum);
        }

        if (counts.isEmpty()) {
            return;
        }

        // Переносим слова в список для сортировки
        List<String> words = new ArrayList<>(counts.keySet());

        // Сортируем по правилам задачи
        words.sort((a, b) -> {
            int freqCompare = counts.get(b).compareTo(counts.get(a));
            if (freqCompare != 0) {
                return freqCompare;
            }

            // Частота равна, сравниваем по алфавиту
            boolean aIsRussian = isRussian(a.charAt(0));
            boolean bIsRussian = isRussian(b.charAt(0));

            // По условию алфавиты не смешиваются во входных данных, 
            // но на всякий случай русские слова ставим перед английскими
            if (aIsRussian != bIsRussian) {
                return aIsRussian ? -1 : 1;
            }

            // Сравнение внутри одного алфавита
            int len = Math.min(a.length(), b.length());
            for (int i = 0; i < len; i++) {
                char ca = a.charAt(i);
                char cb = b.charAt(i);
                if (ca != cb) {
                    // Специальная обработка для русского «е» и «ё»
                    if (isRussian(ca) && isRussian(cb)) {
                        if (ca == 'ё' && cb == 'е') return 1;
                        if (ca == 'е' && cb == 'ё') return -1;
                    }
                    return Character.compare(ca, cb);
                }
            }
            return Integer.compare(a.length(), b.length());
        });

        // Выводим результат
        StringBuilder output = new StringBuilder();
        String separator = System.lineSeparator();
        for (String word : words) {
            output.append(word).append(": ").append(counts.get(word)).append(separator);
        }
        System.out.print(output.toString());
    }

    // Вспомогательный метод для определения алфавита по первой букве
    private static boolean isRussian(char c) {
        return (c >= 'А' && c <= 'Я') || (c >= 'а' && c <= 'я') || c == 'Ё' || c == 'ё';
    }
}