class MultiplicationExpression extends CompoundExpressionImpl {
    /**
     * The constructor for the CompoundExpressionImpl
     *
     * @param contents the given contents of the CompoundExpressionImpl
     */
    MultiplicationExpression(String contents) {
        super(contents);
    }

    /**
     * Helper method that returns new Object of this type
     *
     * @return new Object of type MultiplicationExpression
     */
    CompoundExpression deepCopyHelper() {
        return new MultiplicationExpression(getContents());
    }

    /**
     * Method to create the HBox
     */
    public void createHBox() {
        easyMake();
    }
}
