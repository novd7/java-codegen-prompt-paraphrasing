import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.Collator;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) throws IOException {
        // Читаем одну строку из stdin в UTF-8
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        String line = reader.readLine();

        if (line == null || line.isEmpty()) {
            // Если строка пустая или null, вывод оставляем пустым
            return;
        }

        // Регулярное выражение для поиска слов. 
        // [^...] — любой символ, не входящий в набор (т.е. разделитель).
        // Набор включает латиницу и кириллицу (включая Ё и ё).
        Pattern wordPattern = Pattern.compile("[A-Za-zА-Яа-яЁё]+");
        Matcher matcher = wordPattern.matcher(line);

        Map<String, Integer> counts = new HashMap<>();

        // Подсчитываем слова в нижнем регистре
        while (matcher.find()) {
            String word = matcher.group().toLowerCase(Locale.ROOT);
            counts.merge(word, 1, Integer::sum);
        }

        if (counts.isEmpty()) {
            // Если слов не найдено, вывод оставляем пустым
            return;
        }

        // Коллаторы для правильной сортировки по алфавиту
        Collator ruCollator = Collator.getInstance(new Locale("ru"));
        ruCollator.setStrength(Collator.PRIMARY);

        Collator enCollator = Collator.getInstance(new Locale("en"));
        enCollator.setStrength(Collator.PRIMARY);

        // Сортируем слова
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(counts.entrySet());
        sorted.sort((e1, e2) -> {
            int freqCompare = Integer.compare(e2.getValue(), e1.getValue()); // по убыванию частоты
            if (freqCompare != 0) {
                return freqCompare;
            }

            String w1 = e1.getKey();
            String w2 = e2.getKey();

            boolean isRu1 = isRussian(w1);
            boolean isRu2 = isRussian(w2);

            // Если алфавиты разные — русские слова ставим первыми
            if (isRu1 != isRu2) {
                return isRu1 ? -1 : 1;
            }

            // Если алфавит один, сравниваем через соответствующий Collator
            if (isRu1) {
                return ruCollator.compare(w1, w2);
            } else {
                return enCollator.compare(w1, w2);
            }
        });

        // Выводим результат в UTF-8
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8));
        for (Map.Entry<String, Integer> entry : sorted) {
            writer.write(entry.getKey() + ": " + entry.getValue());
            writer.newLine();
        }
        writer.flush();
    }

    // Вспомогательный метод для определения алфавита слова
    private static boolean isRussian(String word) {
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            // Проверяем попадание в диапазоны кириллицы
            if ((c >= 'А' && c <= 'Я') || (c >= 'а' && c <= 'я') || c == 'Ё' || c == 'ё') {
                return true;
            }
        }
        return false;
    }
}