import java.util.ArrayList;

interface CompoundExpression extends Expression {
    /**
     * Adds the specified expression as a child.
     *
     * @param subexpression the child expression to add
     */
    void addSubexpression(Expression subexpression);

    /**
     * Returns the list of expressions
     *
     * @return the list of expressions
     */
    ArrayList<Expression> getSubexpressions();

    /**
     * Creates a copy of the Expression by instantiating a new
     * Expression
     *
     * @return a newly instantiated copy of the Expression
     */
    CompoundExpression deepCopy();

}
