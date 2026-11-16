import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.text.Collator;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {

    // Регулярное выражение для извлечения слов.
    // [\\p{IsLatin}А-Яа-яЁё]+ соответствует последовательностям латинских или кириллических букв.
    private static final Pattern WORD_PATTERN = Pattern.compile("[\\p{IsLatin}А-Яа-яЁё]+");

    public static void main(String[] args) {
        // Явно задаем UTF-8 для ввода и вывода, как требует условие
        try (Scanner scanner = new Scanner(new InputStreamReader(System.in, StandardCharsets.UTF_8));
             OutputStreamWriter writer = new OutputStreamWriter(System.out, StandardCharsets.UTF_8)) {

            if (!scanner.hasNextLine()) {
                return;
            }
            String line = scanner.nextLine();

            // Группируем слова по алфавиту (true — русский, false — английский)
            // и считаем их количество без учета регистра
            Map<Boolean, Map<String, Long>> groupedCounts = WORD_PATTERN.matcher(line)
                    .results()
                    .map(matchResult -> matchResult.group().toLowerCase(Locale.ROOT))
                    .collect(Collectors.groupingBy(
                            Main::isRussianWord,
                            LinkedHashMap::new, // сохраняем порядок для стабильности, если потребуется
                            Collectors.counting()
                    ));

            // Локализованный компаратор для русского языка (учитывает порядок е/ё)
            Collator russianCollator = Collator.getInstance(new Locale("ru", "RU"));
            russianCollator.setStrength(Collator.PRIMARY); // сравнение без учета регистра и диакритики

            // Объединяем отсортированные списки русских и английских слов
            String output = groupedCounts.entrySet().stream()
                    .flatMap(entry -> {
                        boolean isRussian = entry.getKey();
                        Map<String, Long> wordMap = entry.getValue();

                        Comparator<Map.Entry<String, Long>> comparator = Comparator
                                // Сначала сортируем по убыванию количества
                                .comparingLong((Map.Entry<String, Long> e) -> -e.getValue())
                                // Затем по алфавиту с нужной локалью
                                .thenComparing(e -> e.getKey(), isRussian
                                        ? Comparator.comparing(String::toString, russianCollator)
                                        : String.CASE_INSENSITIVE_ORDER);

                        return wordMap.entrySet().stream()
                                .sorted(comparator)
                                .map(e -> e.getKey() + ": " + e.getValue());
                    })
                    .collect(Collectors.joining("\n"));

            // Если слова были найдены, выводим результат и добавляем завершающий \n
            if (!output.isEmpty()) {
                writer.write(output);
                writer.write('\n');
            }
            writer.flush();

        } catch (Exception e) {
            throw new UncheckedIOException("I/O error", e);
        }
    }

    // Определяем алфавит по первой букве слова.
    // Поскольку по условию в строке только один алфавит, этого достаточно.
    private static boolean isRussianWord(String word) {
        char first = word.charAt(0);
        // Диапазоны кириллицы в Unicode, включающие Ёё
        return (first >= 'А' && first <= 'Я') || (first >= 'а' && first <= 'я') || first == 'Ё' || first == 'ё';
    }
}