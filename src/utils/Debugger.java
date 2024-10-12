package utils;

public class Debugger {
    private static boolean debug = true;

    public static void DebugPrint(Object object) {
        if (debug) {
            System.out.println(object);
        }
    }
}
