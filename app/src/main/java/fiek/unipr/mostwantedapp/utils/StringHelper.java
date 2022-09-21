package fiek.unipr.mostwantedapp.utils;

public class StringHelper {
    public static boolean empty( final String s ) {
        // Null-safe, short-circuit evaluation.
        return s == null || s.trim().isEmpty();
    }

    public static String removeLastChar(String s)
    {
        return s.substring(0, s.length() - 1);
    }

    public static String removeFirstLetterAndLastLetter(String str)
    {
        str = str.substring(1, str.length() - 1);
        return str;
    }
}
