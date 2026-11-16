import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.*;

public class WordCounter {
    
    // Custom comparator for Russian alphabet including ё after е
    private static final String RUSSIAN_ORDER = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя";
    private static final String ENGLISH_ORDER = "abcdefghijklmnopqrstuvwxyz";
    
    public static void main(String[] args) throws IOException {
        // Set up UTF-8 input
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(System.in, StandardCharsets.UTF_8)
        );
        
        String line = reader.readLine();
        if (line == null) {
            return;
        }
        
        // Count words
        Map<String, Integer> wordCount = new HashMap<>();
        Pattern wordPattern = Pattern.compile("\\p{L}+");
        Matcher matcher = wordPattern.matcher(line);
        
        while (matcher.find()) {
            String word = matcher.group().toLowerCase();
            wordCount.merge(word, 1, Integer::sum);
        }
        
        // Check if we have any words
        if (wordCount.isEmpty()) {
            return;
        }
        
        // Determine if we're dealing with Russian or English
        boolean isRussian = false;
        for (String word : wordCount.keySet()) {
            if (!word.isEmpty()) {
                char firstChar = word.charAt(0);
                if (firstChar >= 'а' && firstChar <= 'я' || firstChar == 'ё') {
                    isRussian = true;
                    break;
                }
            }
        }
        
        // Create sorted list
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(wordCount.entrySet());
        
        // Sort by frequency (descending), then alphabetically
        String alphabetOrder = isRussian ? RUSSIAN_ORDER : ENGLISH_ORDER;
        
        entries.sort((a, b) -> {
            int freqCompare = b.getValue().compareTo(a.getValue());
            if (freqCompare != 0) {
                return freqCompare;
            }
            return compareAlphabetically(a.getKey(), b.getKey(), alphabetOrder);
        });
        
        // Output results with UTF-8
        PrintWriter writer = new PrintWriter(
            new OutputStreamWriter(System.out, StandardCharsets.UTF_8)
        );
        
        for (Map.Entry<String, Integer> entry : entries) {
            writer.println(entry.getKey() + ": " + entry.getValue());
        }
        
        writer.flush();
    }
    
    private static int compareAlphabetically(String a, String b, String alphabetOrder) {
        int minLength = Math.min(a.length(), b.length());
        
        for (int i = 0; i < minLength; i++) {
            char charA = a.charAt(i);
            char charB = b.charAt(i);
            
            int indexA = alphabetOrder.indexOf(charA);
            int indexB = alphabetOrder.indexOf(charB);
            
            if (indexA != indexB) {
                return Integer.compare(indexA, indexB);
            }
        }
        
        return Integer.compare(a.length(), b.length());
    }
}