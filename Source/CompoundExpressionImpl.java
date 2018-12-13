import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;

abstract class CompoundExpressionImpl extends SimpleExpressionImpl implements CompoundExpression {

    private List<Expression> _expressions = new ArrayList<>();
    HBox hbox = new HBox();

    /**
     * The constructor for the CompoundExpressionImpl
     *
     * @param contents the given contents of the CompoundExpressionImpl
     */
    CompoundExpressionImpl(String contents) {
        super(contents);
    }

    /**
     * Adds the specified expression as a child.
     *
     * @param subexpression the child expression to add
     */
    public void addSubexpression(Expression subexpression) {
        subexpression.setParent(this);
        _expressions.add(subexpression);
    }

    /**
     * Returns the CompoundExpressionImpl's following expressions
     *
     * @return the list of expressions
     */
    public List<Expression> getSubexpressions() {
        return _expressions;
    }

    /**
     * Creates and returns a deep copy of the expression.
     * The entire tree rooted at the target node is copied, i.e.,
     * the copied Expression is as deep as possible.
     *
     * @return the deep copy
     */
    public CompoundExpression deepCopy() {
        CompoundExpression copy = deepCopyHelper();
        for(Expression subExp : _expressions) {
            copy.addSubexpression(subExp);
        }
        copy.setNode(getNode());
        return copy;
    }

    abstract CompoundExpression deepCopyHelper();

    public String expToText(){
        StringBuilder string = new StringBuilder();
        for (Node node : hbox.getChildren())
        {
            if (node instanceof Label) {
                string.append(((Label) node).getText());
            }
            else {
                string.append(nodeToText((HBox) node));
            }
        }
        return string.toString();
    }

    private String nodeToText(HBox nodes) {
        StringBuilder string = new StringBuilder();
        for (Node node : nodes.getChildren()) {
            if(node instanceof Label) {
                string.append(((Label) node).getText());
            }
            else if(node instanceof HBox){
                string.append(nodeToText((HBox) node));
            }
        }
        return string.toString();
    }

    /**
     * Gets the JavaFX Node
     */
    public Node getNode() {
        return hbox;
    }

    public void setNode(Node node) {
        hbox = (HBox) node;
    }

    /**
     * Recursively flattens the expression as much as possible
     * throughout the entire tree. Specifically, in every multiplicative
     * or additive expression x whose first or last
     * child c is of the same type as x, the children of c will be added to x, and
     * c itself will be removed. This method modifies the expression itself.
     */
    public void flatten() {
        flattenChildren();
        List<Expression> children = new ArrayList<>();

        for(Expression e : this.getSubexpressions()) {
            if(e.getClass() == getClass() && !(e instanceof ParenExpression)) {
                CompoundExpression tempE = (CompoundExpression) e;
                children.addAll(tempE.getSubexpressions());
            }
            else {
                children.add(e);
            }
        }
        _expressions.clear();
        for(Expression exp : children) {
            addSubexpression(exp);
        }
        createHBox();
    }

    private void flattenChildren() {
        for(Expression c : _expressions) {
            // If the child is a SimpleExpression, nothing will happen
            c.flatten();
        }
    }

    abstract void createHBox();

    void hBoxHelper(String str) {
        int i = getSubexpressions().size();
        for(Expression expression : getSubexpressions())
        {
            hbox.getChildren().add(expression.getNode());
            i--;
            if(i > 0) {
                hbox.getChildren().add(new Label(str));
            }
        }
    }

    /**
     * Creates a String representation by recursively printing out (using indentation) the
     * tree represented by this expression, starting at the specified indentation level.
     *
     * @param stringBuilder the StringBuilder to use for building the String representation
     * @param indentLevel   the indentation level (number of tabs from the left margin) at which to start
     */
    public void convertToString(StringBuilder stringBuilder, int indentLevel) {
        indent(stringBuilder, indentLevel);
        stringBuilder.append(getContents());
        stringBuilder.append("\n");
        int indentMod;               // modifies the number of indents. either 1 or 0
        int childCount = 0;          // represents the "index" number of the child in relation to the parent
        for (Expression n : _expressions) {
            childCount++;            // increases to represent the index of the child

            if (childCount == 1)     // if this child is the newest of a set, then increase indent by 1
                indentMod = 1;
            else                     // otherwise the indent level remains the same
                indentMod = 0;

            n.convertToString(stringBuilder, indentLevel += indentMod);   // recursive call to keep going down the list
        }
    }

    /**
     * Static helper method to indent a specified number of times from the left margin, by
     * appending tab characters to the specified StringBuilder.
     *
     * @param stringBuilder the StringBuilder to which to append tab characters.
     * @param indentLevel   the number of tabs to append.
     */
    private void indent(StringBuilder stringBuilder, int indentLevel) {
        for (int i = 0; i < indentLevel; i++) {
            stringBuilder.append('\t');
        }
    }

    public Expression getChildByPos(double x, double y) {
        for(Expression expression : _expressions) {
            Node label = expression.getNode();
            if(label.contains(label.sceneToLocal(x, y))) {
                System.out.println("found");
                return expression;
            }
        }
        return null;
    }
}
