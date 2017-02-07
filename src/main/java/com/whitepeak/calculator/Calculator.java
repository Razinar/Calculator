package com.whitepeak.calculator;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;

/**
 *
 * @author Stefano Galli
 */
public class Calculator {

    
    public static void main(String[] args) throws IOException {
        AnsiConsole.systemInstall();
        final int ESC = 27;
        final int BACKSPACE = 8;
        String input = "Type an expression to begin...";
        int ascii = -1;
        String total = "Real time calculation";
        UI(input,total);
        input="";
        total="";
        while (ascii!=ESC) {
            ascii = RawConsoleInput.read(true);
            switch (ascii) {
                case BACKSPACE: 
                    if(input.length()>0) {
                        input = input.substring(0, input.length()-1);
                        total = calculateString(input);
                    }
                    break;
                case ESC:
                    break;
                default:
                    //If it is ()*+ or -./d or exp then accept the character
                    //otherwise don't consider the last input
                    if((ascii>=40&&ascii<=43)||(ascii>=45&&ascii<=57)||(ascii==94)) { 
                        input = input+Character.toString((char)ascii);
                        total = calculateString(input);
                    }
                    break;
            }   
            UI(input,total);
        }
        AnsiConsole.systemUninstall();
        //Calculator.Test();
    }
    
    private static void UI(String input, String output) {
        System.out.println(ansi().eraseScreen(Ansi.Erase.ALL).fg(RED).a("INPUT : ").fg(WHITE).a(input).reset());
        System.out.println();
        System.out.println(ansi().fg(GREEN).a("OUTPUT: ").fg(WHITE).a(output).reset());
    }
    
    private static String calculateString(String input) {
        try {
            return String.valueOf(Calculator.Calculate(input));
        } catch (ArrayIndexOutOfBoundsException exc) {
            return "Invalid Input...";
        } catch (ArithmeticException arExc) {
            return arExc.getMessage();
        }
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
    }
    
    public static BigDecimal Calculate (String s) throws ArithmeticException{
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
                           throw ArExc;
                        }
                        break;

                    case "^":
                        stack.push(BigDecimal.valueOf(Math.pow(a.doubleValue(),(b.doubleValue()))));
                        break;
                }
            }
        }
        BigDecimal result = stack.pop();
        return result;
    }
}
