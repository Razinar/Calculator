package com.whitepeak.tests;

import com.whitepeak.calculator.Calculator;
import org.junit.Assert;
import org.junit.Test;

public class TestUnits {

    @Test
    public void testCalculation1() throws Exception{
        String input = "-8.1-9.0-(5.7*2-(3.8^(-1.6)-2))/1.9*3.4";
        Assert.assertEquals(Calculator.Calculate(input).doubleValue(), -40.867563636,0);
    }

    @Test
    public void testAddition() throws Exception{
        String input = "4+4";
        Assert.assertEquals(Calculator.Calculate(input).doubleValue(), 8,0);
    }

    @Test
    public void testDifference() throws Exception{
        String input = "8.25-4.06";
        Assert.assertEquals(Calculator.Calculate(input).doubleValue(), 4.19,0);
    }

    @Test
    public void testMultiplication() throws Exception{
        String input = "4.4*4.4";
        Assert.assertEquals(Calculator.Calculate(input).doubleValue(), 19.36,0);
    }

    @Test(expected=ArithmeticException.class)
    public void testDivision() throws Exception{
        String input = "4/0";
        Calculator.Calculate(input);
    }

    @Test
    public void testParenthesis() throws Exception{
        String input = "(5-6)*-6";
        Assert.assertEquals(Calculator.Calculate(input).doubleValue(), 6,0);
    }

    @Test
    public void testExponential() throws Exception{
        String input = "3^3";
        Assert.assertEquals(Calculator.Calculate(input).doubleValue(),27,0);
    }


}