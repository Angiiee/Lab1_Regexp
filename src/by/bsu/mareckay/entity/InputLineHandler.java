package by.bsu.mareckay.entity;

import by.bsu.mareckay.util.Consts;
import by.bsu.mareckay.regex.entity.Node;
import by.bsu.mareckay.util.Util;

import java.util.List;
import java.util.Stack;

import static by.bsu.mareckay.util.Util.isOr;

public class InputLineHandler {

    public static Operation find(List<Node> pattern) {
        String[] operatorsPriority = {Character.toString(Consts.OR), Consts.CONCAT, Character.toString(Consts.REPEAT)};
        int operatorPosition = -1;
        int offset = 1;
        for (int i = 0; i < operatorsPriority.length; i++) {
            if (i == 1) {
                operatorPosition = findConcatinationPosition(pattern);
                if (operatorPosition != -1) {
                    if (operatorPosition != 0) {
                        offset = 0;
                    }
                    return new Operation(pattern.get(operatorPosition).getValue().charAt(0), operatorPosition, offset);
                }
            } else {
                operatorPosition = findOperationPosition(pattern, operatorsPriority[i].charAt(0));
                if (operatorPosition != -1) {
                    if (operatorPosition != 0 && !isOr(operatorsPriority[i].charAt(0))) {
                        offset = 0;
                    }
                    return new Operation(operatorsPriority[i].charAt(0), operatorPosition, offset);
                }
            }
        }
        return null;
    }

    private static int findConcatinationPosition(List<Node> pattern) {
        int position = -1;
        int currentPosition = 0;
        for (Node node : pattern) {
            if (node.getValue().length() == 1 && Util.includeCharacter(node.getValue().charAt(0)) && !isDelimiter(node.getValue().charAt(0))) {
                position = currentPosition;
                break;
            }
            currentPosition++;
        }
        return position;
    }

    private static int findOperationPosition(List<Node> pattern, char operation) {
        int position = -1;
        int currentPosition = 0;
        for (Node node : pattern) {
            if (node.getValue().charAt(0) == operation) {
                position = currentPosition;
                break;
            }
            currentPosition++;
        }
        return position;
    }

    private static boolean isDelimiter(char symbol) {
        return symbol == Consts.BRACKET_RIGHT ||
                symbol == Consts.BRACKET_LEFT ||
                symbol == Consts.REPEAT ||
                symbol == Consts.OR;
    }

    public static String addNecessaryBrackets(String pattern) {
        return addBracketsAroundOr(
                addBracketsAroundSingleRepeatSymbol(pattern)
        );
    }

    private static String addBracketsAroundSingleRepeatSymbol(String pattern) {
        String[] splitResult = pattern.split(Consts.REPEAT_REGEX);
        for (int i = 0; i < splitResult.length - 1; i++) {
            if (splitResult[i].charAt(splitResult[i].length() - 1) != Consts.BRACKET_RIGHT) {
                splitResult[i] = splitResult[i].substring(0, splitResult[i].length() - 1) +
                        Consts.BRACKET_LEFT +
                        splitResult[i].charAt(splitResult[i].length() - 1) +
                        Consts.BRACKET_RIGHT;
            }
        }
        return String.join(Character.toString(Consts.REPEAT), splitResult);
    }

    private static String addBracketsAroundOr(String pattern) {
        String[] splitResult = pattern.split(Consts.OR_REGEX);
        if (splitResult.length == 2) {
            Stack<Character> stack = new Stack<>();
            for (int i = splitResult[0].length() - 1; i >= 0; i--) {
                char symbol = splitResult[0].charAt(i);
                if (symbol == Consts.BRACKET_LEFT) {
                    stack.push(symbol);
                } else if (symbol == Consts.BRACKET_RIGHT) {
                    stack.pop();
                }
            }
            if (stack.size() == 0) {
                splitResult[0] = Consts.BRACKET_LEFT + splitResult[0];
                splitResult[1] = splitResult[1] + Consts.BRACKET_RIGHT;
            }
        }
        return String.join(Character.toString(Consts.OR), splitResult);
    }

    public static String removeBracketsAroundGroup(String pattern) {
        if (pattern.charAt(0) == Consts.BRACKET_LEFT &&
                pattern.charAt(pattern.length() - 1) == Consts.BRACKET_RIGHT) {
            return pattern.substring(1, pattern.length() - 1);
        }
        return pattern;
    }
}
