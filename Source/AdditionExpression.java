import javafx.scene.layout.HBox;

class AdditionExpression extends CompoundExpressionImpl {

    /**
     * The constructor for the CompoundExpressionImpl
     *
     * @param contents the given contents of the CompoundExpressionImpl
     */
    AdditionExpression(String contents) {
        super(contents);
    }

    CompoundExpression deepCopyHelper() {
        return new AdditionExpression(getContents());
    }

    public void createHBox() {
        easyMake();
    }
}
