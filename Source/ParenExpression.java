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

    void createHBox() {
        hbox.getChildren().add(new Label("("));
        for(Expression expression : getExpressions())
        {
            hbox.getChildren().add(expression.getNode());
        }
        hbox.getChildren().add(new Label(")"));
    }
}
