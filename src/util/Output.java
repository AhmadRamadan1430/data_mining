package util;

import java.util.List;

/**
 * Created by Mingwei Zhang on 2/17/14.
 *
 * @author Mingwei Zhang
 */
public class Output {
    public static void p(String str) {
        System.out.println(str);
    }

    public static void p(String str, Object... args) {
        System.out.printf(str, args);
    }

    public static void p(List<String[]> stringList) {
        for (String[] strlist : stringList) {
            for (String str : strlist) {
                System.out.printf("%s ", str);
            }
            System.out.println();
        }
    }

    public static void p(String[] stringList) {
        for (String str : stringList)
            System.out.printf("%s ", str);
        System.out.println();
    }

    public static void p(Integer[] integerList) {
        for (Integer i : integerList)
            System.out.printf("%d ", i);
        System.out.println();
    }
}
