class AdditionExpression extends CompoundExpressionImpl {

    /**
     * The constructor for the CompoundExpressionImpl
     *
     * @param contents the given contents of the CompoundExpressionImpl
     */
    AdditionExpression(String contents) {
        super(contents);
    }

    void createHBox() {
        hBoxHelper(getContents());
    }
}
