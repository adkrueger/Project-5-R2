import java.util.ArrayList;
import java.util.List;

public abstract class CompoundExpressionImpl extends SimpleExpressionImpl implements CompoundExpression {

    private List<Expression> _expressions = new ArrayList<>();

    /**
     * The constructor for the CompoundExpressionImpl
     *
     * @param contents the given contents of the CompoundExpressionImpl
     */
    CompoundExpressionImpl(String contents) {
        super(contents);
    }

    /**
     * Returns the list of expressions
     *
     * @return the list of expressions
     */
    List<Expression> getExpressions() {
        return _expressions;
    }

    /**
     * Adds the specified expression as a child.
     *
     * @param subexpression the child expression to add
     */
    public void addSubexpression(Expression subexpression) {
        _expressions.add(subexpression);
    }

    /**
     * Returns the CompoundExpressionImpl's following expressions
     *
     * @return the list of expressions
     */
    private List<Expression> getSubexpressions() {
        return _expressions;
    }

    /**
     * Creates and returns a deep copy of the expression.
     * The entire tree rooted at the target node is copied, i.e.,
     * the copied Expression is as deep as possible.
     *
     * @return the deep copy
     */
    public Expression deepCopy() {
        return null;
    }

    /**
     * Recursively flattens the expression as much as possible
     * throughout the entire tree. Specifically, in every multiplicative
     * or additive expression x whose first or last
     * child c is of the same type as x, the children of c will be added to x, and
     * c itself will be removed. This method modifies the expression itself.
     */
    public void flatten() {
        List<Expression> expressions = new ArrayList<>();
        for (Expression expression : _expressions) {
            expression.flatten();
            if (isMultOrAdd(expression)) {
                expressions.addAll(((CompoundExpressionImpl) expression).getSubexpressions());
            } else {
                expressions.add(expression);
            }
        }
        _expressions = expressions;
    }

    /**
     * Checks whether a expression is * or + and matches the
     * current Expression contents
     *
     * @param expression the given expression to be checked
     * @return true if matches the contents and + or *,
     * false if not
     */
    private boolean isMultOrAdd(Expression expression) {
        return expression.getContents().equals(this.getContents()) &&
                (expression instanceof AdditionExpression || expression instanceof MultiplicationExpression);
    }

    /**
     * Creates a String representation by recursively printing out (using indentation) the
     * tree represented by this expression, starting at the specified indentation level.
     *
     * @param stringBuilder the StringBuilder to use for building the String representation
     * @param indentLevel   the indentation level (number of tabs from the left margin) at which to start
     */
    public void convertToString(StringBuilder stringBuilder, int indentLevel) {
        indent(stringBuilder, indentLevel);
        stringBuilder.append(getContents());
        stringBuilder.append("\n");
        int indentMod;               // modifies the number of indents. either 1 or 0
        int childCount = 0;          // represents the "index" number of the child in relation to the parent
        for (Expression n : _expressions) {
            childCount++;            // increases to represent the index of the child

            if (childCount == 1)     // if this child is the newest of a set, then increase indent by 1
                indentMod = 1;
            else                     // otherwise the indent level remains the same
                indentMod = 0;

            n.convertToString(stringBuilder, indentLevel += indentMod);   // recursive call to keep going down the list
        }
    }

    /**
     * Static helper method to indent a specified number of times from the left margin, by
     * appending tab characters to the specified StringBuilder.
     *
     * @param stringBuilder the StringBuilder to which to append tab characters.
     * @param indentLevel   the number of tabs to append.
     */
    public void indent(StringBuilder stringBuilder, int indentLevel) {
        for (int i = 0; i < indentLevel; i++) {
            stringBuilder.append('\t');
        }
    }
}
