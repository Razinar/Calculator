package com.whitepeak.calculator;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import jline.AnsiWindowsTerminal;
import jline.console.ConsoleReader;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.fusesource.jansi.AnsiRenderer;

/**
 *
 * @author Stefano Galli
 */
public class Calculator {

    
    public static void main(String[] args) throws IOException,Exception {
        AnsiConsole.systemInstall();
        ConsoleReader reader = new ConsoleReader(System.in, System.out);
        System.out.println(Ansi.ansi().eraseScreen());
        PrintWriter out = new PrintWriter(reader.getOutput());
        final int ESC = 27;
        final int BACKSPACE = 8;
        String input = "Type an expression to begin...";
        int ascii = -1;
        String total = "Real time calculation";
        UI(out,input,total);
        input="";
        total="";
        while (ascii!=ESC) {
            ascii = reader.readCharacter();
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
            UI(out,input,total);
        }
        System.out.println(Ansi.ansi().eraseScreen());
        AnsiConsole.systemUninstall();
        //Calculator.Test();
    }
    
    private static void UI(PrintWriter out,String input, String output) throws IOException{
        out.println(Ansi.ansi().cursor(0,0));
        out.println(Ansi.ansi().bgBright(Ansi.Color.RED));
        out.println(Ansi.ansi().eraseLine().fgRed().bold().a("INPUT : ").fgDefault().boldOff().a(input).saveCursorPosition());
        out.println(Ansi.ansi().eraseLine().a(""));
        out.println(Ansi.ansi().eraseLine().fgGreen().bold().a("OUTPUT: ").boldOff().fgDefault().a(output));
        out.println(Ansi.ansi().restoreCursorPosition());
        out.flush();
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
