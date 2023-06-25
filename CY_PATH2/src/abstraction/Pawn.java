package abstraction;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import control.Coordinate;

import java.io.Serializable;
/**
 * Class that represents the player's pawn
 * @author Kevin, Tess, Esteban, Ibrahim
 */
public class Pawn implements Serializable {
    //position on the GameBoard
    private int x;
    private int y;
    //x or y Axis where the pawn wins
    private int Vict;
    //pawn number
    private int numPawn;
    private transient Circle circle;
    private transient Color color;

    public Pawn(int a, int b, int numPawn)
    {
        this.x = a;
        this.y = b;
        this.Vict = 0;
        this.numPawn = numPawn;
        this.circle = new Circle(20);
    }

    public Pawn()
    {
        this.x = 0;
        this.y = 0;
    }

    //getter
    public int getX()
    {
        return this.x;
    }
    public int getY()
    {
        return this.y;
    }
    public int getNumPawn()
    {
        return this.numPawn;
    }
    public Color getColor(){return this.color;}
    public Circle getCircle(){
        return this.circle;
    }
    public Coordinate getCoordinate(){return new Coordinate(this.getX(),this.getY());}
    public int getVict(){return this.Vict;}

    //setter
    public void setCircle(Circle circle){this.circle=circle;}
    public void setX(int x) {this.x = x;} //new position of the pawn
    public void setY(int y) {this.y = y;}
    public void setColor(Color color){this.color = color;}
    public void setCoord(int x, int y){
        this.setX(x);
        this.setY(y);
    }
    public void setVict(int Vict){this.Vict = Vict;}

    @Override
    public String toString() {
        return "Pawn{" +
                "x=" + x +
                ", y=" + y +
                ", numPawn=" + numPawn +
                '}';
    }
}