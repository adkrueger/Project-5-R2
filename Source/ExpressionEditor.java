import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.Collections;

public class ExpressionEditor extends Application {

    private static Expression _topRoot;
    private static Expression _root;
    private static ArrayList<Expression> _nodes = new ArrayList<>();
    private static Label _label;
    private static Node _ghostLabel;
    private static Pane _pane;
    private static EventType previousMouse;

    private static double _startSceneX = 0;
    private static double _startSceneY = 0;
    private static boolean canDrag = false;

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Mouse event handler for the entire pane that constitutes the ExpressionEditor
     */
    private static class MouseEventHandler implements EventHandler<MouseEvent> {
        MouseEventHandler(Pane pane, CompoundExpression rootExpression_) {
            _pane = pane;
            _topRoot = rootExpression_;
            _root = rootExpression_;
            _label = new Label("Ready to begin!");
            _pane.getChildren().add(_label);
        }

        public void handle(MouseEvent event) {
            if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                _startSceneX = event.getSceneX();
                _startSceneY = event.getSceneY();
                previousMouse = event.getEventType();
                canDrag = _ghostLabel != null && _ghostLabel.contains(_ghostLabel.sceneToLocal(_startSceneX, _startSceneY));
            } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED && _label != null && canDrag) {
                double posX = event.getSceneX();
                double posY = event.getSceneY();
                posX -= _ghostLabel.sceneToLocal(_startSceneX, _startSceneY).getX();
                posY -= _ghostLabel.sceneToLocal(_startSceneX, _startSceneY).getY() + 1.5 * _label.getHeight();
                _label.setTranslateX(posX);
                _label.setTranslateY(posY);
                //findClosestNodeTree(_label.getTranslateX());
                previousMouse = event.getEventType();
            } else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
                if(previousMouse == MouseEvent.MOUSE_PRESSED)
                {
                    _startSceneX = event.getSceneX();
                    _startSceneY = event.getSceneY();
                    mouseClicked();
                    if(_ghostLabel != null) {
                        Bounds local = _ghostLabel.localToScene(_ghostLabel.getBoundsInLocal());
                        _label.setTranslateX(local.getMinX());
                        _label.setTranslateY(local.getMinY() - 1.5 * _label.getHeight());
                        _label.setBorder(Expression.RED_BORDER);
                        generateOptions();
                    }
                }
                else if(_label != null){
                    _root = _topRoot;
                    cleanUp();
                    previousMouse = event.getEventType();
                }
            }
        }
    }

    /*
     * TODO: Write JavaDocs
     */
    private static void mouseClicked() {
        if(_ghostLabel != null) {
            cleanUp();
            _ghostLabel = null;
        }
        _root = _root.getChildByPos(_startSceneX, _startSceneY);
        if(_root != null) {
            _ghostLabel = _root.getNode();
            _label.setText(_root.expToText());
            HBox parent = (HBox) _root.getNode().getParent();
            parent.getChildren().set(parent.getChildren().indexOf(_root.getNode()), _ghostLabel);
            _ghostLabel.setOpacity(0.5f);
        }
        else {
            _root = _topRoot;
        }
    }

    private static void cleanUp() {
        _ghostLabel.setOpacity(1.0f);
        _label.setBorder(Expression.NO_BORDER);
        _label.setText("");
        _label.setTranslateX(0);
        _label.setTranslateY(0);
    }

    private static void generateOptions() {
        for (int i = 0; i <_root.getParent().getSubexpressions().size(); i++) {
            CompoundExpression tempNode = _root.getParent().deepCopy();
            Collections.swap(tempNode.getSubexpressions(), _root.getParent().getSubexpressions().indexOf(_root), i);
            System.out.println(tempNode.convertToString(0));
            _nodes.add(tempNode);
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
    public void start(Stage primaryStage) {
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
                expression.getNode().setLayoutX(WINDOW_WIDTH / 4.0);
                expression.getNode().setLayoutY(WINDOW_HEIGHT / 2.0);

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
