import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {

    // Регулярное выражение для поиска слов.
    // [A-Za-zА-Яа-яЁё]+ — одна или более букв английского или русского алфавитов.
    // (?i) — флаг, делающий поиск регистронезависимым.
    private static final Pattern WORD_PATTERN = Pattern.compile("(?i)[A-Za-zА-Яа-яЁё]+");

    public static void main(String[] args) throws Exception {
        // Читаем одну строку из stdin в UTF-8
        String line;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            line = reader.readLine();
        }

        // Если строка пустая или null (EOF), выводим пустую строку и завершаем работу
        if (line == null || line.isEmpty()) {
            System.out.println();
            return;
        }

        Map<String, Integer> counts = new LinkedHashMap<>();
        Matcher matcher = WORD_PATTERN.matcher(line);

        // Извлекаем слова, нормализуем к нижнему регистру и считаем вхождения
        while (matcher.find()) {
            String word = matcher.group().toLowerCase(Locale.ROOT);
            counts.merge(word, 1, Integer::sum);
        }

        // Сортируем и выводим результат
        counts.entrySet().stream()
                .sorted(getEntryComparator())
                .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));
    }

    // Компаратор для сортировки Map.Entry<String, Integer>
    private static Comparator<Map.Entry<String, Integer>> getEntryComparator() {
        return Comparator
                // 1. По убыванию количества (самые частые в начале)
                .comparing((Map.Entry<String, Integer> e) -> e.getValue(), Comparator.reverseOrder())
                // 2. Английские слова идут перед русскими при равной частоте
                .thenComparing(e -> isEnglish(e.getKey()) ? 0 : 1)
                // 3. Внутри группы — по алфавиту (String.compareTo корректно работает
                //    и для a-z, и для русского, где 'ё' идет после 'е')
                .thenComparing(Map.Entry::getKey);
    }

    // Проверяет, состоит ли слово только из латинских букв a-z
    private static boolean isEnglish(String word) {
        return word.codePoints().allMatch(cp -> cp >= 'a' && cp <= 'z');
    }
}