package M;

import M.Interfaces.IShape;
import M.Shapes.PCircle;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color; // java.awt.Color

import java.util.Map;

public class Shape implements java.io.Serializable {


    private IShape currentState;

    private String name;

    private boolean isChanged;
    private static double oldX;
    private static double oldY;

    // initalize all these vars
    public Shape () { this.currentState = new PCircle(10, 20, 30, 40, 50, 60); } // error

    public IShape getCurrentState() {
        return currentState;
    }

    public void setCurrentState(IShape currentState) {
        this.currentState = currentState;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChanged() {
        return isChanged;
    }

    public void setChanged(boolean changed) {
        isChanged = changed;
    }

    public double getOldX() {
        return oldX;
    }

    public void setOldX(double oldX) {
        this.oldX = oldX;
    }

    public double getOldY() {
        return oldY;
    }

    public void setOldY(double oldY) {
        this.oldY = oldY;
    }

    public Shape(IShape currentState) {
        this.currentState = currentState;
    }

    /*************************************************/

    public void drag(Point2D newPosition,Object canvas){

        erase(canvas);
        setPosition(newPosition);
        draw(canvas);
    }

    public void resize(Object canvas, double width, double height) {

        if (currentState instanceof PCircle)
        {
            erase(canvas);
            getProperties().replace("width", width);
            getProperties().replace("height",height);
            draw(canvas);
        }

        /*

        //for other shapes
        if (currentState instanceof PCircle)
        {
            erase(canvas);
            getProperties().replace("width", width);
            getProperties().replace("height",height);
            draw(canvas);
        }

        */
    }

    /*************************************************/

    //region position

    public void setPosition(Point2D position) {
        currentState.setPosition(position);
    }

    public Point2D getPosition() {
        return currentState.getPosition();
    }

    //endregion

    /*************************************************/

    //region properties

    public void setProperties(Map<String, Double> properties) {
        currentState.setProperties(properties);
    }

    public Map<String, Double> getProperties() {
        return currentState.getProperties();
    }

    //endregion

    /*************************************************/

    //region color

    public void setBackColor(Color color) {
        currentState.setBackColor(color);
    }
    public Color getBackColor() {
        return currentState.getBackColor();
    }

    public void setStrokeColor(Color color) {
        currentState.setStrokeColor(color);
    }
    public Color getStrokeColor() {
        return currentState.getStrokeColor();
    }

    //endregion

    /*************************************************/

    public void draw(Object canvas) {
        currentState.draw(canvas);
    }

    public void erase(Object canvas){
        currentState.erase(canvas);
    }

    /*************************************************/

    public Object clone() throws CloneNotSupportedException {
        return currentState.clone();
    }

    /*************************************************/

    public IShape getState() {
        return currentState;
    }

    public void setState(IShape currentState) {
        this.currentState = currentState;
    }
}
