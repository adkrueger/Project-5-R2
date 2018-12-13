import javafx.scene.layout.HBox;

import java.util.List;

interface CompoundExpression extends Expression {
	/**
	 * Adds the specified expression as a child.
	 * @param subexpression the child expression to add
	 */
	void addSubexpression (Expression subexpression);

	/**
	 * Returns the list of expressions
	 *
	 * @return the list of expressions
	 */
	List<Expression> getSubexpressions();

	// TODO: Add this as well
	CompoundExpression deepCopy();
}
