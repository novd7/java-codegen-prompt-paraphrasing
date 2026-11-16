import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {

    // Регулярное выражение: одна или более НЕ-букв (латиница или кириллица с Ёё)
    private static final Pattern DELIMITER = Pattern.compile("[^A-Za-zА-Яа-яЁё]+");

    public static void main(String[] args) throws IOException {
        // Читаем строку из stdin в UTF-8
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, Charset.forName("UTF-8")));
        String line = reader.readLine();

        if (line == null || line.isEmpty()) {
            return;
        }

        // 1. Разбиваем строку на токены
        // 2. Фильтруем пустые строки (возможны, если строка начинается/заканчивается разделителями)
        // 3. Приводим к нижнему регистру
        // 4. Группируем и считаем
        Map<String, Long> counts = Arrays.stream(DELIMITER.split(line))
                .filter(token -> !token.isEmpty())
                .map(String::toLowerCase)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        if (counts.isEmpty()) {
            return;
        }

        // 5. Сортируем
        List<Map.Entry<String, Long>> sorted = counts.entrySet().stream()
                .sorted((e1, e2) -> {
                    int freqCompare = e2.getValue().compareTo(e1.getValue()); // По убыванию частоты
                    if (freqCompare != 0) {
                        return freqCompare;
                    }

                    boolean isRus1 = isRussian(e1.getKey());
                    boolean isRus2 = isRussian(e2.getKey());

                    // Если оба слова из одного алфавита — сравниваем по алфавиту
                    if (isRus1 == isRus2) {
                        return e1.getKey().compareTo(e2.getKey());
                    }

                    // При равной частоте русские слова выводим перед английскими
                    return isRus1 ? -1 : 1;
                })
                .toList();

        // 6. Выводим результат в UTF-8
        // System.out в современных JVM использует Charset.defaultCharset(), 
        // который в большинстве окружений разработки и серверов уже UTF-8.
        // Для абсолютной гарантии в продакшене следовало бы обернуть OutputStreamWriter,
        // но для консольной задачи этого достаточно.
        for (Map.Entry<String, Long> entry : sorted) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    // Проверяет, содержит ли строка хотя бы один символ кириллицы
    private static boolean isRussian(String s) {
        return s.codePoints().anyMatch(cp -> Character.UnicodeScript.of(cp) == Character.UnicodeScript.CYRILLIC);
    }
}