import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

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
        hbox = new HBox();
        hbox.setOpacity(getOpacity());
        if(getOpacity() < 1) {
            hbox.setBorder(Expression.RED_BORDER);
        }
        Label label = new Label("(");
        label.setFont(Font.font("Times", ExpressionEditor.FONT_SIZE));
        hbox.getChildren().add(label);
        for(Expression expression : getSubexpressions())
        {
            hbox.getChildren().add(expression.getNode());
        }
        Label label2 = new Label(")");
        label2.setFont(Font.font("Times", ExpressionEditor.FONT_SIZE));
        hbox.getChildren().add(label2);
    }
}
