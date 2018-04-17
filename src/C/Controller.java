package C;

import M.Shape;
import M.Shapes.PCircle;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;


import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.net.URL;
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

    public Slider MyOpacitySlider;
    public Slider MyStrokeSlider;


    private double x, y;

    private Shape shape;

    /*******************************************************/

    //region canvas events

    public void MyCanvas_Drag(MouseEvent mouseEvent) {

        x = mouseEvent.getX();
        y = mouseEvent.getY();


        if (MyHandRadioBTN.isSelected()) {

            MyCanvas.setCursor(Cursor.CLOSED_HAND);


            if(shape.getState() instanceof PCircle)
                shape.drag(new Point2D(
                        x - shape.getProperties().get("width") / 2,
                        y - shape.getProperties().get("height") / 2), MyCanvas);

        }

        if(MySizeRadioBTN.isSelected())
        {
            if(shape.getState() instanceof PCircle)
            {

                double width = (x - shape.getOldX()) * 2;
                double height = (x - shape.getOldX()) * 2;

                shape.getProperties().replace("x",  shape.getOldX() - width / 2);
                shape.getProperties().replace("y",  shape.getOldX() - width / 2);
                shape.resize(MyCanvas, width, height);
            }
        }

        if (MyMouseRadioBTN.isSelected()){

            if(shape.getState() instanceof PCircle)
            {

                double width = (x - shape.getOldX()) * 2;
                double height = (x - shape.getOldX()) * 2;

                shape.getProperties().replace("x", shape.getOldY() - width / 2);
                shape.getProperties().replace("y", shape.getOldY() - width / 2);
                shape.resize(MyCanvas, width, height);
            }
        }

        /*

        for other shapes this code work
        if (MyMouseRadioBTN.isSelected()){

            double width = (x - oldX);
            double height = (y - oldY);

            shape.resize(MyCanvas, width, height);
        }
        */
    }


    public void MyCanvas_Press(MouseEvent mouseEvent) {

        x = mouseEvent.getX();
        y = mouseEvent.getY();

        if (MyMouseRadioBTN.isSelected())
        {
            // For circle

            shape = new Shape(new PCircle(x, y, 0, 0, MyOpacitySlider.getValue(), MyStrokeSlider.getValue()));

            shape.setOldX(x);
            shape.setOldY(y);

            shape.setBackColor(MyBackColorPicker.getValue());
            shape.setStrokeColor(MyStrokeColorPicker.getValue());

            shape.draw(MyCanvas);
        }


        if (MyHandRadioBTN.isSelected())
        {

            MyCanvas.setCursor(Cursor.CLOSED_HAND);


            if(shape.getState() instanceof PCircle)
                shape.drag(new Point2D(
                        x - shape.getProperties().get("width") / 2,
                        y - shape.getProperties().get("height") / 2), MyCanvas);


        }

        //(discuss add it or not)working with layers of canvases will make us ignore refresh method
        // & abitity to put layer on layer
        // & processor
        // & grouping shapes
        // (Discuss the code of resizing circle) like other shapes or special case
        // this structure of shape properties will make it easy for all to load and save data
        // we can use refresh to move layer on layer
        // rember to make the clear rectangle according to the final size of the program
        // why not make circle and ellipse one class (programatically)
        // set proper logic behavior between draw, drag, resize controls
        // if clicked without drag show drow from with options
        // better than adding item to the end of the list , add it after selected item

    }

    public void MyCanvas_Release(MouseEvent mouseEvent) {

        // boolean var if changes happened

        x = mouseEvent.getX();
        y = mouseEvent.getY();




        if (MyHandRadioBTN.isSelected())
        {
            shape.setOldX(x);
            shape.setOldY(y);

            MyCanvas.setCursor(Cursor.DEFAULT);
        }

        if(MySizeRadioBTN.isSelected()){

        }

        if(MyMouseRadioBTN.isSelected()){

        }

        XMLEncoder encoder = null;
        try {
            encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream("out.xml")));
        } catch (Exception ex) {
            System.out.println("Exception!");
        }
        if (shape != null) {
            System.out.println("Shape is not null!");
            encoder.writeObject(shape);
        }
        else System.out.println("Shape is null!");
        encoder.close();
    }

    //endregion

    /*******************************************************/


    public void Toggle_Click(MouseEvent mouseEvent){

        if (mouseEvent.getSource() == CircleBTN){
            CircleBTN.setSelected(true);
            EllipseBTN.setSelected(false);
            RectangleBTN.setSelected(false);
            PolygonBTN.setSelected(false);
        }
        if (mouseEvent.getSource() == EllipseBTN){
            CircleBTN.setSelected(false);
            EllipseBTN.setSelected(true);
            RectangleBTN.setSelected(false);
            PolygonBTN.setSelected(false);
        }
        if (mouseEvent.getSource() == RectangleBTN){
            CircleBTN.setSelected(false);
            EllipseBTN.setSelected(false);
            RectangleBTN.setSelected(true);
            PolygonBTN.setSelected(false);
        }
        if (mouseEvent.getSource() == PolygonBTN){
            CircleBTN.setSelected(false);
            EllipseBTN.setSelected(false);
            RectangleBTN.setSelected(false);
            PolygonBTN.setSelected(true);
        }


        MyMouseRadioBTN.setSelected(true);
    }

    /*******************************************************/



    public void Slider_Release(MouseEvent mouseEvent) {

        // make sure that shape is instanciated

        // put change color in color pickers
        shape.setBackColor(MyBackColorPicker.getValue());
        shape.setStrokeColor(MyStrokeColorPicker.getValue());

        if(mouseEvent.getSource() == MyOpacitySlider){

            shape.getProperties().replace("opacity", MyOpacitySlider.getValue());
            shape.erase(MyCanvas);
            shape.draw(MyCanvas);
        }
        if(mouseEvent.getSource() == MyStrokeSlider){
            shape.getProperties().replace("strokewidth",MyStrokeSlider.getValue());
            shape.erase(MyCanvas);
            shape.draw(MyCanvas);
        }
    }

    /*******************************************************/

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        MyMouseRadioBTN.setSelected(true);

        //select first item in list
    }




    /*******************************************************/
}