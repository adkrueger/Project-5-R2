import java.util.function.Function;

/**
 * Starter code to implement an ExpressionParser. Your parser methods should use the following grammar:
 * E := A | X
 * A := A+M | M
 * M := M*M | X
 * X := (E) | L
 * L := [0-9]+ | [a-z]
 */
public class SimpleExpressionParser implements ExpressionParser {
    /**
     * Attempts to create an expression tree -- flattened as much as possible -- from the specified String.
     * Throws a ExpressionParseException if the specified string cannot be parsed.
     *
     * @param str                the string to parse into an expression tree
     * @param withJavaFXControls you can just ignore this variable for R1
     * @return the Expression object representing the parsed expression tree
     */
    public Expression parse(String str, boolean withJavaFXControls) throws ExpressionParseException {
        // Remove spaces -- this simplifies the parsing logic
        str = str.replaceAll(" ", "");
        Expression expression = parseE(str);
        if (expression == null) {
            // If we couldn't parse the string, then raise an error
            throw new ExpressionParseException("Cannot parse expression: " + str);
        }

        // Flatten the expression before returning
        expression.flatten();
        return expression;
    }

    /**
     * Checks if the input, along with two lambda expressions, matches the
     * given operator
     *
     * @param str the input that is being tested
     * @param op  the operator that is between the two lambda functions
     * @param m1  the first lambda function
     * @param m2  the second lambda function
     * @return the expression corresponding with the operator op, otherwise
     * null if it doesn't match
     */
    private static Expression parseHelper(String str, char op, Function<String, Expression> m1,
                                          Function<String, Expression> m2) {
        for (int i = 1; i < str.length() - 1; i++) {
            Expression m1Exp = m1.apply(str.substring(0, i));
            Expression m2Exp = m2.apply(str.substring(i + 1));
            if (str.charAt(i) == op && m1Exp != null && m2Exp != null) {
                CompoundExpressionImpl opExpr = null;
                if (op == '+') {
                    opExpr = new AdditionExpression("+");
                }
                if (op == '*') {
                    opExpr = new MultiplicationExpression("*");
                }
                if (opExpr != null) {
                    opExpr.addSubexpression(m1Exp);
                    opExpr.addSubexpression(m2Exp);
                    return opExpr;
                }
            }
        }
        return null;
    }

    /**
     * Checks whether or not the string matches A or X
     *
     * @param str the given string
     * @return Expression A if the string matches A, Expression X if the
     * string matches X, or null if it matches neither
     */
    private static Expression parseE(String str) {
        return expHelper(str, parseA(str), SimpleExpressionParser::parseX);
    }

    private static Expression expHelper(String str, Expression exp, Function<String, Expression> fn)
    {
        if(exp != null) {
            return exp;
        }
        exp = fn.apply(str);
        return exp; // Doesn't matter if it's null or not, it's all we have
    }

    /**
     * Checks whether or not the string matches M*M or X
     *
     * @param str the given string
     * @return Expression exp if the string matches M*M, Expression X if the
     * string matches X, or null if it matches neither
     */
    private static Expression parseM(String str) {
        // M * M or X
        Expression exp = parseHelper(str, '*', SimpleExpressionParser::parseM, SimpleExpressionParser::parseM);
        return expHelper(str, exp, SimpleExpressionParser::parseX);
    }

    /**
     * Checks whether or not the string matches A+M or M
     *
     * @param str the given string
     * @return Expression exp if the string matches A+M, Expression M if the
     * string matches M, or null if it matches neither
     */
    private static Expression parseA(String str) {
        // A + M or M
        Expression exp = parseHelper(str, '+', SimpleExpressionParser::parseA, SimpleExpressionParser::parseM);
        return expHelper(str, exp, SimpleExpressionParser::parseM);
    }

    /**
     * Checks whether or not the string matches (E) or L
     *
     * @param str the given string
     * @return Expression opExpr if the string matches (E), Expression L if the
     * string matches L, or null if it matches neither
     */
    private static Expression parseX(String str) {
        // (E) or L
        if (str.length() >= 2 && str.charAt(0) == '(' && str.charAt(str.length() - 1)
                == ')') {
            Expression middleE = parseE(str.substring(1, str.length() - 1));
            if (middleE != null) {
                CompoundExpressionImpl opExpr = new ParenExpression("()");
                opExpr.addSubexpression(middleE);
                return opExpr;
            }
        }
        return expHelper(str, null, SimpleExpressionParser::parseL);
    }

    /**
     * Checks whether or not the string is a SimpleExpression using the
     * letters a-z and any number (i.e. a number or a variable)
     *
     * @param str the given string
     * @return a SimpleExpressionImpl representing the variable/number
     * or null if it doesn't match either
     */
    private static Expression parseL(String str) {
        if (str.matches("[a-z]|[0-9]+")) {
            return new SimpleExpressionImpl(str);
        }
        return null;
    }
}
