import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordCounter {

    // Регулярное выражение для извлечения слов. 
    // (?U) включает Unicode-свойства для \w и классов символов.
    // [А-Яа-яЁё]+ — одно или более русских букв (включая Ё/ё).
    // | — или.
    // [A-Za-z]+ — одно или более английских букв.
    private static final Pattern WORD_PATTERN = Pattern.compile("(?U)[А-Яа-яЁё]+|[A-Za-z]+");

    // Проверяет, содержит ли строка хотя бы одну кириллическую букву
    private static boolean isRussianWord(String word) {
        return word.codePoints().anyMatch(cp -> Character.UnicodeScript.of(cp) == Character.UnicodeScript.CYRILLIC);
    }

    public static void main(String[] args) throws Exception {
        // Читаем одну строку строго в UTF-8
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            String line = reader.readLine();
            
            // Если строка пустая или null (EOF), вывод оставляем пустым
            if (line == null || line.isEmpty()) {
                return;
            }

            Map<String, Integer> counts = new LinkedHashMap<>();
            Matcher matcher = WORD_PATTERN.matcher(line);

            // Извлекаем слова, нормализуем к нижнему регистру и считаем
            while (matcher.find()) {
                String word = matcher.group().toLowerCase(Locale.ROOT);
                counts.merge(word, 1, Integer::sum);
            }

            if (counts.isEmpty()) {
                return;
            }

            // Разделяем слова на русские и английские для раздельной проверки алфавитного порядка
            List<String> russianWords = new ArrayList<>();
            List<String> englishWords = new ArrayList<>();

            for (String word : counts.keySet()) {
                if (isRussianWord(word)) {
                    russianWords.add(word);
                } else {
                    englishWords.add(word);
                }
            }

            // Коллейторы для правильной сортировки с учетом позиции буквы 'ё'
            Collator russianCollator = Collator.getInstance(new Locale("ru", "RU"));
            russianCollator.setStrength(Collator.PRIMARY); // Игнорируем регистр (он и так приведен к нижнему)
            
            Collator englishCollator = Collator.getInstance(Locale.ENGLISH);
            englishCollator.setStrength(Collator.PRIMARY);

            // Сортировка русских слов
            russianWords.sort(Comparator
                    .comparing((String w) -> counts.get(w), Comparator.reverseOrder()) // По убыванию частоты
                    .thenComparing(w -> w, russianCollator)); // По алфавиту (ё после е)

            // Сортировка английских слов
            englishWords.sort(Comparator
                    .comparing((String w) -> counts.get(w), Comparator.reverseOrder())
                    .thenComparing(w -> w, englishCollator));

            // Выводим результат. System.out.println автоматически добавляет \n.
            for (String word : russianWords) {
                System.out.println(word + ": " + counts.get(word));
            }
            for (String word : englishWords) {
                System.out.println(word + ": " + counts.get(word));
            }
        }
    }
}