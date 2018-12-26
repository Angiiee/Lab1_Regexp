package by.bsu.mareckay.regex.logic;

import by.bsu.mareckay.regex.entity.Node;
import by.bsu.mareckay.regex.entity.OperationNode;
import by.bsu.mareckay.util.Consts;
import by.bsu.mareckay.util.Util;

public class RegexHandlerHelper {

    public static Node replacePatternToRepeatNode(Node pattern) {
        if (isRepeatPattern(pattern.getLeft())) {
            pattern.setLeft(new OperationNode.Repeat(Character.toString(pattern.getLeft().getValue().charAt(1))));
        } else if (isRepeatPattern(pattern.getRight())) {
            pattern.setRight(new OperationNode.Repeat(Character.toString(pattern.getRight().getValue().charAt(1))));
        }
        return pattern;
    }

    public static boolean isRepeatPattern(Node pattern) {
        if (pattern == null || pattern.getValue() == null) {
            return false;
        }
        return pattern.getValue().length() == 4 &&
                pattern.getValue().charAt(0) == Consts.BRACKET_LEFT &&
                pattern.getValue().charAt(2) == Consts.BRACKET_RIGHT &&
                pattern.getValue().charAt(3) == Consts.REPEAT;
    }

    public static void clone(Node src, Node dist) {
        dist.setLeft(src.getLeft());
        dist.setRight(src.getRight());
        dist.setValue(src.getValue());
        src.setRight((Node) null);
        src.setLeft((Node) null);
    }

    public static Node getNodeByValue(char operator) {
        if (Util.isConcat(operator)) {
            return new OperationNode.Concat();
        } else if (Util.isOr(operator)) {
            return new OperationNode.Or();
        } else if (Util.isRepeat(operator)) {
            return new OperationNode.Repeat();
        }
        return null;
    }

    public static boolean isMovingPossible(Node node) {
        return node != null && node.getValue().length() > 1 && !node.getValue().equals(Consts.REPEAT_OPERATION);
    }
}
