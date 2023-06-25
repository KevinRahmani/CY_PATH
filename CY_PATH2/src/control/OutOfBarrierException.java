package control;


import javafx.scene.control.Label;
//exception for when we run out of barrier, it displays a string on the left top of the screen
public class OutOfBarrierException extends Exception{

    public static final long serialVersionUID = 1L;

    public OutOfBarrierException(){
        super();
    }

    public void redLabel(Label label){
        label.setStyle("-fx-text-fill: red;-fx-font-size: 18px;-fx-alignment: center");
    }
}
