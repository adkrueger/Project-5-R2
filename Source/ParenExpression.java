import javafx.scene.control.Label;

class ParenExpression extends CompoundExpressionImpl {
    /**
     * The constructor for the CompoundExpressionImpl
     *
     * @param contents the given contents of the CompoundExpressionImpl
     */
    ParenExpression(String contents) {
        super(contents);
    }

    CompoundExpression deepCopyHelper() {
        return new ParenExpression(getContents());
    }

    public void createHBox() {
        hbox.getChildren().add(new Label("("));
        for(Expression expression : getSubexpressions())
        {
            hbox.getChildren().add(expression.getNode());
        }
        hbox.getChildren().add(new Label(")"));
    }
}
