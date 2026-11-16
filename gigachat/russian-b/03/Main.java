import java.io.Console;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        Console console = System.console();
        if (console == null) {
            return;
        }

        // Читаем строку в UTF-8
        String line = console.readLine();
        if (line == null || line.isEmpty()) {
            return;
        }

        // Регулярное выражение для поиска последовательностей букв
        // \p{IsCyrillic} — русские буквы, \p{IsLatin} — английские.
        // Ё и ё не входят в \p{IsCyrillic} по стандарту Unicode, поэтому добавляем их явно.
        Pattern wordPattern = Pattern.compile("[\\p{IsCyrillic}Ёё\\p{IsLatin}]+");
        Matcher matcher = wordPattern.matcher(line);

        Map<String, Integer> counts = new HashMap<>();
        while (matcher.find()) {
            String word = matcher.group().toLowerCase(StandardCharsets.UTF_8);
            counts.merge(word, 1, Integer::sum);
        }

        if (counts.isEmpty()) {
            return;
        }

        // Сортируем: 1. По убыванию количества. 
        // 2. По алфавиту (русские перед английскими, затем лексикографически).
        // Стандартный String::compareTo в Java корректно ставит 'ё' после 'е'.
        List<Map.Entry<String, Integer>> sorted = counts.entrySet().stream()
                .sorted((e1, e2) -> {
                    int freqCompare = e2.getValue().compareTo(e1.getValue());
                    if (freqCompare != 0) {
                        return freqCompare;
                    }
                    boolean isRus1 = isRussian(e1.getKey());
                    boolean isRus2 = isRussian(e2.getKey());
                    
                    // Разные алфавиты: русские слова идут первыми
                    if (isRus1 != isRus2) {
                        return isRus1 ? -1 : 1;
                    }
                    
                    // Одинаковый алфавит: стандартное лексикографическое сравнение
                    return e1.getKey().compareTo(e2.getKey());
                })
                .toList();

        // Выводим результат в UTF-8
        for (Map.Entry<String, Integer> entry : sorted) {
            console.printf("%s: %d%n", entry.getKey(), entry.getValue());
        }
    }

    // Проверка, состоит ли слово только из русских букв (включая ё/Ё)
    private static boolean isRussian(String word) {
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (c == 'ё' || c == 'Ё') {
                continue;
            }
            if (c < '\u0400' || c > '\u04FF') {
                return false;
            }
        }
        return true;
    }
}