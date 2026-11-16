import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {

    // Порядок букв для кастомной сортировки: a-z, затем А-Я, затем Ё, затем а-я, затем ё
    private static final String SORT_ORDER = "abcdefghijklmnopqrstuvwxyz" +
            "АБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ" +
            "Ё" +
            "абвгдежзийклмнопрстуфхцчшщъыьэюя" +
            "ё";

    // Массив для быстрого поиска индекса символа в порядке сортировки
    private static final int[] CHAR_INDEX = new int[1114112];

    static {
        // Инициализируем массив значением -1 (символ не найден в нашем алфавите)
        Arrays.fill(CHAR_INDEX, -1);
        for (int i = 0; i < SORT_ORDER.length(); i++) {
            CHAR_INDEX[SORT_ORDER.charAt(i)] = i;
        }
    }

    public static void main(String[] args) throws Exception {
        // Читаем строку из stdin в UTF-8
        String line = new InputStreamReader(System.in, StandardCharsets.UTF_8).readLine();
        if (line == null) {
            return;
        }

        // Регулярное выражение: одна или более букв (латиница или кириллица, включая ё/Ё)
        Pattern wordPattern = Pattern.compile("[A-Za-zА-Яа-яЁё]+");
        Matcher matcher = wordPattern.matcher(line);

        Map<String, Integer> counts = new HashMap<>();
        while (matcher.find()) {
            // Приводим к нижнему регистру в UTF-8 (Java делает это корректно по умолчанию)
            String word = matcher.group().toLowerCase(Locale.ROOT);
            counts.merge(word, 1, Integer::sum);
        }

        if (counts.isEmpty()) {
            // Если слов нет — завершаем программу без вывода
            return;
        }

        // Сортируем слова по правилам задачи
        List<Map.Entry<String, Integer>> sorted = counts.entrySet().stream()
                .sorted((e1, e2) -> {
                    int freqCompare = Integer.compare(e2.getValue(), e1.getValue()); // от частых к редким
                    if (freqCompare != 0) {
                        return freqCompare;
                    }
                    // При равной частоте — по алфавиту с учетом специфики «ё»
                    return compareWords(e1.getKey(), e2.getKey());
                })
                .toList();

        // Выводим результат в stdout в UTF-8
        var writer = new OutputStreamWriter(System.out, StandardCharsets.UTF_8);
        for (Map.Entry<String, Integer> entry : sorted) {
            writer.write(entry.getKey() + ": " + entry.getValue() + "\n");
        }
        writer.flush();
    }

    // Сравнение двух слов, состоящих только из букв одного алфавита
    private static int compareWords(String w1, String w2) {
        int len = Math.min(w1.length(), w2.length());
        for (int i = 0; i < len; i++) {
            int idx1 = CHAR_INDEX[w1.charAt(i)];
            int idx2 = CHAR_INDEX[w2.charAt(i)];
            
            // На случай, если в строке встретятся символы вне заданных алфавитов
            if (idx1 == -1 && idx2 == -1) continue;
            if (idx1 == -1) return 1;
            if (idx2 == -1) return -1;

            if (idx1 != idx2) {
                return Integer.compare(idx1, idx2);
            }
        }
        return Integer.compare(w1.length(), w2.length());
    }
}