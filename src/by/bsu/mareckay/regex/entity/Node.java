package by.bsu.mareckay.regex.entity;

import java.util.Objects;

public class Node {
    private String value;
    private Node left;
    private Node right;

    public Node() {
    }

    public Node(String expr) {
        this.value = expr;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public void setLeft(String left) {
        this.left = new Node(left);
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public void setRight(String right) {
        this.right = new Node(right);
    }

    @Override
    public String toString() {
        return "Node{" +
                "value='" + this.getValue() + '\'' +
                ", left=" + this.getLeft() +
                ", right=" + this.getRight() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(value, node.value) &&
                Objects.equals(left, node.left) &&
                Objects.equals(right, node.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, left, right);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
