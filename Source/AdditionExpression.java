public class AdditionExpression extends CompoundExpressionImpl {

    /**
     * The constructor for the CompoundExpressionImpl
     *
     * @param contents the given contents of the CompoundExpressionImpl
     */
    AdditionExpression(String contents) {
        super(contents);
    }

    /**
     * Creates and returns a deep copy of the expression.
     * The entire tree rooted at the target node is copied, i.e.,
     * the copied Expression is as deep as possible.
     *
     * @return the deep copy
     */
    public Expression deepCopy() {
        CompoundExpressionImpl expression = new AdditionExpression(getContents());
        for (Expression e : getExpressions()) {
            expression.addSubexpression(e.deepCopy());
        }
        return expression;
    }
}
