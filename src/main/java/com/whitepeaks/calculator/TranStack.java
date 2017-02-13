package com.whitepeaks.calculator;

/**
 *
 * @author Stefano Galli
 */
public class TranStack {
    private int maxSize;
    private char[] stack;
    private int top;

    public TranStack(int max) {
        maxSize = max;
        stack = new char[maxSize];
        top = -1;
    }
    
    public char peek() {
        return stack[top];
    }
    
    public void push(char x) {
        stack[++top] = x;
    }
    
    public char pop() {
        return stack[top--];
    }
    
    public boolean isEmpty() {
        return (top == -1);
    }
}
