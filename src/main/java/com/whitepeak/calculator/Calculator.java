package com.whitepeak.calculator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 *
 * @author Stefano Galli
 */
public class Calculator {

    
    public static void main(String[] args) throws IOException {
        final int ESC = 27;
        final int BACKSPACE = 8;
        String input = "";
        int ascii = -1;
        BigDecimal total = BigDecimal.valueOf(0.0);
        while (ascii!=ESC) {
            ascii = RawConsoleInput.read(true);
            switch (ascii) {
                case BACKSPACE: 
                    input = input.substring(0, input.length()-1);
                    break;
                case ESC:
                    break;
                default:
                    if((ascii>=40&&ascii<=43)||(ascii>=45&&ascii<=57)||(ascii==94)) { 
                    input = input+Character.toString((char)ascii);
                    total = Calculator.Calculate(input);
                    }
                    break;
            }   
            RawConsoleInput.cls();
            System.out.println("INPUT: "+input);
            System.out.println("OUTPUT: "+total.toString());
        }
        //Calculator.Test();
    }
    
    public static void Test(){
        String input = "-8.1-9.0-(5.7*2-(3.8^(-1.6)-2))/1.9*3.4";
        Transformation Trans = new Transformation(input);
        String postFix = Trans.toPostfix();
        String delims = "[ ]+";
        String[] tokens = postFix.split(delims);
        CalculationStack stack = new CalculationStack(tokens.length);
        
        for (int i = 0; i<tokens.length; i++){
            try{
                BigDecimal value = new BigDecimal(tokens[i]);
                stack.push(value);
            }catch(NumberFormatException e){
                BigDecimal b = stack.pop();
                BigDecimal a = stack.pop();
                switch(tokens[i]){
                    case "+":
                        stack.push((a.add(b)).setScale(9, RoundingMode.HALF_UP));
                        break;

                    case "-":
                        stack.push((a.subtract(b)).setScale(9, RoundingMode.HALF_UP));
                        break;

                    case "*":
                        stack.push((a.multiply(b)).setScale(9, RoundingMode.HALF_UP));
                        break;

                    case "/":
                        try{
                            stack.push((a.divide(b,MathContext.DECIMAL128)).setScale(9, RoundingMode.HALF_UP));
                        }catch(ArithmeticException ArExc){

                        }
                        break;

                    case "^":
                        stack.push(BigDecimal.valueOf(Math.pow(a.doubleValue(),(b.doubleValue()))));
                        break;
                }
            }
        }
        //Stampo espressione e risultato
        System.out.println("INPUT: "+input);
        System.out.println("OUTPUT: "+stack.pop());
    }
    
    public static BigDecimal Calculate (String s){
        Transformation Trans = new Transformation(s);
        String postFix = Trans.toPostfix();
        String delims = "[ ]+";
        String[] tokens = postFix.split(delims);
        CalculationStack stack = new CalculationStack(tokens.length);
        
        for (int i = 0; i<tokens.length; i++){
            try{
                BigDecimal value = new BigDecimal(tokens[i]);
                stack.push(value);
            }catch(NumberFormatException e){
                BigDecimal b = stack.pop();
                BigDecimal a = stack.pop();
                switch(tokens[i]){
                    case "+":
                        stack.push((a.add(b)).setScale(9, RoundingMode.HALF_UP));
                        break;

                    case "-":
                        stack.push((a.subtract(b)).setScale(9, RoundingMode.HALF_UP));
                        break;

                    case "*":
                        stack.push((a.multiply(b)).setScale(9, RoundingMode.HALF_UP));
                        break;

                    case "/":
                        try{
                            stack.push((a.divide(b,MathContext.DECIMAL128)).setScale(9, RoundingMode.HALF_UP));
                        }catch(ArithmeticException ArExc){

                        }
                        break;

                    case "^":
                        stack.push(BigDecimal.valueOf(Math.pow(a.doubleValue(),(b.doubleValue()))));
                        break;
                }
            }
        }
        //Stampo espressione e risultato
        System.out.println("INPUT: "+s);
        BigDecimal result = stack.pop();
        System.out.println("OUTPUT: "+result);
        return result;
    }
}
