import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) throws Exception {
        // Читаем одну строку из UTF-8 stdin
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            String line = reader.readLine();

            // Если строка пустая или null, выводим пустоту и завершаем работу
            if (line == null || line.isEmpty()) {
                return;
            }

            // Регулярное выражение для извлечения слов. 
            // [A-Za-zА-Яа-яЁё]+ находит непрерывные последовательности букв обоих алфавитов.
            Pattern wordPattern = Pattern.compile("[A-Za-zА-Яа-яЁё]+");
            Matcher matcher = wordPattern.matcher(line);

            // LinkedHashMap сохранит порядок вставки, который мы зададим при финальной сортировке
            Map<String, Integer> counts = new LinkedHashMap<>();

            while (matcher.find()) {
                // Приводим к нижнему регистру. Locale.ROOT гарантирует, что 
                // правила локали не исказят буквы (например, не превратит 'i' в 'İ' в турецкой локали).
                String word = matcher.group().toLowerCase(Locale.ROOT);
                counts.merge(word, 1, Integer::sum);
            }

            // Если слов нет, ничего не выводим
            if (counts.isEmpty()) {
                return;
            }

            // Сортируем слова по правилам задачи
            counts.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder()) // 1. По убыванию частоты
                            .thenComparing(Map.Entry.comparingByKey())) // 2. По алфавиту (a-z, А-Я, Ё, а-я, ё)
                    .forEach(entry -> {
                        // Вывод в UTF-8 stdout (по умолчанию в Java 21, но явное указание делает код надежнее)
                        System.out.println(entry.getKey() + ": " + entry.getValue());
                    });
        }
    }
}