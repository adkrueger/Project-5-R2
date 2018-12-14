class AdditionExpression extends CompoundExpressionImpl {

    /**
     * The constructor for the CompoundExpressionImpl
     *
     * @param contents the given contents of the CompoundExpressionImpl
     */
    AdditionExpression(String contents) {
        super(contents);
    }

    /**
     * Helper method that returns new Object of this type
     *
     * @return new Object of type AdditionExpression
     */
    CompoundExpression deepCopyHelper() {
        return new AdditionExpression(getContents());
    }

    /**
     * Method to create the HBox
     */
    public void createHBox() {
        easyMake();
    }
}
