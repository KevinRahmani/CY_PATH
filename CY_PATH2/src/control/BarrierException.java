package control;
import abstraction.*;
import javafx.animation.PauseTransition;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.util.Duration;


public class BarrierException extends RuntimeException{

    public static final long serialVersionUID = 1L;


    public BarrierException(){
        super();
    }

    //method which triggers a redBarrier on the board at the location of the intersection that was clicked
    public void redBarrier(Intersect intersect, Orientation orientation){
        StackPane stack = intersect.getStack();
        //create a new Line for the red Barrier
        Line redLine = new Line();
        redLine.setStrokeWidth(7);
        redLine.setStyle("-fx-pref-width: 96px;-fx-stroke: red;");

        if(orientation == Orientation.VERTICAL){
            redLine.setEndY(96);
            redLine.toFront();
        } else {
            stack.setTranslateX(0);
            redLine.setEndX(96);
            redLine.toFront();
        }
        stack.getChildren().add(redLine);
        stack.toFront();

        //We pause the animation of the red Barrier for 1 sec to show it and remove it after
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        pause.setOnFinished(e ->{
            stack.getChildren().remove(redLine);

            //2 particular cases
            if(orientation == Orientation.HORIZONTAL && stack.getChildren().isEmpty()){
                //when the stack pane's size is reduced makes the stack pane go back to normal place
                stack.setTranslateX(47);
            }
            if((orientation == Orientation.HORIZONTAL) && (stack.getChildren().size() == 1)){
                //again makes the stack pane go back to normal place
                if(intersect.getVertical()) {
                    stack.setTranslateX(47);
                }
            }
        });
        pause.play();
    }
}
