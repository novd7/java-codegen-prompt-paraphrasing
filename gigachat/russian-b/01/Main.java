import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Консольная программа для подсчета частоты слов в строке.
 * Поддерживает Java 21.
 * Слово — непрерывная последовательность букв одного алфавита (русского или английского).
 * Небуквенные символы разделяют слова. Подсчет ведется без учета регистра.
 * Слова выводятся в нижнем регистре, отсортированные по убыванию частоты,
 * а при равенстве — в алфавитном порядке (с учетом позиции буквы 'ё').
 */
public class Main {

    public static void main(String[] args) throws IOException {
        // Читаем строку из stdin в UTF-8
        InputStreamReader reader = new InputStreamReader(System.in, StandardCharsets.UTF_8);
        StringBuilder inputBuilder = new StringBuilder();
        int charCode;
        while ((charCode = reader.read()) != -1 && charCode != '\n') {
            inputBuilder.append((char) charCode);
        }
        String input = inputBuilder.toString();

        // Подсчитываем слова
        Map<String, Integer> counts = countWords(input);

        // Сортируем и выводим результат в stdout в UTF-8
        OutputStreamWriter writer = new OutputStreamWriter(System.out, StandardCharsets.UTF_8);
        if (!counts.isEmpty()) {
            List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());
            entries.sort(new WordEntryComparator());

            for (Map.Entry<String, Integer> entry : entries) {
                writer.write(entry.getKey() + ": " + entry.getValue() + "\n");
            }
        }
        writer.flush();
    }

    /**
     * Разбивает строку на слова и подсчитывает их вхождения.
     * Слово определяется как непрерывная последовательность букв одного алфавита.
     * Небуквенные символы и смена алфавита служат разделителями.
     */
    private static Map<String, Integer> countWords(String input) {
        Map<String, Integer> map = new HashMap<>();
        StringBuilder currentWord = new StringBuilder();
        Character currentAlphabet = null; // null, "ru" или "en"

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            Character alphabet = getAlphabet(c);

            if (alphabet != null) {
                // Если алфавит не определен или совпадает с текущим — продолжаем собирать слово
                if (currentAlphabet == null || currentAlphabet.equals(alphabet)) {
                    currentWord.append(c);
                    currentAlphabet = alphabet;
                } else {
                    // Смена алфавита внутри последовательности — завершаем предыдущее слово
                    saveWord(map, currentWord);
                    currentWord.setLength(0);
                    currentWord.append(c);
                    currentAlphabet = alphabet;
                }
            } else {
                // Небуквенный символ — разделитель
                saveWord(map, currentWord);
                currentWord.setLength(0);
                currentAlphabet = null;
            }
        }

        // Сохраняем последнее слово, если строка закончилась буквой
        saveWord(map, currentWord);
        return map;
    }

    private static void saveWord(Map<String, Integer> map, StringBuilder wordBuilder) {
        if (wordBuilder.length() > 0) {
            String word = wordBuilder.toString().toLowerCase(StandardCharsets.UTF_8);
            map.merge(word, 1, Integer::sum);
        }
    }

    /**
     * Определяет алфавит символа.
     * Возвращает "ru" для русских букв, "en" для английских, null для остальных.
     */
    private static Character getAlphabet(char c) {
        // Русский алфавит (включая Ё и ё)
        if ((c >= 'А' && c <= 'Я') || (c >= 'а' && c <= 'я') || c == 'Ё' || c == 'ё') {
            return 'r';
        }
        // Английский алфавит
        if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
            return 'e';
        }
        return null;
    }

    /**
     * Компаратор для сортировки:
     * 1. По убыванию количества вхождений.
     * 2. По алфавиту (русский отдельно от английского, внутри — по своим правилам).
     */
    private static class WordEntryComparator implements Comparator<Map.Entry<String, Integer>> {
        @Override
        public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
            // 1. Сортировка по количеству (по убыванию)
            int countCompare = Integer.compare(o2.getValue(), o1.getValue());
            if (countCompare != 0) {
                return countCompare;
            }

            // 2. Сортировка по алфавиту
            String w1 = o1.getKey();
            String w2 = o2.getKey();

            boolean isRu1 = isRussianWord(w1);
            boolean isRu2 = isRussianWord(w2);

            // Разные алфавиты: русские слова всегда идут перед английскими
            if (isRu1 != isRu2) {
                return isRu1 ? -1 : 1;
            }

            // Один алфавит: посимвольное сравнение с учетом позиции 'ё'
            int len = Math.min(w1.length(), w2.length());
            for (int i = 0; i < len; i++) {
                char c1 = w1.charAt(i);
                char c2 = w2.charAt(i);
                if (c1 != c2) {
                    return isRu1 ? compareRussianChars(c1, c2) : Character.compare(c1, c2);
                }
            }
            return Integer.compare(w1.length(), w2.length());
        }

        private boolean isRussianWord(String word) {
            // Так как по условию слово состоит из букв только одного алфавита,
            // достаточно проверить первый символ.
            char c = word.charAt(0);
            return (c >= 'А' && c <= 'Я') || (c >= 'а' && c <= 'я') || c == 'Ё' || c == 'ё';
        }

        /**
         * Сравнение русских букв с учетом правила: 'е' < 'ё'.
         */
        private int compareRussianChars(char c1, char c2) {
            // Приведение к нижнему регистру для единообразия (хотя в слова уже в нижнем)
            c1 = Character.toLowerCase(c1);
            c2 = Character.toLowerCase(c2);

            if (c1 == 'ё' && c2 == 'ё') return 0;
            if (c1 == 'ё') return 1; // 'ё' больше 'е' и всех букв до неё
            if (c2 == 'ё') return -1;
            return Character.compare(c1, c2);
        }
    }
}