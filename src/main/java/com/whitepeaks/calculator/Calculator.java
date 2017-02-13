package com.whitepeaks.calculator;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.logging.Level;

import java.util.logging.Logger;
import static com.whitepeaks.calculator.enums.Messages.*;
import jline.console.ConsoleReader;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import com.whitepeaks.calculator.enums.Operators;
import static com.whitepeaks.calculator.enums.Operators.*;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.OverflowException;

/**
* Calculator class grants methods to execute a calculation based on Reverse
* Polish Notation which is a Postfix calculation technic. As user input a
* properly written arithmetic expression a real time result appears on the
* screen, otherwise user is suggested to correct it.
* <p>
* Real numbers and parentheses are accepted, as well as basic operations (+, -,
* *, /, ^) and backspace to delete last keyboard input.
*
* @author Stefano Galli
*/
public class Calculator {

    public static void main(String[] args) throws IOException, Exception {
        AnsiConsole.systemInstall();
        ConsoleReader reader = new ConsoleReader(System.in, System.out);
        PrintWriter out = new PrintWriter(reader.getOutput());
        out.println(Ansi.ansi().eraseScreen());
        final int ESC = 27;
        final int BACKSPACE = 8;
        final int DELETE = 127;
        String input = INPUT.toString();
        int ascii = -1;
        String total = TOTAL.toString();
        UI(out, input, total);
        input = EMPTY.toString();
        total = EMPTY.toString();
        while (ascii != ESC) {
            ascii = reader.readCharacter();
            switch (ascii) {
                case BACKSPACE:
                case DELETE:
                    if (input.length() > 0) {
                        input = input.substring(0, input.length() - 1);
                        total = calculateString(input);
                    }
                    break;
                case ESC:
                    break;
                default:
                    //If it is ()*+ or -./d or exp then accept the character
                    //otherwise don't consider the last input
                    if ((ascii >= 40 && ascii <= 43) || (ascii >= 45 && ascii <= 57) || (ascii == 94)) {
                        input = input + Character.toString((char) ascii);
                        total = calculateString(input);
                    }
                    break;
            }
            UI(out, input, total);
        }
        AnsiConsole.systemUninstall();
    }

    /*
    * Prints the console UI interface for the command line calculator
    *
    * @param  out  printwriter to write to
    * @param  input the string that is being typed by the user
    * @param  output the string that has been calculated
    * @throws IOException
    */
    private static void UI(PrintWriter out, String input, String output) throws IOException {
        out.println(Ansi.ansi().cursor(0, 0));
        out.println(Ansi.ansi().eraseScreen());
        out.println(Ansi.ansi().fgRed().bold().a(INPUTUI.toString()).fgDefault().boldOff().a(input));
        out.println(Ansi.ansi().a(""));
        out.println(Ansi.ansi().eraseLine().fgGreen().bold().a(OUTPUTUI.toString()).boldOff().fgDefault().a(output));
        out.flush();
    }

    /*
    * Execute Calculate(String s) on user input and result is converted into String
    *
    * @param  input the string that is being typed by the user
    * @return formatted result as String
    */
    public static String calculateString(String input) {
        try {
            return String.valueOf(Calculator.Calculate(input).toPlainString());
        } catch (ArrayIndexOutOfBoundsException exc) {
            return INVALID_INPUT.toString();
        } catch (ArithmeticException arExc) {
            Logger.getGlobal().log(Level.SEVERE, arExc.getStackTrace().toString());
            if (arExc.getMessage().equalsIgnoreCase(POWEROFNEG.toString())) {
                return INVALID_INPUT.toString();
            }
            return arExc.getMessage();
        } catch (NumberFormatException | OverflowException exc) {
            return exc.getMessage();
        }
    }

    /*
    * Transform user input into Postfix notation before result is calculated.
    * Calculation is performed by parsing the formatted string, pushing every operand into a stack.
    * Once an operator appears, last 2 operands are popped out from the stack and operator applied to them.
    *
    * @param  s the string that is being typed by the user
    * @return Calculation result as BigDecimal
    * @throws ArithmeticException
    */
    public static BigDecimal Calculate(String s) throws ArithmeticException {
        Transformation Trans = new Transformation(s);
        String postFix = Trans.toPostfix();
        String delims = "[ ]+";
        String[] tokens = postFix.split(delims);
        CalculationStack stack = new CalculationStack(tokens.length);

        for (int i = 0; i < tokens.length; i++) {
            try {
                BigDecimal value = new BigDecimal(tokens[i]);
                stack.push(value);
            } catch (NumberFormatException e) {
                BigDecimal b = stack.pop();
                BigDecimal a = stack.pop();
                switch (Operators.get(tokens[i].charAt(0))) {
                    case PLUS:
                        stack.push((a.add(b)).setScale(9, RoundingMode.HALF_UP));
                        break;

                    case MINUS:
                        stack.push((a.subtract(b)).setScale(9, RoundingMode.HALF_UP));
                        break;

                    case TIMES:
                        stack.push((a.multiply(b)).setScale(9, RoundingMode.HALF_UP));
                        break;

                    case DIVIDE:
                        try {
                            stack.push((a.divide(b, MathContext.DECIMAL128)).setScale(9, RoundingMode.HALF_UP));
                        } catch (ArithmeticException ArExc) {
                            throw ArExc;
                        }
                        break;

                    case EXP:
                        try {
                            stack.push(BigDecimal.valueOf(Math.pow(a.doubleValue(),
                                    (b.doubleValue()))));
                            break;
                        } catch (NumberFormatException nfExc) {
                            Apfloat apflResult = ApfloatMath.pow(new Apfloat(
                                    new BigDecimal(a.toString()), 9), new Apfloat(
                                            new BigDecimal(b.toString()), 9));
                            Double doubleResult = Double.valueOf(apflResult
                                    .toString());
                            stack.push(BigDecimal.valueOf(doubleResult));
                            break;
                        }
                }
            }
        }
        BigDecimal result = stack.pop();
        return result;
    }
}
