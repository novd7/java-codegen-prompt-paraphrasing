import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.Collator;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {
        // Читаем одну строку из System.in в UTF-8
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        String line = reader.readLine();

        if (line == null || line.isEmpty()) {
            return;
        }

        // Регулярное выражение для извлечения слов. 
        // [\\p{IsCyrillic}a-zA-Z]+ — одна или более букв кириллицы или латиницы.
        // Символ 'ё' и 'Ё' входят в блок \p{IsCyrillic}.
        Pattern wordPattern = Pattern.compile("[\\p{IsCyrillic}a-zA-Z]+", Pattern.UNICODE_CHARACTER_CLASS);
        Matcher matcher = wordPattern.matcher(line);

        Map<String, Integer> counts = new HashMap<>();
        while (matcher.find()) {
            // Приводим к нижнему регистру в UTF-8 (toLowerCase(Locale.ROOT) корректно работает с Unicode)
            String word = matcher.group().toLowerCase(Locale.ROOT);
            counts.merge(word, 1, Integer::sum);
        }

        if (counts.isEmpty()) {
            return;
        }

        // Получаем коллаторы для правильного алфавитного порядка
        Collator ruCollator = Collator.getInstance(new Locale("ru", "RU"));
        // В Java для русской локали 'ё' по умолчанию стоит после 'е', что соответствует условию задачи.
        Collator enCollator = Collator.getInstance(Locale.ENGLISH);

        // Сортируем слова
        List<String> sortedWords = counts.keySet().stream()
                .sorted((w1, w2) -> {
                    int countCompare = counts.get(w2).compareTo(counts.get(w1));
                    if (countCompare != 0) {
                        return countCompare;
                    }
                    // Если частоты равны, сравниваем по алфавиту
                    boolean w1IsRussian = isRussianWord(w1);
                    boolean w2IsRussian = isRussianWord(w2);

                    if (w1IsRussian && w2IsRussian) {
                        return ruCollator.compare(w1, w2);
                    }
                    if (!w1IsRussian && !w2IsRussian) {
                        return enCollator.compare(w1, w2);
                    }
                    // По условию задачи слова разных алфавитов в одной строке не встретятся,
                    // но для полноты реализации (если это случится) можно задать детерминированный порядок.
                    return w1IsRussian ? 1 : -1;
                })
                .collect(Collectors.toList());

        // Выводим результат в UTF-8
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8));
        for (String word : sortedWords) {
            writer.write(word + ": " + counts.get(word));
            writer.newLine();
        }
        writer.flush();
    }

    // Вспомогательный метод для определения алфавита слова
    private static boolean isRussianWord(String word) {
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            // Проверяем попадание в диапазоны кириллицы (А-я) и отдельно Ёё
            if ((c >= 'А' && c <= 'я') || c == 'Ё' || c == 'ё') {
                return true;
            }
            // Латиница
            if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
                return false;
            }
        }
        return false;
    }
}