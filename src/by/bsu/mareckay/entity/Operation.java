package by.bsu.mareckay.entity;

public class Operation {
    private char operator;
    private int position;
    private int offset;

    public Operation() {
    }

    public Operation(char operator, int position, int offset) {
        this.operator = operator;
        this.position = position;
        this.offset = offset;
    }

    public char getOperator() {
        return operator;
    }

    public void setOperator(char operator) {
        this.operator = operator;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public String toString() {
        return "Operation{" +
                "operator='" + operator + '\'' +
                ", position=" + position +
                ", offset=" + offset +
                '}';
    }
}
