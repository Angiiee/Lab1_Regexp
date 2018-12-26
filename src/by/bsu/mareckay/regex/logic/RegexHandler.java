package by.bsu.mareckay.regex.logic;

import by.bsu.mareckay.entity.Operation;
import by.bsu.mareckay.regex.entity.Node;
import by.bsu.mareckay.regex.entity.OperationNode;
import by.bsu.mareckay.util.Consts;

import java.util.*;

import static by.bsu.mareckay.entity.InputLineHandler.addNecessaryBrackets;
import static by.bsu.mareckay.entity.InputLineHandler.find;
import static by.bsu.mareckay.entity.InputLineHandler.removeBracketsAroundGroup;
import static by.bsu.mareckay.regex.logic.RegexHandlerHelper.isMovingPossible;
import static by.bsu.mareckay.util.Util.includeCharacter;

public class RegexHandler {

    private static Node pattern;
    private static int symbolCount = 0;
    private static String stringToVerify;
    private static boolean isRepeatPhase = false;
    private static List<String> repeatPhase = new ArrayList<>();
    private static Node repeatRecoveryState = null;
    private static Node orRecoveryState = null;

    public RegexHandler(String stringPattern) {
        String processedLine = addNecessaryBrackets(stringPattern);
        processedLine = processedLine.replaceAll("\\\\", "");
        pattern = new Node(processedLine + Consts.END_OF_STRING_SYMBOLS);
        compile(pattern);

    }

    public void compile(Node regexExpr) {
        List<Node> allPatterns = extractGroupsInRegexExpr(regexExpr);
        Operation operation = find(allPatterns);
        Node node = RegexHandlerHelper.getNodeByValue(operation.getOperator());
        StringBuilder leftSon = new StringBuilder();
        List<Node> leftSonArguments = allPatterns.subList(0, operation.getPosition() == 0 ? 1 : operation.getPosition());
        for (Node n : leftSonArguments) {
            leftSon.append(n.getValue());
        }
        node.setLeft(leftSon.toString());
        StringBuilder rightSon = new StringBuilder();
        List<Node> rightSonArguments = allPatterns.subList(operation.getPosition() + operation.getOffset(), allPatterns.size());
        for (Node n : rightSonArguments) {
            rightSon.append(n.getValue());
        }
        node.setRight(rightSon.toString());

        RegexHandlerHelper.replacePatternToRepeatNode(node);
        RegexHandlerHelper.clone(node, regexExpr);

        if (isMovingPossible(regexExpr.getLeft())) {
            compile(regexExpr.getLeft());
        }
        if (isMovingPossible(regexExpr.getRight())) {
            compile(regexExpr.getRight());
        }
    }

    public List<Node> extractGroupsInRegexExpr(Node regexExpr) {
        if (regexExpr == null) {
            return null;
        }

        int groupStartPosition = 0;
        String pattern = removeBracketsAroundGroup(regexExpr.getValue());
        Stack<Character> stack = new Stack<>();
        List<Node> results = new ArrayList<>();

        for (int i = 0; i < pattern.length(); i++) {
            results.add(new Node(Character.toString(pattern.charAt(i))));
        }

        for (int i = 0; i < pattern.length(); i++) {
            char symbol = pattern.charAt(i);
            if (symbol == Consts.BRACKET_LEFT) {
                stack.push(symbol);
                groupStartPosition = i;
            } else if (symbol == Consts.BRACKET_RIGHT) {
                stack.pop();
                if (groupStartPosition != i) {
                    String group = pattern.substring(groupStartPosition, i + 1);
                    results.subList(groupStartPosition, ++i).clear();
                    results.add(groupStartPosition, new OperationNode.Group(group));
                }
            }
        }

        return results;
    }

    public boolean match(String stringToVerify) {
        RegexHandler.stringToVerify = stringToVerify + Consts.END_OF_STRING_SYMBOLS;
        return process(pattern);
    }



    public Map<String, Node> getAllPossibleMoves(Node state) {

        repeatPhase = new ArrayList<>();

        Map<String, Node> nodeByValue = new HashMap<>();
        if (OperationNode.isLiteral(state)) {
            if (repeatRecoveryState != null && isRepeatPhase) {
                Node savedrp = repeatRecoveryState;
                repeatRecoveryState = null;
                nodeByValue.put(state.getValue(), savedrp);
                return nodeByValue;
            }
            if (orRecoveryState != null) {
                Node savedrp = orRecoveryState;
                orRecoveryState = null;
                nodeByValue.put(state.getValue(), savedrp);
                return nodeByValue;
            }
            return nodeByValue;
        }

        if (OperationNode.isRepeat(state.getLeft())) {
            Node stateIfGoesToRepeat = state.getLeft().getRight();
            if (includeCharacter(state.getLeft().getRight().getValue().charAt(0))) {
                nodeByValue.put(state.getLeft().getRight().getValue(), state);
            } else if (OperationNode.isOr(stateIfGoesToRepeat)) {
                repeatRecoveryState = state;
                repeatPhase.add(stateIfGoesToRepeat.getLeft().getLeft().getValue());
                repeatPhase.add(stateIfGoesToRepeat.getRight().getLeft().getValue());
                nodeByValue.put(stateIfGoesToRepeat.getLeft().getLeft().getValue(), stateIfGoesToRepeat.getLeft().getRight());
                nodeByValue.put(stateIfGoesToRepeat.getRight().getLeft().getValue(), stateIfGoesToRepeat.getRight().getRight());
            } else {
                repeatRecoveryState = state;
                repeatPhase.add(stateIfGoesToRepeat.getLeft().getValue());
                nodeByValue.put(stateIfGoesToRepeat.getLeft().getValue(), stateIfGoesToRepeat.getRight());
            }
            if (OperationNode.isConcat(state.getRight())) {
                nodeByValue.put(state.getRight().getLeft().getValue(), state.getRight().getRight());
            }
            if (OperationNode.isLiteral(state.getRight())) {
                nodeByValue.put(state.getRight().getValue(), state.getRight());
            }
        } else if (OperationNode.isOr(state.getLeft())) {
            List<Node> equivalentBranches = new ArrayList<>();
            equivalentBranches.add(state.getLeft().getLeft());
            equivalentBranches.add(state.getLeft().getRight());
            for (Node branch : equivalentBranches) {
                if (includeCharacter(branch.getValue().charAt(0))) {
                    nodeByValue.put(branch.getValue(), state.getRight());
                } else {
                    orRecoveryState = state.getRight();
                    nodeByValue.put(branch.getLeft().getValue(), branch.getRight());
                }
            }
        } else if (state.getLeft() != null && includeCharacter(state.getLeft().getValue().charAt(0))) {
            nodeByValue.put(state.getLeft().getValue(), state.getRight());
            return nodeByValue;
        }

        return nodeByValue;
    }

    private boolean process(Node state) {
        Map<String, Node> possibleNextMoves = getAllPossibleMoves(state);
        char symbolToVerify = getNextSymbol();
        Node currentState = possibleNextMoves.get(Character.toString(symbolToVerify));

        if (!repeatPhase.isEmpty()) {
            isRepeatPhase = repeatPhase.contains(symbolToVerify);        }

        if (currentState != null && currentState.getValue().charAt(0) == Consts.END_OF_STRING_SYMBOLS ||
                (symbolToVerify == Consts.END_OF_STRING_SYMBOLS && possibleNextMoves.size() == 0)) {
            return true;
        } else
            return currentState != null && process(currentState);
    }

    private char getNextSymbol() {
        return stringToVerify.charAt(symbolCount++);
    }
}