import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

abstract class CompoundExpressionImpl extends SimpleExpressionImpl implements CompoundExpression {

    private ArrayList<Expression> _expressions = new ArrayList<>();
    HBox hbox;

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
    public ArrayList<Expression> getSubexpressions() {
        return _expressions;
    }

    /**
     * Creates and returns a deep copy of the expression.
     * The entire tree rooted at the target node is copied, i.e.,
     * the copied Expression is as deep as possible.
     *
     * @return the copied expression
     */
    public CompoundExpression deepCopy() {
        CompoundExpression copy = deepCopyHelper();

        for (Expression subExp : _expressions) {
            Expression copyMini = subExp.deepCopy();
            copyMini.setOpacity(subExp.getOpacity());     //
            copyMini.flatten();                           // formats the copy appropriately
            copy.addSubexpression(copyMini);              //
        }

        copy.setOpacity(getOpacity());
        copy.flatten();
        return copy;
    }

    /**
     * Helper method that returns new Object of this type
     *
     * @return new Object of type Expression
     */
    abstract CompoundExpression deepCopyHelper();

    /**
     * Creates an HBox
     */
    void easyMake() {
        hbox = new HBox();
        hbox.setOpacity(getOpacity());
        if (getOpacity() < 1) {
            hbox.setBorder(Expression.RED_BORDER);
        }
        hBoxHelper(getContents());
    }

    /**
     * Gets the JavaFX Node
     */
    public Node getNode() {
        return hbox;
    }

    /**
     * Sets the given Node as the Node in the parameter
     * (assumes given node is an HBox so that casting will
     * operate properly)
     *
     * @param node the node
     */
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

        for (Expression e : this.getSubexpressions()) {
            if (e.getClass() == getClass() && !(e instanceof ParenExpression)) {
                CompoundExpression tempE = (CompoundExpression) e;
                children.addAll(tempE.getSubexpressions());
            } else {
                children.add(e);
            }
        }

        _expressions.clear();
        for (Expression exp : children) {
            addSubexpression(exp);
        }

        createHBox();
    }

    /**
     * Flattens all children in the given expression
     */
    private void flattenChildren() {
        for (Expression c : _expressions) {
            // If the child is a SimpleExpression, nothing will happen
            c.flatten();
        }
    }

    /**
     * Creates an HBox
     */
    abstract void createHBox();

    /**
     * Adds in alternating order the Expression then str and so on,
     * ending with an Expression.
     *
     * @param str the expression in String form (i.e. "+")
     */
    private void hBoxHelper(String str) {
        int i = getSubexpressions().size();

        for (Expression expression : getSubexpressions()) {
            hbox.getChildren().add(expression.getNode());
            i--;
            if (i > 0) {
                Label label = new Label(str);
                label.setFont(Font.font("Times", ExpressionEditor.FONT_SIZE));
                hbox.getChildren().add(label);
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

    /**
     * Gets the child of an expression given a
     * certain (x,y) coordinate
     *
     * @param x the x coordinate of the query
     * @param y the y coordinate of the query
     * @return the expression in the x,y position or
     * null if nothing is found
     */
    public Expression getChildByPos(double x, double y) {
        for (Expression expression : _expressions) {
            Node label = expression.getNode();
            if (label.contains(label.sceneToLocal(x, y))) {
                System.out.println("found");
                return expression;
            }
        }
        return null;
    }
}
