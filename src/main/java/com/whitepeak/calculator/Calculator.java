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
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String input = "";
        String prevIn = "";
        String prevOut = "";
        String resp = "";
        String errorMess = "Invalid input...";
        BigDecimal total = BigDecimal.valueOf(0.0);
        //finchè l'utente non scrive "quit", quando l'input subisce una variazione viene effettuato un nuovo calcolo
        while (!input.equalsIgnoreCase("quit")) {
            input = in.readLine();
            if(!input.equalsIgnoreCase(prevIn)){
                prevIn = input;
                try{
                    total = Calculator.Calculate(input);
                    //cancello tutta la riga e riporto il token a inizio riga per scrivere, se serve
                    if(total.toString().length() < prevOut.length()){
                        resp = "";
                        while(resp.length() != total.toString().length()){
                            resp = resp + "\b";
                        }
                        System.out.println(resp + "\r");
                    }
                    prevOut = total.toString();
                    System.out.println(total.toString() + "\r");
                }catch(ArrayIndexOutOfBoundsException exc){
                    if(errorMess.length() < prevOut.length()){
                        resp = "";
                        while(resp.length() != errorMess.length()){
                            resp = resp + "\b";
                        }
                        System.out.println(resp + "\r");
                    }
                    prevOut = errorMess;
                    System.out.println(errorMess + "\r");
                }
            }
        }
        
        in.close();
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
