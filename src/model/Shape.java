package model;

import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TreeItem;
import model.Shapes.IShape;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import model.commands.DrawCommand;
import model.commands.SetPositionCommand;
import model.commands.SetPropertiesCommand;

import java.util.HashMap;
import java.util.Map;

public class Shape {

    private Application application;

    private IShape currentState;
    public String name;

    public double oldX;
    public double oldY;

    public Shape (IShape currentState) {
        this.currentState = currentState;
        this.application  = Application.getInstance();
    }

    /**
     * Drag the shape object to the new position specified by the parameter.
     * @param newPosition
     */
    public void drag (Point2D newPosition, Object canvas){

        /* Instantiate a new setPositionCommand object with the caller's reference to act upon and the new point. */
        SetPositionCommand setPositionCommand = new SetPositionCommand(this, newPosition);

        /* Execute the request. */
        setPositionCommand.execute();

        /* Refresh the canvas to show changes. */
        application.refresh(application.getCanvas());

    }

    /**
     * Resize the shape object to the size specified by the parameters.
     * @param canvas
     * @param width
     * @param height
     */
    public void resize(Object canvas, double width, double height) {

        Map<String, Double> properties = new HashMap<>(this.getProperties());
        properties.replace("width", width);
        properties.replace("height", height);

        /* Instantiate a new setPropertiesCommand object with the caller's reference to act upon and the new map. */
        SetPropertiesCommand setPropertiesCommand = new SetPropertiesCommand(this, properties);

        /* Execute the request. */
        setPropertiesCommand.execute();

        /* Refresh the canvas to show changes. */
        application.refresh(application.getCanvas());

    }

    public void setPosition(Point2D position) {
        currentState.setPosition(position);
    }

    public Point2D getPosition() {
        return currentState.getPosition();
    }

    public void setProperties(Map<String, Double> properties) {
        currentState.setProperties(properties);
    }

    public Map<String, Double> getProperties() {
        return currentState.getProperties();
    }



    

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
       // currentState.draw(canvas);//create object drawCommand .. perform execute method on it
        DrawCommand drawCommand = new DrawCommand(currentState);
        drawCommand.execute();
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

        /* 2. Remove the shape from the tree view. */
        TreeItem<String> selectedItem = application.getTreeView().getSelectionModel().getSelectedItem();
        selectedItem.getParent().getChildren().remove(selectedItem);

    }

}
