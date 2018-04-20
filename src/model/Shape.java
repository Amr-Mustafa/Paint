package model;

import model.Shapes.IShape;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

import java.util.Map;

public class Shape {

    private Application application;

    private IShape currentState;
    public String name;

    public boolean isChanged;

    public double oldX;
    public double oldY;

    // initalize all these vars

    public Shape(IShape currentState) {
        this.currentState = currentState;
        this.application  = Application.getInstance();
    }

    /*************************************************/

    public void drag(Point2D newPosition,Object canvas){

        erase(canvas);
        setPosition(newPosition);

    }

    public void resize(Object canvas, double width, double height) {

        erase(canvas);
        getProperties().replace("width", width);
        getProperties().replace("height",height);

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

    public void remove() {

        /* 1. Remove the shape from shapes list. */
        application.getShapes().remove(this);


    }
}
