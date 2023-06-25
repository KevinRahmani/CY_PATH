package control;

import javafx.animation.FillTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

//exception for an illegal move of a pawn, it displays a red case for 1 sec after the user clicked on it
public class MoveException extends Exception{

    public static final long serialVersionUID = 1L;

    public MoveException(){
        super();
    }

    public void animateStackPane(StackPane stackPane, Button button, Color color) {
        Rectangle fillRect = new Rectangle(stackPane.getWidth(), stackPane.getHeight(), color);
        stackPane.getChildren().add(fillRect);

        // Animation to make the case red
        FillTransition fillTransition = new FillTransition(Duration.seconds(0.1), fillRect);
        fillTransition.setFromValue(Color.TRANSPARENT);
        fillTransition.setToValue(color);
        fillTransition.setDelay(Duration.ZERO);

        // Animation to reestablish the color of the case
        FillTransition restoreTransition = new FillTransition(Duration.seconds(0.1), fillRect);
        restoreTransition.setFromValue(color);
        restoreTransition.setToValue(Color.TRANSPARENT);
        restoreTransition.setDelay(Duration.seconds(0.1));

        // Disable the button during the animation
        button.setDisable(true);

        // End of the animation
        restoreTransition.setOnFinished(event -> {
            stackPane.getChildren().remove(fillRect);
            // Activate the button at the end
            button.setDisable(false);
        });

        // Throw animation
        SequentialTransition animation = new SequentialTransition(fillTransition, restoreTransition);
        animation.play();
    }
}
