package BinarySearch;
import java.util.List;
import java.util.Comparator;

import static BinarySearch.Arrays.binarySearch;

public class Main {
    public static void main(String[] args) {

        System.out.println("\nArrays\n==========================================================================");

        byte[] byteArray = {1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25, 27, 29};
        byte byteKey = 19;
        System.out.println("Поиск для byte. Ожидаем получить 9");
        System.out.println("Результат без ренджа: " + binarySearch(byteArray, byteKey));
        System.out.println("Результат c ренджом: " + binarySearch(byteArray, 1, 12, byteKey) + "\n==========================================================================");

        char[] charArray = {'a', 'c', 'e', 'g', 'i', 'k', 'm', 'o', 'q', 's', 'u', 'w', 'y', 'z'};
        char charKey = 'y';
        System.out.println("Поиск для char. Ожидаем получить 12");
        System.out.println("Результат без ренджа: " + binarySearch(charArray, charKey));
        System.out.println("Результат c ренджом: " + binarySearch(charArray, 5, 13, charKey) + "\n==========================================================================");

        double[] doubleArray = {0.5, 1.2, 2.3, 3.4, 4.8, 5.9, 6.2, 7.1, 8.3, 9.5, 10.6, 11.9, 12.4, 13.7, 15.0};
        double doubleKey = 13.7;
        System.out.println("Поиск для double. Ожидаем получить 13");
        System.out.println("Результат без ренджа: " + binarySearch(doubleArray, doubleKey));
        System.out.println("Результат c ренджом: " + binarySearch(doubleArray, 8, 15, doubleKey) + "\n==========================================================================");

        float[] floatArray = {0.1f, 0.3f, 0.6f, 0.9f, 1.2f, 1.5f, 2.0f, 2.5f, 3.1f, 4.4f, 5.0f, 6.6f, 7.3f, 8.9f};
        float floatKey = 0.9f;
        System.out.println("Поиск для float. Ожидаем получить 3");
        System.out.println("Результат без ренджа: " + binarySearch(floatArray, floatKey));
        System.out.println("Результат c ренджом: " + binarySearch(floatArray, 0, 6, floatKey) + "\n==========================================================================");

        int[] intArray = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150};
        int intKey = 80;
        System.out.println("Поиск для int. Ожидаем получить 7");
        System.out.println("Результат без ренджа: " + binarySearch(intArray, intKey));
        System.out.println("Результат c ренджом: " + binarySearch(intArray, 3, 14, intKey) + "\n==========================================================================");

        long[] longArray = {100L, 200L, 300L, 400L, 500L, 600L, 700L, 800L, 900L, 1000L, 1100L, 1200L, 1300L, 1400L, 1500L};
        long longKey = 200L;
        System.out.println("Поиск для long. Ожидаем получить 1");
        System.out.println("Результат без ренджа: " + binarySearch(longArray, longKey));
        System.out.println("Результат c ренджом: " + binarySearch(longArray, 0, 8, longKey) + "\n==========================================================================");

        short[] shortArray = {2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30};
        short shortKey = 24;
        System.out.println("Поиск для short. Ожидаем получить 11");
        System.out.println("Результат без ренджа: " + binarySearch(shortArray, shortKey));
        System.out.println("Результат c ренджом: " + binarySearch(shortArray, 6, 14, shortKey) + "\n==========================================================================");


        String[] stringArray = {"ВШЭ", "КФУ", "МГИМО", "МГТУ", "МГУ", "МИФИ", "НГУ", "РТУ МИРЭА", "СПбПУ", "ТГУ", "ТУСУР", "УрФУ", "ЮФУ"};
        String stringKey = "МИФИ";
        System.out.println("Поиск для дженерика. Ожидаем получить 5");
        System.out.println("Результат без ренджа: " + binarySearch(stringArray, stringKey, Comparator.naturalOrder()));
        System.out.println("Результат c ренджом: " + binarySearch(stringArray, 2, 8, stringKey, Comparator.naturalOrder()) + "\n==========================================================================");


        System.out.println("\nCollections\n==========================================================================");

        List<Integer> list_ints = Arrays.asList(1, 3, 5, 7, 9, 11, 13, 15, 17, 19);

        int index_int = Collections.binarySearch(list_ints, 17);
        System.out.println("Поиск для инта по листу. Ожидаем получить 8\nРезультат:" + index_int + "\n==========================================================================");


        List<String> list_strings = Arrays.asList("ВШЭ", "КФУ", "МГИМО", "МГТУ", "МГУ", "МИФИ", "НГУ", "РТУ МИРЭА", "СПбПУ", "ТГУ", "ТУСУР", "УрФУ", "ЮФУ");

        Comparator<String> comparator = Comparator.naturalOrder();
        int index_string = Collections.binarySearch(list_strings, "МИФИ", comparator);
        System.out.println("Поиск для стринги по листу. Ожидаем получить 5\nРезультат:" + index_string + "\n==========================================================================");


    }
}
