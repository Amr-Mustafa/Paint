package controller;

import model.Application;
import model.Shape;
import model.Shapes.*;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public Canvas MyCanvas;
    public ColorPicker MyBackColorPicker;
    public ColorPicker MyStrokeColorPicker;

    public RadioButton MyHandRadioBTN;
    public RadioButton MySizeRadioBTN;
    public RadioButton MyMouseRadioBTN;

    public ToggleButton CircleBTN;
    public ToggleButton EllipseBTN;
    public ToggleButton RectangleBTN;
    public ToggleButton PolygonBTN;
    public ToggleButton SquareBTN;
    public ToggleButton LineBTN;
    public ToggleButton TriangleBTN;

    public Slider MyOpacitySlider;
    public Slider MyStrokeSlider;

    Application application = Application.getInstance();
    public TreeView <String> MyTreeView;

    private TreeItem<String> root = new TreeItem<String>("Paint");
    private List<Shape> shapesList = new ArrayList<>();

    private int []counter = new int[8];

    private double x, y;

    private Shape selectedShape = null;
    //private Shape shape;

    private void resizeShape(){

        double width,height;

        if(selectedShape.getState() instanceof PCircle || selectedShape.getState() instanceof PSquare)
        {
            width = (x - selectedShape.oldX) * 2;
            height = (x - selectedShape.oldX) * 2;

            selectedShape.getProperties().replace("x", selectedShape.oldX - width / 2);
            selectedShape.getProperties().replace("y", selectedShape.oldY - width / 2);
            selectedShape.resize(MyCanvas, width, height);
        }

        if (selectedShape.getState() instanceof PEllipse || selectedShape.getState() instanceof PRectangle)
        {

            width = (x - selectedShape.getProperties().get("x"));
            height = (y - selectedShape.getProperties().get("y"));

            selectedShape.resize(MyCanvas, width, height);
        }
        if(selectedShape.getState() instanceof PTriangle
                || selectedShape.getState() instanceof PHexagon
                || selectedShape.getState() instanceof PPentagon)
        {

            width = (x - selectedShape.oldX)*2;
            height = (y - selectedShape.oldY)*2;

            selectedShape.getProperties().replace("x", selectedShape.oldX+height/2);
            selectedShape.getProperties().replace("y", selectedShape.oldY+height/2);
            selectedShape.resize(MyCanvas, width, height);
        }
        if(selectedShape.getState() instanceof PLine)
        {
            selectedShape.getProperties().replace("x2", x);
            selectedShape.getProperties().replace("y2", y);
            selectedShape.erase(MyCanvas);
            selectedShape.draw(MyCanvas);
        }
    }

    public void MyCanvas_Drag(MouseEvent mouseEvent) {

        x = mouseEvent.getX();
        y = mouseEvent.getY();

        if (selectedShape != null)
        {
            String selectedShapeName = MyTreeView.getSelectionModel().getSelectedItem().getValue();

            for (Shape current : shapesList)
            {
                if (current.name.trim().equals(selectedShapeName))
                {
                    selectedShape = current;
                    break;
                }
            }

            if (MyHandRadioBTN.isSelected())
            {
                MyCanvas.setCursor(Cursor.CLOSED_HAND);

                if(selectedShape.getState() instanceof PTriangle
                        || selectedShape.getState() instanceof PPentagon
                        || selectedShape.getState() instanceof PHexagon)

                    selectedShape.drag(new Point2D(
                            x - selectedShape.getProperties().get("width") / 10,
                            y - selectedShape.getProperties().get("height") / 10), MyCanvas);

                if(selectedShape.getState() instanceof PLine)
                    selectedShape.drag(new Point2D(
                            x - selectedShape.getProperties().get("x2") / 2,
                            y - selectedShape.getProperties().get("y2") / 2), MyCanvas);

                else
                    selectedShape.drag(new Point2D(
                            x - selectedShape.getProperties().get("width") / 2,
                            y - selectedShape.getProperties().get("height") / 2), MyCanvas);
            }

            if(MySizeRadioBTN.isSelected() || MyMouseRadioBTN.isSelected()) {

                assert selectedShape != null;
                resizeShape();
            }

            for (Shape current : shapesList) current.draw(MyCanvas);
        }
    }

    public void MyCanvas_Press(MouseEvent mouseEvent) {

        x = mouseEvent.getX();
        y = mouseEvent.getY();


        if (MyMouseRadioBTN.isSelected())
        {
            Shape newShape = null;

            int index = -1;

            if(CircleBTN.isSelected()){
                newShape = new Shape(new PCircle(x, y, 0, 0, MyOpacitySlider.getValue(),
                        MyStrokeSlider.getValue()));

                index = 0;
            }

            if(EllipseBTN.isSelected()){
                newShape = new Shape(new PEllipse(x, y, 0, 0, MyOpacitySlider.getValue(),
                        MyStrokeSlider.getValue()));

                index = 1;
            }

            if(RectangleBTN.isSelected()){
                newShape = new Shape(new PRectangle(x, y, 0, 0, MyOpacitySlider.getValue(),
                        MyStrokeSlider.getValue()));

                index = 2;
            }

            if(SquareBTN.isSelected()){
                newShape = new Shape(new PSquare(x, y, 0, 0, MyOpacitySlider.getValue(),
                        MyStrokeSlider.getValue()));

                index = 3;
            }

            if(LineBTN.isSelected()){
                newShape = new Shape(new PLine(x, y, 0, 0, MyOpacitySlider.getValue(),
                        MyStrokeSlider.getValue()));

                index = 4;
            }

            if(PolygonBTN.isSelected()){
                newShape = new Shape(new PHexagon(x, y, 0, 0, MyOpacitySlider.getValue(),
                        MyStrokeSlider.getValue()));

                index = 5;
            }

            if(TriangleBTN.isSelected()) {
                newShape = new Shape(new PTriangle(x, y, 0, 0, MyOpacitySlider.getValue(),
                        MyStrokeSlider.getValue()));

                index = 6;
            }

            assert newShape != null;

            newShape.oldX = x;
            newShape.oldY = y;

            newShape.setBackColor(MyBackColorPicker.getValue());
            newShape.setStrokeColor(MyStrokeColorPicker.getValue());

            newShape.name = newShape.getState().getClass().getName().replace("model.Shapes.P","") + " (" + counter[index]++ + ")";

            shapesList.add(newShape);

            TreeItem<String> newItem = new TreeItem<String>(newShape.name);
            root.getChildren().add(newItem);


            MultipleSelectionModel msm = MyTreeView.getSelectionModel();
            msm.select(MyTreeView.getRow(newItem));

            selectedShape = newShape;
        }
    }

    public void MyCanvas_Release(MouseEvent mouseEvent) {

        x = mouseEvent.getX();
        y = mouseEvent.getY();

        if (selectedShape != null)
        {
            if (MyHandRadioBTN.isSelected())
            {
                if (selectedShape.getState() instanceof PCircle || selectedShape.getState() instanceof PSquare)
                {
                    selectedShape.oldX = x;
                    selectedShape.oldY = y;
                }

                if (selectedShape.getState() instanceof  PEllipse
                        || selectedShape.getState() instanceof PRectangle
                        || selectedShape.getState() instanceof PLine)
                {
                    selectedShape.oldX = selectedShape.getProperties().get("x");
                    selectedShape.oldY = selectedShape.getProperties().get("y");
                }

                MyCanvas.setCursor(Cursor.DEFAULT);
            }
        }
    }

    public void Toggle_Click(MouseEvent mouseEvent){

        if (mouseEvent.getSource() == CircleBTN){
            CircleBTN.setSelected(true);
            EllipseBTN.setSelected(false);
            RectangleBTN.setSelected(false);
            PolygonBTN.setSelected(false);
            SquareBTN.setSelected(false);
            LineBTN.setSelected(false);
            TriangleBTN.setSelected(false);
        }
        if (mouseEvent.getSource() == EllipseBTN){
            CircleBTN.setSelected(false);
            EllipseBTN.setSelected(true);
            RectangleBTN.setSelected(false);
            PolygonBTN.setSelected(false);
            SquareBTN.setSelected(false);
            LineBTN.setSelected(false);
            TriangleBTN.setSelected(false);
        }
        if (mouseEvent.getSource() == RectangleBTN){
            CircleBTN.setSelected(false);
            EllipseBTN.setSelected(false);
            RectangleBTN.setSelected(true);
            PolygonBTN.setSelected(false);
            SquareBTN.setSelected(false);
            LineBTN.setSelected(false);
            TriangleBTN.setSelected(false);
        }
        if (mouseEvent.getSource() == PolygonBTN){
            CircleBTN.setSelected(false);
            EllipseBTN.setSelected(false);
            RectangleBTN.setSelected(false);
            PolygonBTN.setSelected(true);
            SquareBTN.setSelected(false);
            LineBTN.setSelected(false);
            TriangleBTN.setSelected(false);
        }
        if (mouseEvent.getSource() == SquareBTN){
            CircleBTN.setSelected(false);
            EllipseBTN.setSelected(false);
            RectangleBTN.setSelected(false);
            PolygonBTN.setSelected(false);
            SquareBTN.setSelected(true);
            LineBTN.setSelected(false);
            TriangleBTN.setSelected(false);
        }
        if (mouseEvent.getSource() == LineBTN){
            CircleBTN.setSelected(false);
            EllipseBTN.setSelected(false);
            RectangleBTN.setSelected(false);
            PolygonBTN.setSelected(false);
            SquareBTN.setSelected(false);
            LineBTN.setSelected(true);
            TriangleBTN.setSelected(false);
        }
        if (mouseEvent.getSource() == TriangleBTN){
            CircleBTN.setSelected(false);
            EllipseBTN.setSelected(false);
            RectangleBTN.setSelected(false);
            PolygonBTN.setSelected(false);
            SquareBTN.setSelected(false);
            LineBTN.setSelected(false);
            TriangleBTN.setSelected(true);
        }


        MyMouseRadioBTN.setSelected(true);
    }

    public void Slider_Release(MouseEvent mouseEvent) {

        if(selectedShape != null)
        {
            selectedShape.setBackColor(MyBackColorPicker.getValue());
            selectedShape.setStrokeColor(MyStrokeColorPicker.getValue());

            if(mouseEvent.getSource() == MyOpacitySlider){

                selectedShape.getProperties().replace("opacity", MyOpacitySlider.getValue());
                selectedShape.erase(MyCanvas);
                selectedShape.draw(MyCanvas);
            }
            if(mouseEvent.getSource() == MyStrokeSlider){
                selectedShape.getProperties().replace("strokewidth",MyStrokeSlider.getValue());
                selectedShape.erase(MyCanvas);
                selectedShape.draw(MyCanvas);
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        application.setCanvas(MyCanvas);

        MyMouseRadioBTN.setSelected(true);
        CircleBTN.setSelected(true);

        MyTreeView.setRoot(root);
        MyTreeView.setShowRoot(false);

        for (int i = 0; i < 8; i++) counter[i] = 1;

        /*
        try
        {
            PCircle circle = (PCircle) shape.clone();
            Shape shape = new Shape(circle);
            shape.setPosition(new Point2D(
                    shape.getProperties().get("x") + 10, shape.getProperties().get("y") + 10));

            shape.draw(MyCanvas);


        }
        catch (CloneNotSupportedException e)
        {
            e.printStackTrace();
        }
        */

        //select first item in list
    }

    public void Delete_Click(MouseEvent mouseEvent) {

        if (selectedShape != null)
        {
            selectedShape.erase(MyCanvas);

            // to remove from tree view
            TreeItem<String> selectedItem = MyTreeView.getSelectionModel().getSelectedItem();
            selectedItem.getParent().getChildren().remove(selectedItem);

            /* Remove selected shape from the array list. */
            application.removeShape(selectedShape);

            if (!shapesList.isEmpty()) {
                selectedShape = shapesList.get(0);

                MultipleSelectionModel msm = MyTreeView.getSelectionModel();
                msm.select(0);
            }
            else selectedShape = null;

            for (Shape current : shapesList) current.draw(MyCanvas);
        }

    }

}