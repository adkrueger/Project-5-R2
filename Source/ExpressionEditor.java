import javafx.application.Application;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.util.*;

public class ExpressionEditor extends Application {

    private static CompoundExpression _topRoot;
    private static CompoundExpression _copyTop;
    private static Expression _root;
    private static Node _label;
    private static Node _ghostLabel;
    private static EventType _previousMouse;
    private static Pane _pane;

    private static Pane expressionPane = new Pane();

    private static double _startSceneX = 0;
    private static double _startSceneY = 0;
    private static boolean canDrag = false;
    private static HashMap<Double, CompoundExpression> list = new HashMap<>();

    /**
     * Launches the GUI
     *
     * @param args arguments to pass to launch
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Mouse event handler for the entire pane that constitutes the ExpressionEditor
     */
    private static class MouseEventHandler implements EventHandler<MouseEvent> {
        MouseEventHandler(Pane pane, CompoundExpression rootExpression_) {
            _topRoot = rootExpression_;
            _root = rootExpression_;
            _pane = pane;
        }

        /**
         * The event handler
         *
         * @param event the event to be handled
         */
        public void handle(MouseEvent event) {
            if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                _startSceneX = event.getSceneX();
                _startSceneY = event.getSceneY();
                _previousMouse = event.getEventType();
                canDrag = _ghostLabel != null && _ghostLabel.contains(_ghostLabel.sceneToLocal(_startSceneX, _startSceneY));
            } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED && _label != null && canDrag) {
                double posX = event.getSceneX();
                double posY = event.getSceneY();
                posX -= _ghostLabel.sceneToLocal(_startSceneX, _startSceneY).getX();
                posY -= _ghostLabel.sceneToLocal(_startSceneX, _startSceneY).getY();

                _label.setTranslateX(posX);
                _label.setTranslateY(posY);

                CompoundExpression top = _root.getParent();

                if (top != null) {
                    top.getSubexpressions().clear();
                    Expression e = getClosest();
                    if (e.getContents().equals("()")) {
                        e = ((CompoundExpression) e).getSubexpressions().get(0);
                    }
                    top.getSubexpressions().add(e);
                }

                _copyTop = _topRoot.deepCopy();
                update(_pane, _copyTop);
                _previousMouse = event.getEventType();

            } else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
                if (_previousMouse == MouseEvent.MOUSE_PRESSED) {
                    mouseClicked(event.getSceneX(), event.getSceneY());
                } else {
                    if (_label != null) {
                        _ghostLabel = null;
                        _pane.getChildren().remove(_label);
                        resetOpacity(_copyTop);
                        _root = _copyTop;
                        _topRoot = _copyTop;
                        System.out.println(_topRoot.convertToString(0));
                    }
                }
            }
        }

        /**
         * Updates the Expression on the screen
         *
         * @param pane    the Pane containing the Expression
         * @param topRoot the topmost expression
         */
        private static void update(Pane pane, Expression topRoot) {
            pane.getChildren().clear();
            pane.getChildren().add(topRoot.getNode());
            if (_label != null) {
                pane.getChildren().add(_label);
            }
            topRoot.getNode().setLayoutX(WINDOW_WIDTH / 4.0);
            topRoot.getNode().setLayoutY(WINDOW_HEIGHT / 3.0);
        }
    }

    /**
     * Resets the opacity and border for the finished Node
     *
     * @param exp the given Node
     */
    private static void resetOpacity(Expression exp) {
        if (exp instanceof CompoundExpression) {
            for (Expression e : ((CompoundExpression) exp).getSubexpressions()) {
                e.getNode().setOpacity(1.0f);
                ((Region) e.getNode()).setBorder(Expression.NO_BORDER);
                e.setOpacity(1.0);
                resetOpacity(e);
            }
        } else {
            ((Region) exp.getNode()).setBorder(Expression.NO_BORDER);
            exp.getNode().setOpacity(1.0f);
            exp.setOpacity(1.0);
        }
    }

    /**
     * Returns the closest Expression to the selected Expression
     *
     * @return the closest Expression to the selected Expression
     */
    private static CompoundExpression getClosest() {
        double bestDistance = -1.0;
        double currentBest = -1.0;
        for (Double dist : list.keySet()) {
            if (bestDistance == -1.0 || bestDistance > Math.abs(_label.getTranslateX() - dist)) {
                bestDistance = Math.abs(_label.getTranslateX() - dist);
                currentBest = dist;
            }
        }
        return list.get(currentBest);
    }

    /**
     * Checks if the mouse is clicked and changes focus appropriately
     *
     * @param sceneX the x position of the click
     * @param sceneY the y position of the click
     */
    private static void mouseClicked(double sceneX, double sceneY) {
        _pane.getChildren().remove(_label);
        _startSceneX = sceneX;
        _startSceneY = sceneY;
        _root = _root.getChildByPos(_startSceneX, _startSceneY);
        if (_ghostLabel != null) {
            ((Region) _ghostLabel).setBorder(Expression.NO_BORDER);
        }
        if (_root != null) {
            _ghostLabel = _root.getNode();
            ((Region) _ghostLabel).setBorder(Expression.RED_BORDER);
            Expression e = _root.deepCopy();
            e.flatten();
            _label = e.getNode();
            _pane.getChildren().add(_label);
            _label.setTranslateX(_ghostLabel.localToScene(_ghostLabel.getBoundsInLocal()).getMinX());
            _label.setTranslateY(_ghostLabel.localToScene(_ghostLabel.getBoundsInLocal()).getMinY());
            makeClosest();
        } else {
            _root = _topRoot;
        }
    }

    /**
     * Makes a list of all possible expressions when the
     * current Expression has been focused
     */
    private static void makeClosest() {
        list.clear();
        int i = 0;
        for (Expression exp : _root.getParent().getSubexpressions()) {
            double distance = exp.getNode().localToScene(exp.getNode().getBoundsInLocal()).getMinX();
            CompoundExpression top = _root.getParent().deepCopy();
            top.getSubexpressions().remove(_root.getParent().getSubexpressions().indexOf(_root));
            top.getSubexpressions().add(i, _root);
            CompoundExpression fixed = top.deepCopy();
            fixed.getSubexpressions().get(i).setOpacity(0.5);
            list.put(distance, fixed);
            i++;
        }
    }

    /**
     * The size of the font
     */
    static final int FONT_SIZE = 36;

    /**
     * Size of the GUI
     */
    private static final int WINDOW_WIDTH = 800, WINDOW_HEIGHT = 400;

    /**
     * Initial expression shown in the textbox
     */
    private static final String EXAMPLE_EXPRESSION = "2*x+3*y+4*z+(7+6*z)";

    /**
     * Parser used for parsing expressions.
     */
    private final ExpressionParser expressionParser = new SimpleExpressionParser();

    /**
     * Starts the GUI
     *
     * @param primaryStage the stage to view
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Expression Editor");

        // Add the textbox and Parser button
        final Pane queryPane = new HBox();
        final TextField textField = new TextField(EXAMPLE_EXPRESSION);
        final Button button = new Button("Parse");
        queryPane.getChildren().add(textField);

        // Add the callback to handle when the Parse button is pressed
        button.setOnMouseClicked(e -> {
            // Try to parse the expression
            try {
                // Success! Add the expression's Node to the expressionPane
                final Expression expression = expressionParser.parse(textField.getText(), true);
                System.out.println(expression.convertToString(0));
                _label = null;
                MouseEventHandler.update(expressionPane, expression);

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
        expressionPane.getChildren().add(queryPane);

        final Pane pane = new Pane();
        pane.getChildren().add(expressionPane);
        pane.getChildren().add(queryPane);

        primaryStage.setScene(new Scene(pane, WINDOW_WIDTH, WINDOW_HEIGHT));
        primaryStage.show();
    }
}
