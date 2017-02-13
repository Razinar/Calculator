package com.whitepeaks.calculator.enums;


public enum Messages {
    INVALID_INPUT("Invalid Input"),
    CALCULATOR_EXCEPTION("Calculator Exception"),
    INPUT("Type an expression to begin..."),
    TOTAL("Real time calculation"),
    INPUTUI("INPUT: "),
    OUTPUTUI("OUTPUT: "),
    POWEROFNEG("Power of negative number; result would be complex");
    private String message;

    Messages(String message) {
        this.message=message;
    }

    @Override
    public String toString() {
        return message;
    }
}
