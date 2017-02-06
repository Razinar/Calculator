package com.whitepeak.calculator;

import java.math.BigDecimal;

/**
 *
 * @author Stefano Galli
 */
public class CalculationStack {
        private int maxSize;
        private BigDecimal[] stack;
        private int top;

        public CalculationStack(int max) {
            maxSize = max;
            stack = new BigDecimal[maxSize];
            top = -1;
        }

        public BigDecimal peek() {
            return stack[top];
        }

        public void push(BigDecimal x) {
            stack[++top] = x;
        }

        public BigDecimal pop() {
            return stack[top--];
        }

        public boolean isEmpty() {
            return (top == -1);
        }
    }
