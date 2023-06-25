package control;

//coordinate x and y of an object on the board

/**
 * Class that defines a data structure Coordinate with two integers as attributes
 * @author Kevin, Tess, Esteban, Ibrahim
 */

public class Coordinate implements java.io.Serializable {
    private int x;
    private int y;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    //getters
    public int getX() {return x;}
    public int getY() {return y;}

    //setters
    public void setX(int x){ this.x = x;}
    public void setY(int y) { this.y = y;}

    @Override
    public String toString() {
        return "( "+ x + " ; "+y+" )";
    }

    @Override
    public boolean equals(Object obj){
        return (obj instanceof Coordinate) && (getX() == ((Coordinate)obj).getX()) && (getY() == ((Coordinate)obj).getY());
    }
}


