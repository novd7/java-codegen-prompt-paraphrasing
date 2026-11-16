import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws Exception {
        // Читаем одну строку из stdin в UTF-8
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            String line = reader.readLine();

            if (line == null || line.isEmpty()) {
                return;
            }

            // Регулярное выражение для поиска слов (буквы А-Я, а-я, Ё, ё, A-Z, a-z)
            // Флаг CASE_INSENSITIVE не обязателен для логики, так как мы всё равно приводим к нижнему регистру,
            // но он делает работу регулярного выражения более предсказуемой.
            Pattern wordPattern = Pattern.compile("(?i)[A-Za-zА-Яа-яЁё]+");
            Matcher matcher = wordPattern.matcher(line);

            Map<String, Integer> counts = new HashMap<>();

            // Собираем слова, нормализуем регистр и считаем вхождения
            while (matcher.find()) {
                String word = matcher.group().toLowerCase(Locale.ROOT);
                counts.merge(word, 1, Integer::sum);
            }

            if (counts.isEmpty()) {
                return;
            }

            // Сортируем слова:
            // 1. По убыванию количества вхождений
            // 2. По алфавиту (внутри своего алфавита). 
            // Java сравнивает строки Unicode по кодам символов. 
            // Для латиницы это a-z, для кириллицы это алфавитный порядок (е перед ё).
            List<Map.Entry<String, Integer>> sorted = counts.entrySet().stream()
                    .sorted((a, b) -> {
                        int valueComparison = Integer.compare(b.getValue(), a.getValue());
                        if (valueComparison != 0) {
                            return valueComparison;
                        }

                        char firstA = a.getKey().charAt(0);
                        char firstB = b.getKey().charAt(0);
                        boolean isRussianA = firstA >= 'а' && firstA <= 'я' || firstA == 'ё';
                        boolean isRussianB = firstB >= 'а' && firstB <= 'я' || firstB == 'ё';

                        // Разные алфавиты: русские слова считаем "меньше" (выше в приоритете при равенстве count),
                        // чтобы они шли первыми, если это потребуется. 
                        // Однако по условию алфавитный порядок проверяется отдельно, 
                        // поэтому при разных алфавитах просто ставим их в стандартный лексикографический порядок.
                        if (isRussianA != isRussianB) {
                            return a.getKey().compareTo(b.getKey());
                        }

                        // Одинаковый алфавит: обычный строковый порядок
                        return a.getKey().compareTo(b.getKey());
                    })
                    .toList();

            // Выводим результат в UTF-8
            StringBuilder output = new StringBuilder();
            for (Map.Entry<String, Integer> entry : sorted) {
                output.append(entry.getKey())
                      .append(": ")
                      .append(entry.getValue())
                      .append('\n');
            }

            System.out.print(output.toString());
        }
    }
}