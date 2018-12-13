class MultiplicationExpression extends CompoundExpressionImpl {
    /**
     * The constructor for the CompoundExpressionImpl
     *
     * @param contents the given contents of the CompoundExpressionImpl
     */
    MultiplicationExpression(String contents) {
        super(contents);
    }

    CompoundExpression deepCopyHelper() {
        return new MultiplicationExpression(getContents());
    }

    public void createHBox() {
        hBoxHelper(getContents());
    }
}
