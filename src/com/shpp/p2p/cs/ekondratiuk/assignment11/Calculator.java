package com.shpp.p2p.cs.ekondratiuk.assignment11;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;

/**
 * Calculates the given expression in a postfix notation
 */
public class Calculator {
    /**
     * Gets string expression's calculation result
     *
     * @param arguments       The arguments' list for the expression to calculate
     * @param postfixLexemes  The input expression's postfix representation to calculate
     * @return                Result of the expression's calculation
     */
    static String calculateExpression(List<String> arguments, List<String> postfixLexemes) {
        HashMap<String, Double> vars = Parser.getVarsMap(arguments);
        Stack<String> stack = new Stack<>();
        for (String lexeme : postfixLexemes) {
            if (Verifier.isOperator(lexeme)) {
                if (!Verifier.isUnaryOperator(lexeme)) {
                    String op2 = stack.pop();
                    double operand2 = convertOperand(op2, vars);
                    String op1 = stack.pop();
                    double operand1 = convertOperand(op1, vars);
                    stack.push(String.valueOf(getBinaryOperation(operand1, operand2, lexeme)));
                }
                else {
                    String op = stack.pop();
                    double operand = convertOperand(op, vars);
                    stack.push(String.valueOf(getUnaryOperation(operand, lexeme)));
                }
            } else {
                stack.push(lexeme);
            }
        }
        return stack.pop();
    }

    /**
     * Implements one of the reserved binary operation(+,-,*,/,^)
     *
     * @param operand1   Operand one for the operation
     * @param operand2   Operand two for the operation
     * @param operation  The passed operation to implement
     * @return           The operation result in the String format
     */
    private static double getBinaryOperation(double operand1, double operand2, String operation) {
        double result;
        switch (operation) {
            case "+" -> result = operand1 + operand2;
            case "-" -> result = operand1 - operand2;
            case "*" -> result = operand1 * operand2;
            case "/" -> {
                if (operand2 != 0) {
                    result = operand1 / operand2;
                } else {
                    throw new ArithmeticException("Incorrect operation: Zero dividing");
                }
            }
            case "^" -> result = Math.pow(operand1, operand2);

            default -> throw new IllegalStateException("Unexpected binary operator: " + operation);
        }
        return result;
    }

    /**
     * Implements one of the reserved unary operations
     *
     * @param operand    The operand for an unary operation
     * @param operation  The operation to implement
     * @return           The operation result in the Double format
     */
    private static double getUnaryOperation(double operand, String operation) {
        double result;
        if ("_".equals(operation)) {
            result = operand * -1;
        } else if ("sin".equals(operation)) {
            result = Math.sin(operand);
        } else if ("cos".equals(operation)) {
            result = Math.cos(operand);
        } else if ("tan".equals(operation)) {
            result = Math.tan(operand);
        } else if ("atan".equals(operation)) {
            result = Math.atan(operand);
        } else if ("sqrt".equals(operation)) {
            result = Math.sqrt(operand);
        } else if ("log2".equals(operation)) {
            result = Math.log(operand) / Math.log(2);
        }
        else {
            throw new IllegalStateException("Unexpected unary operator: " + operation);
        }
        return result;
    }

    /**
     * Converts the operand from String to Double or gets the var value from the map if it is a var
     *
     * @param operand The given operand in String representation
     * @param vars    Map of variables and their values
     * @return        The converted operand
     */
    private static double convertOperand(String operand, HashMap<String, Double> vars) {
        return Verifier.isVariable(operand) ? vars.get(operand) : Double.parseDouble(operand);
    }
}
