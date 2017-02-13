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

    private static void UI(PrintWriter out, String input, String output) throws IOException {
        out.println(Ansi.ansi().cursor(0, 0));
        out.println(Ansi.ansi().eraseScreen());
        out.println(Ansi.ansi().fgRed().bold().a(INPUTUI.toString()).fgDefault().boldOff().a(input));
        out.println(Ansi.ansi().a(""));
        out.println(Ansi.ansi().eraseLine().fgGreen().bold().a(OUTPUTUI.toString()).boldOff().fgDefault().a(output));
        out.flush();
    }

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
