package abstraction;

import control.Coordinate;
import javafx.scene.layout.StackPane;

import java.io.Serializable;
/**
 * Class that represents the barriers that will block the pawn's possible moves
 */

public class Intersect implements Serializable {
    Coordinate c;
    private boolean horizontal;//horizontal intersect
    private boolean vertical;//vertical intersect
    private transient StackPane stack;//contains intersect and error intersect

    public Intersect(int x, int y){
        this.stack = new StackPane();
        this.c = new Coordinate(x,y);
        this.vertical = false;
        this.horizontal = false;
    }

    //getters
    public StackPane getStack(){
        return this.stack;
    }
    public boolean getVertical() {return this.vertical;}
    public boolean getHorizontal(){return this.horizontal;}
    public Coordinate getCoordinates(){return c;}


    //setters
    public void setVertical(boolean vertical){this.vertical = vertical;}
    public void setHorizontal(boolean horizontal){this.horizontal = horizontal;}
    public void setStack(StackPane stack){
        this.stack = stack;
    }


    public String toString(){
        String orientation;
        if(getVertical()){
            orientation = "V";
        }else if(getHorizontal()){
            orientation ="H";
        }else{
            orientation = "";
        }
        return "("+(getVertical() || getHorizontal())+":{"+orientation+" "+c.getX()+","+c.getY()+"}";
    }
}