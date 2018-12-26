package by.bsu.mareckay.util;

public class Util {

    public static boolean includeCharacter(char symbol) {
        return Consts.CHAR_SYMBOLS.indexOf(symbol) != -1;
    }

    public static boolean isOr(char symbol) {
        return symbol == Consts.OR;
    }

    public static boolean isConcat(char symbol) {
        return includeCharacter(symbol);
    }

    public static boolean isRepeat(char symbol) {
        return symbol == Consts.REPEAT;
    }
}
