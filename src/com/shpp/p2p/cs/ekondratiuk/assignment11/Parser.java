package com.shpp.p2p.cs.ekondratiuk.assignment11;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contains the auxiliary methods for parsing and checking passed expressions, lexemes, etc.
 */
public class Parser {

    /**
     * Syntactically prepares the input expression
     */
    static String prepareExpression(String expression) {
        expression = trimSpaces(expression);
        Verifier.checkExpressionSyntax(expression);
        return expression;
    }

    /**
     * Removes spaces from the input expression
     *
     * @param s  String of the expression
     * @return   Trimmed expression
     */
    static String trimSpaces(String s) {
        return s.replaceAll("\\s+","");
    }

    /**
     * Gets variable's name from the input argument string
     *
     * @param s Argument string in the following format: name=value
     * @return  Extracted variable name
     */
    static String getVarNameFromArgument(String s) {
        Pattern catchVar = Pattern.compile("([a-zA-Z]+)=-*[0-9.]+");
        Matcher matcher = catchVar.matcher(s);
        if (matcher.find()) {
            return matcher.group(1);
        } else throw new RuntimeException("Unknown error");
    }

    /**
     * Gets variable's value from the input argument string
     *
     * @param s Argument string in the following format: name=value
     * @return  Extracted variable value
     */
    private static String getVarValueFromArgument(String s) {
        Pattern catchVar = Pattern.compile("[a-zA-Z]+=(-*[0-9.]+)");
        Matcher matcher = catchVar.matcher(s);
        if (matcher.find()) {
            return matcher.group(1);
        } else throw new RuntimeException("Unknown error");
    }

    /**
     * Gets map of passed variables to calculate an expression
     * @param arguments List of passed arguments that contains variables names and its values
     * @return          Map of variables where keys are names in string format and
     *                  values are constants in double format.
     */
    static HashMap<String, Double> getVarsMap(List<String> arguments) {
        HashMap<String, Double> map = new HashMap<>();
        for (String var : arguments) {
            map.put(getVarNameFromArgument(var), Double.parseDouble(getVarValueFromArgument(var)));
        }
        return map;
    }

    /**
     * Parses input expression in lexemes due to the noticed rules
     *
     * @param line Input expression
     * @return     List of lexemes of the given expression
     */
    static List<String> getLexemesList(String line) {
        List<String> lexemes = new ArrayList<>();
        Pattern pattern = Pattern.compile(
                "(sin|cos|atan|sqrt|log2|log10)|([a-zA-Z]+)|([0-9.]+)|([^0-9a-z])"
        );
        Matcher matcher = pattern.matcher(line);
        int positionInList = -1;
        while (matcher.find()) {
            positionInList++;
            if (matcher.group().equals("-") && Verifier.isUnaryMinus(positionInList, lexemes)) {
                lexemes.add("_");
            } else {
                lexemes.add(matcher.group());
            }
        }
        return lexemes;
    }

    /**
     * Gets variables list from the given expression
     *
     * @param lexemes List of the expression's lexemes
     * @return        List of vars that contains the expression
     */
    static List<String> getVarsFromExpression(List<String> lexemes) {
        List<String> list = new ArrayList<>();
        for (String lexeme : lexemes) {
            if (Verifier.isVariable(lexeme) && !list.contains(lexeme)) {
                list.add(lexeme);
            }
        }
        return list;
    }

    /**
     * Gets postfix string representation of the input expression
     *
     * @param lexemesList List of the expression's lexemes
     * @return            String of expression that represents it in the postfix form
     */
    static List<String> getPostfixNotation(List<String> lexemesList) {
        Stack<String> stack = new Stack<>();
        List<String> output = new ArrayList<>();
        String current;
        for (String s : lexemesList) {
            current = s;
            if (Verifier.isOperator(current)) {
                /* Braces processing */
                if (current.equals("(") ||
                        !stack.empty() && stack.peek().equals("(") && !current.equals(")")) {
                    stack.push(current);
                    continue;
                }
                if (current.equals(")")) {
                    /* Pop all the operators in the stack till the open brace to output  */
                    while (!stack.peek().equals("(")) {
                        output.add(stack.pop());
                    }
                    /* Take off the open brace from the stack */
                    stack.pop();
                    continue;
                }
                /* End of braces processing */

                /* Operators priority processing */
                while (!stack.empty()) {
                    if (getPriority(current) <= getPriority(stack.peek())) {
                        output.add(stack.pop());
                    } else {
                        break;
                    }
                }
                stack.push(current);

            } else {
                output.add(current);
                /* Pop the current item to output if it is an unary operator */
                if (!stack.empty() &&
                        Verifier.isOperator(stack.peek()) && getPriority(stack.peek()) > 4) {
                    output.add(stack.pop());
                }
            }
        }
        /* Get all operators from stack while it is not empty*/
        while (stack.size() > 0) {
            output.add(stack.pop());
        }
        return output;
    }

    /**
     * Gets the operators priority range
     * The biggest number is the biggest priority
     *
     * @param string The passed operation
     * @return       Range number
     */
    private static int getPriority(String string) {
        return switch (string) {
            case "+", "-" -> 1;
            case "*", "/" -> 2;
            case "^" -> 3;
            case "cos", "sin", "tan", "atan", "log2", "log10", "sqrt" -> 4;
            case "_" -> 5;
            default -> 0;
        };
    }
}
