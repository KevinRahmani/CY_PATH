package abstraction;

import control.Coordinate;
import javafx.scene.layout.StackPane;
/**
 * Class that represents each Cell where a Pawn will be able to get to
 * @author Kevin, Tess, Esteban, Ibrahim
 */

public class Case implements java.io.Serializable{
    public Pawn pawn;//check if there's a pawn
    private int x;
    private int y;
    private Coordinate c =new Coordinate(x,y);
    private transient StackPane stack;
    public boolean visited; // checks if a case has been visited
    double distance; // distance between the pawn's positions and the case


    public Case()
    {
        this.pawn =null;
        this.stack = new StackPane();
        this.visited = false;
        this.distance = Double.POSITIVE_INFINITY;
    }


    //getter
    public Pawn getPawn() {return this.pawn;}
    public double getDistance() {return distance;}
    public StackPane getStack(){return this.stack;}
    public int getX(){return this.x;}
    public int getY(){return this.y;}
    public boolean isVisited(){return this.visited;}

    //setters
    public void setVisited(boolean v){this.visited = v;}
    public void setDistance(double dist){this.distance = dist;}
    public void setPawn(Pawn pawn) {this.pawn=pawn;}
    public void setX(int x){this.x = x;this.c.setX(x);}
    public void setY(int y){this.y = y;this.c.setY(y);}


    @Override
    public String toString() {
        return "Case{" +
                "pawn=" + pawn +
                ", c=" + c +
                '}';
    }
}