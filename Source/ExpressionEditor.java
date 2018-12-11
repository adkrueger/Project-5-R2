import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ExpressionEditor extends Application {
	public static void main (String[] args) {
		launch(args);
	}

	/**
	 * Mouse event handler for the entire pane that constitutes the ExpressionEditor
	 */
	private static class MouseEventHandler implements EventHandler<MouseEvent> {
		MouseEventHandler (Pane pane_, CompoundExpression rootExpression_) {

		}
		double _startSceneX = 0;
		double _startSceneY = 0;
		public void handle (MouseEvent event) {
			if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
				_startSceneX = event.getSceneX();
				_startSceneY = event.getSceneY();
			}
			/*
			if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
				double posX = event.getSceneX();
				double posY = event.getSceneY();
				posX -= _startSceneX;
				posY -= _startSceneY;
				_label.setTranslateX(posX;
				_label.setTranslateY(posY);
			}
			if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
				_label.setLayoutX(_label.getLayoutX() + _label.getTranslateX());
				_label.setLayoutY(_label.getLayoutY() + _label.getTranslateY());
				_label.setTranslateX(0);
				_label.setTranslateY(0);
			}
			*/
		}
	}

	/**
	 * Size of the GUI
	 */
	private static final int WINDOW_WIDTH = 500, WINDOW_HEIGHT = 250;

	/**
	 * Initial expression shown in the textbox
	 */
	private static final String EXAMPLE_EXPRESSION = "2*x+3*y+4*z+(7+6*z)";

	/**
	 * Parser used for parsing expressions.
	 */
	private final ExpressionParser expressionParser = new SimpleExpressionParser();

	@Override
	public void start (Stage primaryStage) {
		primaryStage.setTitle("Expression Editor");

		// Add the textbox and Parser button
		final Pane queryPane = new HBox();
		final TextField textField = new TextField(EXAMPLE_EXPRESSION);
		final Button button = new Button("Parse");
		queryPane.getChildren().add(textField);

		final Pane expressionPane = new Pane();

		// Add the callback to handle when the Parse button is pressed	
		button.setOnMouseClicked(e -> {
			// Try to parse the expression
			try {
				// Success! Add the expression's Node to the expressionPane
				final Expression expression = expressionParser.parse(textField.getText(), true);
				System.out.println(expression.convertToString(0));
				expressionPane.getChildren().clear();
				expressionPane.getChildren().add(expression.getNode());
				System.out.println("called 2");
				expression.getNode().setLayoutX(WINDOW_WIDTH/4.0);
				expression.getNode().setLayoutY(WINDOW_HEIGHT/2.0);

				// If the parsed expression is a CompoundExpression, then register some callbacks
				if (expression instanceof CompoundExpression) {
					((Pane) expression.getNode()).setBorder(Expression.NO_BORDER);
					final MouseEventHandler eventHandler = new MouseEventHandler(expressionPane, (CompoundExpression) expression);
					expressionPane.setOnMousePressed(eventHandler);
					expressionPane.setOnMouseDragged(eventHandler);
					expressionPane.setOnMouseReleased(eventHandler);
				}
			} catch (ExpressionParseException epe) {
				// If we can't parse the expression, then mark it in red
				textField.setStyle("-fx-text-fill: red");
			}
		});
		queryPane.getChildren().add(button);

		// Reset the color to black whenever the user presses a key
		textField.setOnKeyPressed(e -> textField.setStyle("-fx-text-fill: black"));
		
		final BorderPane root = new BorderPane();
		root.setTop(queryPane);
		root.setCenter(expressionPane);

		primaryStage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
		primaryStage.show();
	}
}
