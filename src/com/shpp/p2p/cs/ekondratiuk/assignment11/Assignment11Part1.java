package com.shpp.p2p.cs.ekondratiuk.assignment11;

import java.util.*;

/**
 * Evaluates arithmetical expressions in the string format(with spaces or without them)
 * The expression may have variables consist of english letters in lower and upper case
 * The expression may have constants of integer or double(num.num) numbers
 * The evaluator supports the format with braces and without them as well
 * The evaluator supports the following operations:
 * "+","-","*","/","^","sin","cos","tan","atan","sqrt","log2","log10"
 * The expression cannot have any operators at the end of the string
 * The expression cannot have more than one of the operators in succession, exclude: "*-","/-","^-"
 */
public class Assignment11Part1 {
    /**
     * Gets an expression(and its arguments if they are needed) and calculates it.
     * After launch of the program it parses the passed expression and its arguments
     * through IDEA parameters or the command line.
     * The program can calculate the same already parsed expression using new arguments
     * passed through the console.
     *
     * @param args Input arguments' list of the program
     */
    public static void main(String[] args) {
        final Scanner scan = new Scanner(System.in);
        List<String> arguments = new ArrayList<>();

        try {
            /* Check if the program has any parameters */
            Verifier.isArgsArrayEmpty(args);

            /* Prepare the expression to parse in the convenient representation */
            String expression = Parser.prepareExpression(args[0]);

            List<String> lexemes = Parser.getLexemesList(expression);
            Verifier.checkBracesSyntax(lexemes);
            List<String> varsInExpression = Parser.getVarsFromExpression(lexemes);
            List<String> postfixNotation = Parser.getPostfixNotation(lexemes);

            /* Primary arguments from command line processing */
            for (int i = 1; i < args.length; i++) {
                String argument = Parser.trimSpaces(args[i]);
                if (Verifier.isArgumentNameCorrect(argument, varsInExpression)) {
                    arguments.add(argument);
                }
            }
            /* Try to evaluate the expression with primary arguments */
            if (Verifier.expVarsNumberEqualsArgsNumber(varsInExpression, arguments)) {
                String calculateResult = Calculator.calculateExpression(arguments, postfixNotation);
                printResults(expression, arguments, calculateResult);
                arguments.clear();
            } else {
                System.out.println("Input expression: " + expression +
                        "\nSomething wrong with arguments.");
            }

            /////////////////////////////////////////////////////////////////////////////////////////////
            /* Evaluate already parsed expression for cyclically gotten args from the console if they are */
            while (varsInExpression.size() > 0) {
                System.out.println("\nInitialize the variables with new values " +
                        "\n* varValue may be any num of integer or double(number.number) type");
                for (String var : varsInExpression) {
                    while (true) {
                        /* Check every argument from input till it is allowed and syntactically correct */
                        System.out.print("argument " + var + ": ");
                        String argument = scan.nextLine();
                        if (argument.equals("")) {
                            System.out.println("Argument is not passed. Try again");
                            continue;
                        }
                        argument = Parser.trimSpaces(argument);
                        if (Verifier.isArgumentValueCorrect(argument)) {
                            arguments.add(var + "=" + argument);
                            break;
                        } else {
                            System.out.println("Something wrong with passed argument's value.");
                        }
                    }
                }
                /* Try to evaluate the expression with arguments from the console */
                String calculateResult = Calculator.calculateExpression(arguments, postfixNotation);
                printResults(expression, arguments, calculateResult);
                arguments.clear();
            }
            ////////////////////////////////////////////////////////////////////////////////////////

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /** Prints the input expression and passed arguments if they are and the calculation's result */
    private static void printResults(String expression, List<String> arguments, String result) {
        System.out.println("Input expression: " + expression);
        if (arguments.size() > 0) {
            System.out.print("Arguments: ");
            for (String argument : arguments) {
                System.out.print(argument + " ");
            }
            System.out.println();
        }
        System.out.println("Calculation result: " + result);
    }
}