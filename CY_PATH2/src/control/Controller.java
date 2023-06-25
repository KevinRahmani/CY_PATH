package control;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import abstraction.*;

import java.io.*;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.*;

//Initialize interface necessary to utilize a ChangeListener => EventHandler that's attached to an Observable value
/**
 * Class where the game is loaded
 * @author Kevin, Tess, Esteban, Ibrahim
 */
public class Controller implements Initializable, java.io.Serializable {
    /*In the SceneBuilder app, we defined fx:id that we take back from there (located in the Controller section)
     * ==> FXML injection
     */
    @FXML
    private Pane pane;
    //Assist nbLabel
    @FXML
    private Label assistNbLabel;
    //Label used for the number of players
    @FXML
    private Label nbLabel;
    //slider used for choosing the number of players
    @FXML
    private Slider slider;
    @FXML
    private Button buttonValidateGame;
    @FXML
    private GridPane boardGame;
    @FXML
    private Button buttonSaveGame;

    @FXML
    private Button buttonLoadGame;
    @FXML
    private Button buttonPutBarrier;
    //nbPlayers variable
    int nbPlayers;
    //List of all pawns
    List<Pawn> pawnArrayList;
    //actual pawn
    private Pawn pawn;
    //list of possible move of each pawn
    private List<Coordinate> possibleCoordinate;
    //index of the pawn
    private Integer index;
    //boolean if first round
    boolean firstRound;
    //array of intersect
    private Intersect[][] inter;
    //orientation clicked
    private Orientation orientation;
    //array of case
    private Case[][] board;
    //num Barrier
    private int numBarrier;
    //Label for numberBarrier
    private final Label labelNbBarrier;
    //Line for barrier preview
    private Line imaginaryLine;
    //arrayLabel for Player's sides
    Map<Integer, Label> arrayLabel;

    //Empty constructor because Javafx automatically inject the FXML code here


    public Controller(){
        this.pawnArrayList = new ArrayList<>();
        this.nbPlayers = 0;
        this.pawn = null;
        this.possibleCoordinate = null;
        this.index = 0;
        this.firstRound = true;
        this.orientation = Orientation.HORIZONTAL;
        this.numBarrier = 20;
        this.board = new Case[9][9];
        this.inter = new Intersect[8][8];
        this.labelNbBarrier = new Label();
        arrayLabel = new HashMap<>();
    }

    //Serialization method to save the game state

    /**
     * Save the current game state
     */
    private void saveGame(){
        try{
            FileOutputStream fos = new FileOutputStream("game.save");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(board); // Stackpane attribute from Case class cannot be serialized
            oos.writeObject(inter); // StackPane attribute from Intersect class cannot be serialized
            oos.writeObject(pawnArrayList); //Cercle and Color aren't serialized
            oos.writeObject(pawn);
            oos.writeObject(index);
            oos.flush();
            oos.close();
            System.out.println("Game saved");
        }catch (Exception e){
            System.out.println("Serialization error! Can't save data " +
                    e.getClass()+ " : "+ e.getMessage());
        }
    }


    //Serialization method to load the game state

    /**
     * Load the last saved game state
     */
    private void loadGame(){
        try{

            FileInputStream fis = new FileInputStream("game.save");
            ObjectInputStream ois = new ObjectInputStream(fis);
            board = (Case[][]) ois.readObject();
            inter = (Intersect[][]) ois.readObject();
            emptyPawnList(pawnArrayList);
            pawnArrayList = (List<Pawn>) ois.readObject();
            pawn = (Pawn) ois.readObject();
            index = (Integer) ois.readObject();
            ois.close();


        }catch (Exception e){
            System.out.println("Serialization error! Can't load data" +
                    e.getClass()+" : "+ e.getMessage());
        }
    }

    /**
     * Start the user interface and listen if any element has been toggled
     * @param url
     * @param resourceBundle
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {

        nbPlayers = (int) slider.getValue();
        nbLabel.setText(nbPlayers +" players");

        buttonValidateGame.setOnAction(actionEvent -> {
            for (int row = 0; row < 9; row++) {
                for (int col = 0; col < 9; col++) {
                    Case c = new Case();
                    c.setX(col);c.setY(row);
                    StackPane stack = c.getStack();
                    stack.setUserData(c);
                    Button b = new Button();
                    stack.getChildren().add(b);
                    //fill this 2D array to instantiate all cells
                    board[row][col] = c;
                    b.setOnAction(this::movePawn);
                    b.setStyle("-fx-border-color: grey; -fx-border-width:1px;-fx-background-color:none;-fx-pref-width: 96px;-fx-pref-height: 80px");
                    boardGame.add(stack, col, row);
                }
            }
            for (int row = 0 ;row <8 ; row ++){
                for (int column =0 ; column < 8 ; column++){
                    Intersect intersection = new Intersect(column,7-row);
                    this.inter[column][7-row]= intersection;
                    StackPane intersectStack = intersection.getStack();
                    intersectStack.setUserData(this.inter[column][7-row]);
                    intersectStack.setMaxHeight(9);intersectStack.setMaxWidth(9);
                    intersectStack.setTranslateX(47);intersectStack.setTranslateY(28);

                    boardGame.add(intersectStack, column, row); // the board has 145 elements [0 => 80] : stack panes  ; [81 => 144] lines
                }
            }
            boardGame.setHgap(7);
            boardGame.setVgap(7);
            //Insert all the players into an arrayList and insert all Labels into an HashMap with a key whi is their number
            initPawns((int) slider.getValue(),Mode.INITIAL);
            //Update all the cases which contains pawn
            initialUpdateCase();
            //delete the labels and slider from initialization
            setDisable();
            buttonPutBarrier.setOnMouseClicked(event -> {
                if (event.getClickCount()== 1){
                    imaginaryLine = new Line();
                    imaginaryLine.setMouseTransparent(true);
                    imaginaryLine.setStrokeWidth(5);
                    imaginaryLine.setStyle("-fx-stroke: yellow;");
                    imaginaryLine.setOpacity(0.6);
                    pane.getChildren().add(imaginaryLine);
                }
                for (int i=81 ; i<145;i++){
                    boardGame.getChildren().get(i).setStyle("-fx-background-color:grey");
                }
            });
            for (Node n: boardGame.getChildren()){
                if (n instanceof StackPane ts){
                    ts.setOnMouseMoved(mouseEvent -> {
                        if(imaginaryLine!=null){
                            if(orientation ==Orientation.HORIZONTAL ){
                                pane.getChildren().remove(imaginaryLine);
                                imaginaryLine.setStartX(mouseEvent.getSceneX());
                                imaginaryLine.setEndX(mouseEvent.getSceneX()+105);
                                imaginaryLine.setTranslateX(-52.5);
                                imaginaryLine.setStartY(mouseEvent.getSceneY());
                                imaginaryLine.setEndY(mouseEvent.getSceneY());
                                imaginaryLine.setTranslateY(0);
                                pane.getChildren().add(imaginaryLine);

                            }else{
                                pane.getChildren().remove(imaginaryLine);
                                imaginaryLine.setStartX(mouseEvent.getSceneX());
                                imaginaryLine.setEndX(mouseEvent.getSceneX());
                                imaginaryLine.setTranslateX(0);
                                imaginaryLine.setStartY(mouseEvent.getSceneY());
                                imaginaryLine.setEndY(105+ mouseEvent.getSceneY());
                                imaginaryLine.setTranslateY(-50);
                                pane.getChildren().add(imaginaryLine);
                            }
                        }
                    });
                    ts.setOnMouseExited(event->{
                        if(imaginaryLine!=null){
                            pane.getChildren().remove(imaginaryLine);
                        }

                    });
                    ts.setOnMouseClicked(event -> {
                        if (imaginaryLine!=null){
                            if(event.getButton()==MouseButton.SECONDARY){
                                if(orientation==Orientation.VERTICAL){
                                    orientation = Orientation.HORIZONTAL;
                                    pane.getChildren().remove(imaginaryLine);
                                    imaginaryLine.setStartX(event.getSceneX());
                                    imaginaryLine.setEndX(event.getSceneX()+105);
                                    imaginaryLine.setTranslateX(-52.5);
                                    imaginaryLine.setStartY(event.getSceneY());
                                    imaginaryLine.setEndY(event.getSceneY());
                                    imaginaryLine.setTranslateY(0);
                                    pane.getChildren().add(imaginaryLine);
                                }else{
                                    orientation = Orientation.VERTICAL;
                                    pane.getChildren().remove(imaginaryLine);
                                    imaginaryLine.setStartX(event.getSceneX());
                                    imaginaryLine.setEndX(event.getSceneX());
                                    imaginaryLine.setTranslateX(0);
                                    imaginaryLine.setStartY(event.getSceneY());
                                    imaginaryLine.setEndY(105+ event.getSceneY());
                                    imaginaryLine.setTranslateY(-50);
                                    pane.getChildren().add(imaginaryLine);
                                }
                            }else if(event.getButton()==MouseButton.PRIMARY){

                                if(putBarrier(((Intersect)ts.getUserData()).getCoordinates().getX(),((Intersect)ts.getUserData()).getCoordinates().getY(),orientation)){

                                    nextPawnAndCord();
                                }
                                imaginaryLine.setStyle("-fx-stroke: none;");
                                imaginaryLine =null;
                                for (int i=81 ; i<145;i++){
                                    boardGame.getChildren().get(i).setStyle("-fx-background-color:none");
                                }

                            }

                        }
                    });
                }
            }
        });
        //adding a changeListener to the slider via a lambda expression
        slider.valueProperty().addListener((observableValue, number, t1) -> {
            nbPlayers = (int) slider.getValue();
            nbLabel.setText(nbPlayers + " players");
        });

        buttonSaveGame.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton()==MouseButton.PRIMARY){
                saveGame();
            }
        });
        buttonLoadGame.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton()==MouseButton.PRIMARY){
                loadGame(); // loaded game.save
                initPawns(pawnArrayList.size(), Mode.FINAL); // re-create pawn's Circle and Color
                refreshGame(); // refresh boadGame
                clearPawns(); // clear all pawns and ghostPawns on the previous boardGame
                initialUpdateCase(); // show pawns from pawnArraylist on the board
                buttonLoadGame.setVisible(false);
                possibleCoordinate = findPossibleCase(pawn);
                addGhostPawn(possibleCoordinate,pawn); // add ghost pawns for the current player

            }
        });
    }

    //verify if x and y still into intersect array

    /**
     *  Check if there is an intersection at (x,y)
     * @param x  int
     * @param y  int
     * @return True if there is an intersection at (x,y)
     */
    public boolean VerifyCoordinateIntersect(int x, int y){
        return x >= 0 && x < 8 && y >= 0 && y < 8;
    }

    /**
     * Draw a horizontal line on the stackpane
     * @param stack Stackpane where the line will be drawn
     */
    public void createHLine(StackPane stack) {

        Line lineH = new Line();
        lineH.setStrokeWidth(7);
        lineH.setStyle("-fx-stroke: black;");
        stack.setTranslateX(0);
        lineH.setEndX(96);
        lineH.toBack();
        numBarrier--;
        labelNbBarrier.setText(numBarrier + " remaining intersects");
        stack.getChildren().add(lineH);
    }

    /**
     * Draw a vertical line on the stackpane
     * @param stack Stackpane where the line will be drawn
     */
    public void createVLine(StackPane stack){
            Line lineV = new Line();
            lineV.setStrokeWidth(7);
            lineV.setStyle("-fx-pref-width: 96px;-fx-stroke: black;");
            lineV.setEndY(96);
            lineV.toBack();
            numBarrier--;
            labelNbBarrier.setText(numBarrier +" remaining intersects");
            stack.getChildren().add(lineV);
        }

    /**
     * Place a barrier at the selected intersection with the chosen orientation
     * @param x int
     * @param y int
     * @param orientation enum to set the orientation (HORIZONTAL, VERTICAL)
     * @return true if the barrier has been put successfully
     */
    public boolean putBarrier(int x, int y, Orientation orientation) { // the third attribute specifies the orientation ("v" for vertical and "h" for horizontal)
        Intersect intersect = inter[x][y];
        try{
            if(orientation == Orientation.VERTICAL){
                if(!intersect.getHorizontal() && !intersect.getVertical()){//no overlapping
                    if(VerifyCoordinateIntersect(x,y+1) && VerifyCoordinateIntersect(x,y-1)){//if case on the top and on the bottom
                        if(!inter[x][y+1].getVertical() && !inter[x][y-1].getVertical()){//verify there is no vertical barrier
                            intersect.setVertical(true);
                            //test of dijkstra
                            for(Pawn p : pawnArrayList){
                                if(!dijkstraSearch(p)){
                                    intersect.setVertical(false);
                                    addGhostPawn(possibleCoordinate,pawn);
                                    throw new BarrierException();
                                }
                            }
                            createVLine(intersect.getStack());
                            return true;
                        }else{
                            throw new BarrierException();
                        }
                    } else if(!VerifyCoordinateIntersect(x,y+1)){//if only case on the top
                        if(!inter[x][y-1].getVertical()){//verify there is no vertical barrier
                            intersect.setVertical(true);
                            for(Pawn p : pawnArrayList){
                                if(!dijkstraSearch(p)){
                                    intersect.setVertical(false);
                                    addGhostPawn(possibleCoordinate,pawn);

                                    throw new BarrierException();
                                }
                            }
                            createVLine(intersect.getStack());
                            return true;
                        }else{
                            throw new BarrierException();
                        }
                    }else if(!VerifyCoordinateIntersect(x,y-1)){//if only case on the bottom
                        if(!inter[x][y+1].getVertical()){//verify there is no vertical barrier
                            intersect.setVertical(true);
                            for(Pawn p : pawnArrayList){
                                if(!dijkstraSearch(p)){
                                    intersect.setVertical(false);
                                    addGhostPawn(possibleCoordinate,pawn);
                                    throw new BarrierException();
                                }
                            }
                            createVLine(intersect.getStack());
                            return true;
                        }else{
                            throw new BarrierException();
                        }
                    }
                }else{
                    throw new BarrierException();
                }
            }
            else{
                if(!intersect.getVertical() && !intersect.getHorizontal()){//no overlapping
                    if(VerifyCoordinateIntersect(x+1,y) && VerifyCoordinateIntersect(x-1,y)){//if case on the left and on the right
                        if(!inter[x+1][y].getHorizontal() && !inter[x-1][y].getHorizontal()){//verify there is no horizontal barrier
                            intersect.setHorizontal(true);
                            for(Pawn p : pawnArrayList){
                                if(!dijkstraSearch(p)){
                                    intersect.setHorizontal(false);
                                    addGhostPawn(possibleCoordinate,pawn);
                                    throw new BarrierException();
                                }
                            }
                            createHLine(intersect.getStack());
                            return true;
                        }else{
                            throw new BarrierException();
                        }
                    } else if (VerifyCoordinateIntersect(x+1,y)){//if only case on the right
                        if(!inter[x+1][y].getHorizontal()){//verify there is no horizontal barrier
                            intersect.setHorizontal(true);
                            for(Pawn p : pawnArrayList){
                                if(!dijkstraSearch(p)){
                                    intersect.setHorizontal(false);
                                    addGhostPawn(possibleCoordinate,pawn);
                                    throw new BarrierException();
                                }
                            }
                            createHLine(intersect.getStack());
                            return true;
                        }else{
                            throw new BarrierException();
                        }
                    } else {//if only case on the left
                        if(!inter[x-1][y].getHorizontal()){//verify no horizontal barrier
                            intersect.setHorizontal(true);
                            for(Pawn p : pawnArrayList){
                                if(!dijkstraSearch(p)){
                                    intersect.setHorizontal(false);
                                    addGhostPawn(possibleCoordinate,pawn);
                                    throw new BarrierException();
                                }
                            }
                            createHLine(intersect.getStack());
                            return true;
                        }else{
                            throw new BarrierException();
                        }
                    }
                }else{
                    throw new BarrierException();
                }
            }
        }catch(BarrierException e){
            e.redBarrier(intersect,orientation);
            return false;
        }
        return false;
    }

    /**
     * Get the adjacent reachable cells from a starting cell
     * @param c targeted cell
     * @return A list of cells that are reachable for the pawn that is trying to figure out a path to win
     */
    public List<Case> addCase(Case c) // filters out the arrayList returned by getAdjCase, keeping the allowed moves only
    {
        List<Case> result = new ArrayList<>();
        //Initialize a temporal Pawn in order to be able to use the findPossibleCase method
        Pawn tempPawn = new Pawn();
        tempPawn.setCoord(c.getX(),c.getY());
        deleteGhostPawns();
        List<Coordinate> possibleMoves = findPossibleCase(tempPawn);
        for (Coordinate cell : possibleMoves){
            result.add((Case) getStackPane(cell.getX(), cell.getY()).getUserData());
        }
        return result;
    }

    /**
     * Determine if there is a path from the current pawn postion to the winning side of the board
     * @param pawn current pawn
     * @return true if there is a path from the current pawn postion to the winning side of the board
     */
    public boolean dijkstraSearch(Pawn pawn){
        //reset visited cells
        for (Case[] row: board){
            for (Case cell : row){
                cell.setVisited(false);
            }
        }
        //set the starting cell
        Case start = (Case) getStackPane(pawn.getX(), pawn.getY()).getUserData();

        //depends on the yAxis for 1 && 2 and on the xAxis for 3 and 4
        ArrayList<Case> victoryArrayList = new ArrayList<>();
        if (pawn.getNumPawn() ==1 || pawn.getNumPawn() == 2){
            for(int i=0;i<9;i++){
                victoryArrayList.add((Case) getStackPane(i, pawn.getVict()).getUserData());
            }
        } else {
            for(int i=0;i<9;i++){
                victoryArrayList.add((Case) getStackPane(pawn.getVict(),i).getUserData());
            }
        }

        //We use a comparator to classify elements by distance from the starting point
        Comparator<Case> comp = (c1,c2) -> (int) (c1.getDistance() - c2.getDistance());
        Case aCase=start;
        PriorityQueue<Case> queue = new PriorityQueue<>(comp);
        queue.offer(aCase); // insert the current case to the queue
        while(!queue.isEmpty()){
            aCase = queue.poll(); // return and remove the head of the queue
            for (Case adjCase: addCase(aCase)){

                if(aCase.getDistance() + 1 < adjCase.getDistance()){ // here 1 stands for the weight of a case
                    adjCase.setDistance(aCase.getDistance() + 1); // add 1 to the distance of the adjacent cells of aCase
                }
                if(!adjCase.visited){ // mark the adjacent cells to aCase if they aren't marked yet
                    queue.offer(adjCase);
                    adjCase.visited = true;
                }
            }
            //verify if we have a path on the list of victorious case of the pawn
            for (Case victoriousCase : victoryArrayList) {
                if(victoriousCase.isVisited()){

                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Setup the pawns color, starting position and winning position
     * @param nbPlayers int
     * @param mode enum to specify if it's a new game or a loading game (INITIAL,FINAL)
     */
    public void initPawns(int nbPlayers, Mode mode){
    //Hashmap of all labels of players
//    Map<Integer, Label> arrayLabel = new HashMap<>();

    //Creation of pawns and label's player


    //Coordinate of each pawn
    if(mode == Mode.INITIAL){
        for(int i = 1;i<=nbPlayers;i++){
            Label labelPlayer = new Label("Player " + i);
            arrayLabel.put(i,labelPlayer);
            Pawn p = new Pawn(0,0,i);
            pawnArrayList.add(p);

        }

        for(Pawn p : pawnArrayList){
            if(p.getNumPawn() == 1 ){
                p.setCoord(4,0);p.setVict(8);
                p.setColor(Color.BLACK);
                p.getCircle().setFill(p.getColor());

            } else if(p.getNumPawn() == 2){
                p.setCoord(4,8);p.setColor(Color.RED);
                p.getCircle().setFill(p.getColor());p.setVict(0);

            } else if(p.getNumPawn() == 3){
                p.setCoord(0,4);p.setColor(Color.BLUE);
                p.getCircle().setFill(p.getColor());p.setVict(8);

            } else if(p.getNumPawn() == 4) {
                p.setCoord(8,4);p.setColor(Color.GREEN);
                p.getCircle().setFill(p.getColor());p.setVict(0);
            }
        }
    } else {

        for (int i = 1 ; i< arrayLabel.size()+1;i++){
            pane.getChildren().remove(arrayLabel.get(i));
        }
        arrayLabel.clear();

        for(int i = 1;i<=nbPlayers;i++) {
            Label labelPlayer = new Label("Player " + i);
            arrayLabel.put(i, labelPlayer);
        }

        for(Pawn p : pawnArrayList){
            if(p.getNumPawn() == 1 ){
                p.setColor(Color.BLACK);p.setCircle(new Circle(20));
                p.getCircle().setFill(p.getColor());

            } else if(p.getNumPawn() == 2){
                p.setColor(Color.RED);
                p.setCircle(new Circle(20));p.getCircle().setFill(p.getColor());

            } else if(p.getNumPawn() == 3){
                p.setColor(Color.BLUE);
                p.setCircle(new Circle(20));p.getCircle().setFill(p.getColor());

            } else if(p.getNumPawn() == 4) {
                p.setColor(Color.GREEN);
                p.setCircle(new Circle(20));p.getCircle().setFill(p.getColor());
            }
        }
    }

    //label for the number of barrier
    labelNbBarrier.setPrefSize(200,25);
    labelNbBarrier.setLayoutX(25);labelNbBarrier.setLayoutY(190);
    labelNbBarrier.setStyle("-fx-alignment: center;-fx-font-size: 18px");
    labelNbBarrier.setText(numBarrier +" remaining intersects");
    if(mode == Mode.INITIAL){

        pane.getChildren().add(labelNbBarrier);
    }

    //Color and coordinate of each label
    for(Integer p : arrayLabel.keySet()){
        if(p == 1 ){
            arrayLabel.get(p).setStyle("-fx-translate-x: 380px;-fx-translate-y: 200px;-fx-alignment: center;-fx-font-size: 20px;");
        } else if(p == 2){
            arrayLabel.get(p).setStyle("-fx-translate-x: 380px;-fx-translate-y: 763px;-fx-text-fill: red;-fx-alignment: center;-fx-font-size: 20px;");
        } else if(p == 3){
            arrayLabel.get(p).setStyle("-fx-translate-x: 45px;-fx-translate-y: 488px;-fx-text-fill: blue;-fx-alignment: center;-fx-font-size: 20px;");
        } else if(p == 4) {
            arrayLabel.get(p).setStyle("-fx-translate-x: 675px;-fx-translate-y: 488px;-fx-text-fill: green;-fx-alignment: center;-fx-font-size: 20px;");
        }
        pane.getChildren().add(arrayLabel.get(p));
    }
    //Initializing the first pawn to play with the possible moves and the ghost pawns
    if(mode == Mode.INITIAL){
        pawn = pawnArrayList.get(0);
    }
    possibleCoordinate = findPossibleCase(pawn);
    addGhostPawn(possibleCoordinate, pawn);
}

    /**
     *  Calculate all the possible moves for the current pawn
     * @param pawn current pawn
     * @return ArrayList of possible moves of the current pawn
     */
    public ArrayList<Coordinate> findPossibleCase(Pawn pawn) {
        ArrayList<Coordinate> positions = new ArrayList<>();
        for (int i = pawn.getX() - 1; i < pawn.getX() + 2; i++) //Basic movements
        {
            if(
                    i != pawn.getX() //We're not on the pawn's tile
                            && i >= 0 //We don't go out of the board on left
                            && i < 9 //nor on the right
            ) // TLDR; If we're not on the pawn's tile and inside the board (Horizontal treatment)
            {
                if ( !( //Check on the left and right
                        (
                                i < pawn.getX() //We're on the left
                                        && (
                                        (
                                                pawn.getY() > 0 //We're not on the highest tile
                                                        && inter[(i)][(-pawn.getY() + 8)].getVertical()//Check the top right intersection
                                        )
                                                ||
                                                (
                                                        pawn.getY() < 8 && inter[(i)][(-pawn.getY() + 7)].getVertical()//Check the bottom right intersection
                                                )
                                )
                        )
                                ||
                                (
                                        i > pawn.getX() //We're on the right

                                                && (
                                                (
                                                        pawn.getY() > 0 //We're not on the highest tile
                                                                && inter[(i - 1)][(-pawn.getY() +8)].getVertical()//Check the top left intersection
                                                )
                                                        ||
                                                        (
                                                                pawn.getY() < 8
                                                                        && inter[(i -1)][(-pawn.getY() +7)].getVertical()//Check the bottom left intersection
                                                        )
                                        )
                                )
                )
                )// TLDR; There is no barrier vertically directly next to the pawn
                {
                    if (getPawn(getStackPane(i,pawn.getY()))!= null)//there's another pawn on the left or right of the center tile
                    {
                        if (
                                i < pawn.getX() //We're on the left
                                        && pawn.getX() > 1 //We don't check left too far
                                        &&(
                                        (
                                                pawn.getY() > 0 //We're not at the top of the board
                                                        && inter[(i -1)][(-pawn.getY() + 8)].getVertical()//Check the barrier top left of detected pawn
                                        )
                                                ||
                                                (
                                                        pawn.getY() < 8 //We're not at the bottom of the board
                                                                && inter[(i -1)][(-pawn.getY() + 7)].getVertical() //Check the barrier bottom left of detected pawn
                                                )
                                )
                        )// TLDR; There is a barrier on the left of detected pawn
                        {
                            if (
                                    pawn.getY() - 1 > 0 //We're not going outside the top of the board
                                            && getPawn(getStackPane((i),(pawn.getY()-1)))==null//There is no pawn on the top left diagonal
                                            &&(
                                            !(
                                                    inter[(i)][(-pawn.getY()+8)].getHorizontal() //There is no horizontal barrier top right of detected pawn
                                                            ||
                                                            inter[(i-1)][(-pawn.getY()+8)].getHorizontal() //There is no horizontal barrier top left of detected pawn
                                            )
                                    )
                            )
                            {positions.add(new Coordinate(i, (pawn.getY() - 1)));} // TLDR; still inside the board and not on another pawn then go diagonally up
                            if (
                                    pawn.getY() + 1 < 8 //We're not going outside the bottom of the board
                                            && getPawn(getStackPane((i),(pawn.getY()+1)))==null//There is no pawn on the bottom left diagonal
                                            &&(
                                            !(
                                                    inter[(i)][(-pawn.getY()+7)].getHorizontal() //There is no horizontal barrier bottom right of detected pawn
                                                            ||
                                                            inter[(i-1)][(-pawn.getY()+7)].getHorizontal() //There is no horizontal barrier bottom left of detected pawn
                                            )
                                    )
                            )
                            {positions.add(new Coordinate(i, (pawn.getY() + 1)));} // or go diagonally down
                        }
                        else if (
                                i > pawn.getX() //We're on the right
                                        && pawn.getX() < 7 //We don't check right too far
                                        && (
                                        (
                                                pawn.getY() > 0 //We don't check top right too far
                                                        && inter[(i)][(-pawn.getY() + 8)].getVertical()//Check for top right barrier of detected pawn
                                        )
                                                ||
                                                (
                                                        pawn.getY() < 8 //We don't check bottom right too far
                                                                && inter[(i)][(-pawn.getY() + 7)].getVertical()//Check for top right barrier of detected pawn
                                                )
                                )
                        )// TLDR; There is a barrier on the right of the detected pawn
                        {
                            if (
                                    pawn.getY() -1 > 0 //We're not going outside the top of the board
                                            && getPawn(getStackPane(i,(pawn.getY()-1)))==null//board[(-pawn.getY()+4-1)][(i+4)].getPawn() == null//There is no pawn on the top right diagonal
                                            &&(
                                            !(
                                                    inter[(i)][(-pawn.getY()+8)].getHorizontal() //There is no horizontal barrier top right of detected pawn
                                                            ||
                                                            inter[(pawn.getX())][(-pawn.getY()+8)].getHorizontal() //There is no horizontal barrier top left of detected pawn
                                            )
                                    )
                            )
                            {positions.add(new Coordinate(i, (pawn.getY() - 1)));} //still inside the board and not on another pawn then go diagonally up
                            if (
                                    pawn.getY() + 1 < 8//We're not going outside the bottom of the board
                                            && getPawn(getStackPane(i,(pawn.getY()+1)))==null//There is no pawn on the bottom right diagonal
                                            &&(
                                            !(
                                                    inter[(i)][(-pawn.getY()+7)].getHorizontal() //There is no horizontal barrier bottom right of detected pawn
                                                            ||
                                                            inter[(i-1)][(-pawn.getY()+7)].getHorizontal() //There is no horizontal barrier bottom left of detected pawn
                                            )
                                    )
                            )
                            {positions.add(new Coordinate(i, (pawn.getY() + 1)));} //still inside the board and not on another pawn then go diagonally down
                        }else//There is no barrier
                        {
                            if (
                                    i < pawn.getX() //We're on the left
                                            && pawn.getX() > 1 //We're not going to go outside the board when we jump
                                            && getPawn(getStackPane(i-1,pawn.getY()))==null//There isn't another pawn when we jump over the detected one
                            )// TLDR; jump over the detected pawn on the left
                            {positions.add(new Coordinate(i - 1, pawn.getY()));}
                            else if (
                                    i > pawn.getX()//We're on the right
                                            && pawn.getX() < 7 //We're not going to go outside the board when we jump
                                            && getPawn(getStackPane(i+1,pawn.getY()))==null//There isn't another pawn when we jump over the detected one
                            )// TLDR; jump over the detected pawn on the right
                            {positions.add(new Coordinate(i + 1, pawn.getY()));}
                        }// TLDR; jump lateraly over adjacent pawns
                    } else //If there's no adjacent pawn nor any adjacent barrier
                    {
                        positions.add(new Coordinate(i, pawn.getY()));//Move right of left
                    }// TLDR; Do the basic movements
                }
            } else if (
                    i >= 0
                            && i < 9
            ) // TLDR; if we're inside the rows of the board and on the pawn's X tile
            {
                for (int j = pawn.getY() - 1; j < pawn.getY() + 2; j += 2) //check above and below the pawn
                {
                    if (
                            j >= 0
                                    && j < 9
                    )// TLDR; We're inside the columns of the board(Vertical treatment)
                    {
                        if ( !(
                                (
                                        j < pawn.getY()//We're above
//                                            && j > 0 //we're not on the highest tile
                                                && (
                                                (
                                                        i < 8//We don't check too far right
                                                                && inter[(i)][(-j+7)].getHorizontal() //Check top right barrier
                                                )
                                                        ||
                                                        (
                                                                i > 0 //We don't check too far left
                                                                        && inter[(i-1)][(-j+7)].getHorizontal() //Check top left barrier
                                                        )
                                        )
                                )
                                        ||
                                        (
                                                j > pawn.getY()//We're below
                                                        && (
                                                        (
                                                                i < 8 //We don't check too far right
                                                                        && inter[(i)][(-j+8)].getHorizontal() //Check bottom right barrier
                                                        )
                                                                ||
                                                                (
                                                                        i > 0 //We don't check too far left
                                                                                && inter[(i -1)][(-j + 8)].getHorizontal() //Check bottom left barrier
                                                                )
                                                )
                                        )
                        )
                        )// TLDR; There's no barrier horizontally directly next to the pawn
                        {
                            if (
                                    getPawn(getStackPane(i,j)) != null
                            ) //if there's a pawn above or below the center tile
                            {
                                if (
                                        j > pawn.getY() //We're below
                                                && j < 8
                                                && (
                                                (
                                                        i < 8 //We're not at the right-most column
                                                                && inter[(i)][(-j + 7)].getHorizontal() //Check bottom right barrier of detected pawn
                                                )
                                                        ||
                                                        (
                                                                i > 0 //We're not at the left-most column
                                                                        && inter[(i -1)][(-j + 7)].getHorizontal() //Check bottom left barrier of detected pawn
                                                        )
                                        )

                                )// TLDR; There is a barrier at the bottom of the detected pawn
                                {
                                    if (
                                            i + 1 <= 8 //Jump is still inside the board
                                                    && getPawn(getStackPane(i+1,j))==null//There isn't another pawn on the jump tile
                                                    &&(
                                                    !(
                                                            inter[(i)][(-j+8)].getVertical() //There is no horizontal barrier top right of detected pawn
                                                                    ||
                                                                    inter[(i)][(-j+7)].getVertical() //There is no horizontal barrier bottom right of detected pawn
                                                    )
                                            )
                                    )
                                    {positions.add(new Coordinate(i + 1, j));} //still inside the board and not on another pawn then go diagonally bottom right
                                    if (
                                            i - 1 >= 0 //Jump is still inside the board
                                                    && getPawn(getStackPane(i-1,j))==null//There isn't another pawn on the jump tile
                                                    &&(
                                                    !(
                                                            inter[(i-1)][(-j+8)].getVertical() //There is no horizontal barrier top left of detected pawn
                                                                    ||
                                                                    inter[(i-1)][(-j+7)].getVertical() //There is no horizontal barrier bottom left of detected pawn
                                                    )
                                            )
                                    )
                                    {positions.add(new Coordinate(i - 1, j));} //still inside the board and not on another pawn then go diagonally bottom left
                                } else if (
                                        j < pawn.getY()//We're on top
                                                && j > 0 //We're not at the highest row of the board
                                                &&(
                                                (
                                                        i < 8 //We're not at the right-most column
                                                                && inter[(i)][(-j + 8)].getHorizontal() //Check top right barrier
                                                )
                                                        ||
                                                        (
                                                                i > 0 //We're not at the left-most column
                                                                        && inter[(i -1)][(-j + 8)].getHorizontal() //Check top left barrier
                                                        )
                                        )

                                )// TLDR; There is a barrier on top of the detected pawn
                                {
                                    if (
                                            i + 1 <= 8
                                                    && getPawn(getStackPane(i+1,j))==null
                                                    &&(
                                                    !(
                                                            inter[(i)][(-j+8)].getVertical() //There is no horizontal barrier top right of detected pawn
                                                                    ||
                                                                    inter[(i)][(-j+7)].getVertical() //There is no horizontal barrier bottom right of detected pawn
                                                    )
                                            )
                                    )
                                    {positions.add(new Coordinate(i + 1, j));} //still inside the board and not on another pawn then go diagonally top right
                                    if (
                                            i - 1 >= 0
                                                    && getPawn(getStackPane(i-1,j))==null
                                                    &&(
                                                    !(
                                                            inter[(i-1)][(-j+8)].getVertical() //There is no horizontal barrier top left of detected pawn
                                                                    ||
                                                                    inter[(i-1)][(-j+7)].getVertical() //There is no horizontal barrier bottom left of detected pawn
                                                    )
                                            )
                                    )
                                    {positions.add(new Coordinate(i - 1, j));} //still inside the board and not on another pawn then go diagonally top left
                                } else//There is no barrier
                                {
                                    if (
                                            j > pawn.getY() //We're at the bottom
                                                    && j<8
                                                    && getPawn(getStackPane(i,j+1))==null//There is no pawn after the detected pawn
                                    )
                                    {positions.add(new Coordinate(i, j + 1));}//if detected pawn is at the bottom go one down one more time, if still inside the board
                                    if (
                                            j < pawn.getY() //We're on top
                                                    && j>0
                                                    && getPawn(getStackPane(i,j-1))==null//There is no pawn after the detected pawn
                                    )
                                    {positions.add(new Coordinate(i, j - 1));}// same but on top
                                }
                            } else //There is no adjacent pawn
                            {
                                positions.add(new Coordinate(i, j));
                            }// TLDR; Basic movements
                        }
                    }
                }
            }
        }
        return positions;
    }

    //get a stackPane corresponding to coordinates

    /**
     * Get the targeted stackpane from the boardGame
     * @param col int
     * @param row int
     * @return Stackpane at the coordinate (col,row)
     */
    public StackPane getStackPane(int col, int row){
        for(Node node : boardGame.getChildren()) {
            //if the pane's children corresponds to what we're searching for and if it is a Case : return it
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col) {
                if(node instanceof StackPane){
                    return (StackPane) node;
                }
            }
        }
        return null;
    }

    //get a pawn from a stack pane

    /**
     * Get if there's any pawn at a given cell
     * @param stack targeted stackpane
     * @return The found pawn or null if there's no pawn at the targeted stackpane
     */
    public Pawn getPawn(StackPane stack){
        for(Node node : stack.getChildren()){
            if(node instanceof Circle circle){
                for(Pawn p : pawnArrayList){
                    if(p.getCircle().getFill().equals(circle.getFill())){
                        return p;
                    }
                }
            }
        }
        return null;
    }

    //initialize pawns at the beginning of the game

    /**
     * Draw the initialized pawns on the board
     */
    public void initialUpdateCase(){
        for(Pawn p : pawnArrayList){
            StackPane stack = getStackPane(p.getX(),p.getY());
            stack.getChildren().add(p.getCircle());
            stack.setAlignment(Pos.CENTER);
        }
    }
    //delete previous pawn that has been replaced + delete ghost pawns

    /**
     * Remove the ghost pawns and the previous pawn circle, then draw it on the selected position
     * @param tmp current pawn
     */
    public void updateCase(Pawn tmp){
        //delete old circle (because we modify the structure of the list of the StackPane (and it's a collection), we have to use an iterator)
        // use the iterator to suppress an element
        getStackPane(tmp.getX(), tmp.getY()).getChildren().removeIf(node -> node instanceof Circle);
        //add the new circle
        //we force the change of color of tmp, or it will take the color of the previous Pawn
        tmp.getCircle().setFill(pawn.getColor());
        getStackPane(pawn.getX(), pawn.getY()).getChildren().add(tmp.getCircle());
        //delete ghostPawns (we have to use an iterator again)
        deleteGhostPawns();

    }
    //method create in order to avoid dijkstra's algorithm to take into account ghost pawns

    /**
     * Delete all ghost pawns from the board
     */
    public void deleteGhostPawns(){
        for (Node node : boardGame.getChildren()) {
            if (node instanceof StackPane stack) {
                Iterator<Node> stackIterator = stack.getChildren().iterator();
                while (stackIterator.hasNext()) {
                    Node node2 = stackIterator.next();
                    if (node2 instanceof Circle circle) {
                        if (circle.getOpacity() == 0.5) {
                            stackIterator.remove();
                        }
                    }
                }
            }
        }

    }
    //disables all the unnecesary buttons when starting a game

    /**
     * Disable the starting game elements and show the current game buttons.
     */
    public void setDisable(){
        //delete the labels and slider from initialization
        assistNbLabel.setVisible(false);
        nbLabel.setVisible(false);
        slider.setVisible(false);
        buttonValidateGame.setVisible(false);
        assistNbLabel.setDisable(true);
        nbLabel.setDisable(true);
        slider.setDisable(true);
        buttonValidateGame.setDisable(true);
        buttonPutBarrier.setVisible(true);
        File file = new File("game.save");
        if(file.exists()){
            buttonLoadGame.setVisible(true);
        }
        buttonSaveGame.setVisible(true);
    }

    //translucent pawns indicating the possible positions to play

    /**
     * Add a translucent pawn indicating the possible moves for the current player
     * @param cord List of coordinates
     * @param p current pawn
     */
    public void addGhostPawn(List<Coordinate> cord, Pawn p){
        for (Coordinate c : cord){
            for(Node node : boardGame.getChildren()){
                if(GridPane.getRowIndex(node) == c.getY() && GridPane.getColumnIndex(node) == c.getX()){
                    Circle circle = new Circle(20);
                    circle.setStyle("-fx-opacity: 0.5;-fx-alignment: center");
                    //to avoid blocking the way of the button behind the circle
                    circle.setMouseTransparent(true);
                    circle.setFill(p.getColor());


                    ((StackPane) node).getChildren().add(circle);

                    ((StackPane) node).setAlignment(Pos.CENTER);
                    break;
                }
            }
        }
    }

    //detect victory of a pawn

    /**
     * Check if ther's a pawn that has arrived to the opposite side of the board
     * @return true if a pawn is located at the oposite side of its starting location
     */
    public boolean findWinner(){
        if(pawn.getNumPawn() == 1 || pawn.getNumPawn() ==2){
            return pawn.getY() == pawn.getVict();
        } else {
            return pawn.getX() == pawn.getVict();
        }

    }

    /**
     * Disable all interface elements
     */
    public void disableAll(){
        //disable all buttons on the board
        for (Node node : boardGame.getChildren()){
            if(node instanceof StackPane stack){
                for (Node nodeStack : stack.getChildren()){
                    if(nodeStack instanceof Button b){
                        b.setDisable(true);
                    }
                }
            }
        }
        //disable all stack pane which where into intersect
        for (int i=0;i<8;i++){
            for(int j=0;j<8;j++){
                inter[i][j].getStack().setDisable(true);
            }
        }
    }

    /**
     * Add a label indicating the player who has won the game
     */
    public void addLayoutWinner(){
        //layout for the winner
        Label winnerLabel = new Label("Le joueur numéro "+pawn.getNumPawn() + " a gagné la partie !");
        winnerLabel.setStyle("-fx-font-size: 25px; -fx-text-fill: green;-fx-background: none; -fx-text-decoration: underline;-fx-text-alignment: center;");
        pane.getChildren().add(winnerLabel);
        winnerLabel.setPrefWidth(450);winnerLabel.setPrefHeight(40);winnerLabel.setLayoutX(170);winnerLabel.setLayoutY(150);
    }

    /**
     * Move a pawn to a selected position
     * @param event mouse event
     */
    public void movePawn(ActionEvent event) {

        for (Pawn p : pawnArrayList)
        //at the initialization we can't delete the ghostPawn of previous pawn because there is none, so we have a firstRound variable
        firstRound = false;
        //Button on which we detected the click
        Button clickedButton = (Button) event.getSource();
        //Parent of the button
        Parent parent = clickedButton.getParent();
        //which is the stack Pane
        StackPane stack = (StackPane) parent;
        //coordinate of the stackPane
        Coordinate cord = new Coordinate(GridPane.getColumnIndex(stack),GridPane.getRowIndex(stack));
        //boolean to see if it's a good move or not
        boolean checkPoint = false;

        try{
            //if coordinate clicked matches with a coordinates of the list of possibleCoordinate, then we move the pawn
            for(Coordinate i : possibleCoordinate){
                if(cord.equals(i)){
                    //previous pawn (we need it for updateCase)
                    Pawn tmp = new Pawn(pawn.getX(),pawn.getY(),99);
                    tmp.setColor(pawn.getColor());
                    board[tmp.getY()][tmp.getX()].setPawn(null);
                    pawn.setX(cord.getX());pawn.setY(cord.getY());
                    board[pawn.getY()][pawn.getX()].setPawn(pawn);
                    updateCase(tmp);
                    //if a pawn win we disable all button and intersect and display the winner's layout
                    if(findWinner()){
                        disableAll();
                        addLayoutWinner();
                        break;
                    }
                    nextPawnAndCord();
                    checkPoint = true;
                    break;
                }
            }
            //verify for each coordinates if it matches the coordinates of the case we clicked on, if not checkpoint = false, and we throw an exception
            if(!checkPoint){
                throw new MoveException();
            }
        } catch (MoveException e){
            //if we have a winner we throw an exception with a green color to shows that we won
            if(findWinner()){
                e.animateStackPane(stack,clickedButton, Color.GREEN);
            } else {
                e.animateStackPane(stack, clickedButton, Color.RED);
            }
        }
    }

    //Method to update the board of the new position of each pawn and make appear the ghostPawns for the nextPlayer

    /**
     * Show the next player's possible moves
     */
    public void nextPawnAndCord(){
        updateCase(pawnArrayList.get(index));
        index = (index + 1) % pawnArrayList.size(); //Change player's turn
        pawn = pawnArrayList.get(index); //select pawn to play
        possibleCoordinate = findPossibleCase(pawn);
        addGhostPawn(possibleCoordinate, pawn);
    }
    // remove the pawns from the current game in order to load thos from game.save

    /**
     * Clear a list of pawns
     * @param pL List of Pawns
     */
    public void emptyPawnList(List<Pawn> pL){
        for (int indexItem=0;indexItem< pL.size();indexItem++){
            getStackPane(pL.get(indexItem).getX(),pL.get(indexItem).getY()).getChildren().remove(1);
            pL.set(indexItem,null);
        }
    }
    // remove all pawns from the board (ghost pawns included)

    /**
     * Remove all the pawns located on the board
     */
    public void clearPawns(){
        for (Node n : boardGame.getChildren()){
            StackPane stack = (StackPane) n;
            if(stack.getChildren().size()>1){
                Circle c = (Circle) stack.getChildren().get(1);
                stack.getChildren().remove(c);
            }
        }
    }
    // update the board and intersect matrix with the bytecode read from game.save

    /**
     * Set the board, intersect and nbPlayers parameters to the loaded bytecode from the previously saved game
     */
    public void refreshGame(){
        // get the number of players from the saved game
        nbPlayers = pawnArrayList.size();

        //refreshing the board
        for( int row =0 ; row <9;row++){
            for (int column =0 ; column<9; column++) {
                    boardGame.getChildren().get(column+9*row).setUserData(board[row][column]);
            }
        }
        //refreshing the intersections


        for(int i=81;i<145;i++){
            if (((StackPane)boardGame.getChildren().get(i)).getChildren().size()>0){
                ((StackPane)boardGame.getChildren().get(i)).getChildren().remove(0);
            }
            int x = ((Intersect) boardGame.getChildren().get(i).getUserData()).getCoordinates().getX();
            int y = ((Intersect) boardGame.getChildren().get(i).getUserData()).getCoordinates().getY();
            boardGame.getChildren().get(i).setUserData(inter[x][y]);
            inter[x][y].setStack((StackPane) boardGame.getChildren().get(i));
            if(inter[x][y].getHorizontal()){
                inter[x][y].setHorizontal(false);
                putBarrier(x,y,Orientation.HORIZONTAL);
            }
            else if(inter[x][y].getVertical()){
                inter[x][y].setVertical(false);
                putBarrier(x,y,Orientation.VERTICAL);
            }
        }

    }
}

