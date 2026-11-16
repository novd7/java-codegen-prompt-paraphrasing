import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {
    // Диапазоны кодов Unicode для определения алфавита
    private static final int CYRILLIC_START = 0x0400;
    private static final int CYRILLIC_END = 0x04FF;

    public static void main(String[] args) throws IOException {
        String line = readLine();
        
        if (line == null || line.isEmpty()) {
            return;
        }

        // Регулярное выражение для поиска слов. 
        // (?i) — регистронезависимый поиск.
        // [A-Za-zА-Яа-яЁё]+ — одна или более букв английского или русского алфавита (включая Ёё).
        Pattern pattern = Pattern.compile("(?i)[A-Za-zА-Яа-яЁё]+");
        Matcher matcher = pattern.matcher(line);

        Map<String, Integer> counts = new HashMap<>();

        while (matcher.find()) {
            String word = matcher.group().toLowerCase(Locale.ROOT);
            counts.merge(word, 1, Integer::sum);
        }

        if (counts.isEmpty()) {
            return;
        }

        // Сортируем слова по правилам задачи
        List<Map.Entry<String, Integer>> sorted = counts.entrySet().stream()
            .sorted((e1, e2) -> {
                int cmp = e2.getValue().compareTo(e1.getValue()); // 1. По убыванию количества
                if (cmp != 0) {
                    return cmp;
                }
                
                // 2. По алфавиту внутри своего языка
                boolean e1IsCyrillic = isCyrillic(e1.getKey());
                boolean e2IsCyrillic = isCyrillic(e2.getKey());

                if (e1IsCyrillic && e2IsCyrillic) {
                    return e1.getKey().compareTo(e2.getKey());
                }
                if (!e1IsCyrillic && !e2IsCyrillic) {
                    return e1.getKey().compareTo(e2.getKey());
                }

                // 3. Если языки разные, русские слова идут перед английскими
                // (это требование не было явно указано, но логично вытекает из условия 
                // раздельной проверки алфавитного порядка для вывода в одном потоке)
                return e1IsCyrillic ? -1 : 1;
            })
            .collect(Collectors.toList());

        // Вывод в UTF-8
        for (Map.Entry<String, Integer> entry : sorted) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    private static String readLine() throws IOException {
        Console console = System.console();
        if (console != null) {
            // Console по умолчанию использует кодировку терминала (в современных ОС это UTF-8)
            return console.readLine();
        } else {
            // Fallback для сред, где console недоступен (например, IDE)
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
                return reader.readLine();
            }
        }
    }

    private static boolean isCyrillic(String word) {
        if (word.isEmpty()) {
            return false;
        }
        int codePoint = word.codePointAt(0);
        return codePoint >= CYRILLIC_START && codePoint <= CYRILLIC_END;
    }
}