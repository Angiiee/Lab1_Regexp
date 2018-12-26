package by.bsu.mareckay.regex.entity;

import by.bsu.mareckay.util.Consts;
import by.bsu.mareckay.util.Util;

public class OperationNode {

    public static class Literal extends Node {
        public Literal(String expr) {
            super(expr);
        }
    }

    public static class Concat extends Node {
        public Concat() {
            super(Consts.CONCAT_OPERATION);
        }

        public Concat(String expr1, String expr2) {
            super(Consts.CONCAT_OPERATION);
            this.setLeft(expr1);
            this.setRight(expr2);
        }

        @Override
        public String toString() {
            return Consts.CONCAT_OPERATION + super.toString();
        }
    }

    public static class Or extends Node {
        public Or() {
            super(Consts.OR_OPERATION);
        }

        public Or(String expr1, String expr2) {
            super(Consts.OR_OPERATION);
            this.setLeft(expr1);
            this.setRight(expr2);
        }

        @Override
        public String toString() {
            return Consts.OR_OPERATION + super.toString();
        }
    }

    public static class Repeat extends Node {
        public Repeat() {
            super(Consts.REPEAT_OPERATION);
        }

        public Repeat(String expr) {
            super(Consts.REPEAT_OPERATION);
            this.setRight(expr);
        }

        @Override
        public String toString() {
            return Consts.REPEAT_OPERATION + super.toString();
        }
    }

    public static class Group extends Node {
        public Group(String expr) {
            super(expr);
        }

        @Override
        public String toString() {
            return Consts.GROUP_OPERATION + super.toString();
        }
    }

    public static boolean isOr(Node node) {
        return node.getValue().equals(Consts.OR_OPERATION);
    }

    public static boolean isConcat(Node node) {
        return node.getValue().equals(Consts.CONCAT_OPERATION);
    }

    public static boolean isRepeat(Node node) {
        return node.getValue().equals(Consts.REPEAT_OPERATION);
    }

    public static boolean isLiteral(Node node) {
        return node.getValue().length() == 1 && Util.includeCharacter(node.getValue().charAt(0));
    }
}
