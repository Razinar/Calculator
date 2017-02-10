package com.whitepeak.calculator.enums;


public enum Messages {
    INVALID_INPUT("Invalid Input"),CALCULATOR_EXCEPTION("Calculator Exception"),INPUT("Type an expression to begin..."),TOTAL("Real time calculation"),INPUTUI("INPUT: "),OUTPUTUI("OUTPUT: ");
    private String message;

    Messages(String message) {
        this.message=message;
    }

    @Override
    public String toString() {
        return message;
    }
}
