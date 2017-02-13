package com.whitepeaks.calculator.enums;

public enum Operators {
    PLUS('+'),MINUS('-'),TIMES('*'),DIVIDE('/'),EXP('^'),LPAR('('),RPAR(')'),DECIMAL('.'),WS(' '),EMPTY(Character.MIN_VALUE),FAIL('!');

    private final char symbol;

    Operators(char symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol == Character.MIN_VALUE ? new String("") : new String (""+symbol);
    }

    public char toChar() {
        return symbol;
    }

    public static Operators get(char symbol) {
        switch(symbol) {
            case  '+': return PLUS;
            case  '-': return MINUS;
            case '*': return TIMES;
            case  '/': return DIVIDE;
            case '^': return EXP;
            case '(': return LPAR;
            case ')': return RPAR;
            case '.': return DECIMAL;
            case ' ': return WS;
            case Character.MIN_VALUE: return EMPTY;
        }
        return FAIL;
    }

}
