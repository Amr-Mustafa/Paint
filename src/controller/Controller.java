package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import model.Application;
import model.Shape;
import model.Shapes.*;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import model.commands.*;
import model.save.JsonStrategy;
import model.save.XmlStrategy;

import java.io.File;
import java.net.URL;
import java.util.*;

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

    private Application application = Application.getInstance();
    public TreeView <String> MyTreeView;

    private TreeItem<String> root = new TreeItem<String>("Paint");
    private List<MenuItem> groupsList = new ArrayList<>();


    private int[] counter = new int[8];
    private int groupsCounter = 1;

    private double x, y;

    private Shape selectedShape = null;
    private Shape newShape = null;

    private ContextMenu contextMenu = new ContextMenu();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        contextMenu.setOnAction(this::contextMenu_Click);

        /* Set the canvas reference in the application singleton. */
        application.setCanvas(MyCanvas);

        /* Default start. */
        MyMouseRadioBTN.setSelected(true);
        CircleBTN.setSelected(true);

        MyTreeView.setRoot(root);
        MyTreeView.setShowRoot(false);

        /* Indexing for shapes' names. */
        for (int i = 0; i < 8; i++) counter[i] = 1;

        /* Set the tree reference in the application singleton. */
        application.setTreeView(MyTreeView);
    }

    private void resizeShape() {

        double width,height;

        if(selectedShape.getState() instanceof PCircle || selectedShape.getState() instanceof PSquare) {

            width = (x - selectedShape.oldX) * 2;
            height = (x - selectedShape.oldX) * 2;

            Map<String, Double> properties = new HashMap<>(selectedShape.getProperties());
            properties.replace("x", selectedShape.oldX - width / 2);
            properties.replace("y", selectedShape.oldY - width / 2);
            properties.replace("width", width);
            properties.replace("height", height);

            SetPropertiesCommand setPropertiesCommand = new SetPropertiesCommand(selectedShape, properties);
            setPropertiesCommand.execute();

        }

        if (selectedShape.getState() instanceof PEllipse || selectedShape.getState() instanceof PRectangle) {

            width = (x - selectedShape.getProperties().get("x"));
            height = (y - selectedShape.getProperties().get("y"));

            Map<String, Double> properties = new HashMap<>(selectedShape.getProperties());
            properties.replace("width", width);
            properties.replace("height", height);

            SetPropertiesCommand setPropertiesCommand = new SetPropertiesCommand(selectedShape, properties);
            setPropertiesCommand.execute();

        }

        if(selectedShape.getState() instanceof PTriangle
                || selectedShape.getState() instanceof PHexagon
                || selectedShape.getState() instanceof PPentagon) {

            width = (x - selectedShape.oldX) * 2;
            height = (y - selectedShape.oldY) * 2;

            Map<String, Double> properties = new HashMap<>(selectedShape.getProperties());

            properties.replace("x", selectedShape.oldX + height / 2);
            properties.replace("y", selectedShape.oldY + height / 2);
            properties.replace("width", width);
            properties.replace("height", height);

            SetPropertiesCommand setPropertiesCommand = new SetPropertiesCommand(selectedShape, properties);
            setPropertiesCommand.execute();

        }

        if(selectedShape.getState() instanceof PLine) {

            selectedShape.getProperties().replace("x2", x);
            selectedShape.getProperties().replace("y2", y);
            selectedShape.erase(MyCanvas);
            selectedShape.draw(MyCanvas);

        }
    }

    private int retrieveSelectedShape(){

        if(CircleBTN.isSelected()){
            newShape = new Shape(new PCircle(x, y, 0, 0, MyOpacitySlider.getValue(),
                    MyStrokeSlider.getValue()));

            return  0;
        }

        if(EllipseBTN.isSelected()){
            newShape = new Shape(new PEllipse(x, y, 0, 0, MyOpacitySlider.getValue(),
                    MyStrokeSlider.getValue()));

            return 1;
        }

        if(RectangleBTN.isSelected()){
            newShape = new Shape(new PRectangle(x, y, 0, 0, MyOpacitySlider.getValue(),
                    MyStrokeSlider.getValue()));

            return 2;
        }

        if(SquareBTN.isSelected()){
            newShape = new Shape(new PSquare(x, y, 0, 0, MyOpacitySlider.getValue(),
                    MyStrokeSlider.getValue()));

            return 3;
        }

        if(LineBTN.isSelected()){
            newShape = new Shape(new PLine(x, y, 0, 0, MyOpacitySlider.getValue(),
                    MyStrokeSlider.getValue()));

            return 4;
        }

        if(PolygonBTN.isSelected()){
            newShape = new Shape(new PHexagon(x, y, 0, 0, MyOpacitySlider.getValue(),
                    MyStrokeSlider.getValue()));

            return 5;
        }

        if(TriangleBTN.isSelected()) {
            newShape = new Shape(new PTriangle(x, y, 0, 0, MyOpacitySlider.getValue(),
                    MyStrokeSlider.getValue()));

            return 6;
        }

        return -1;
    }

    private void initializeSelectedShape(int index){

        newShape.oldX = x;
        newShape.oldY = y;

        newShape.getProperties().replace("width", 10.0);
        newShape.getProperties().replace("height", 10.0);

        newShape.setBackColor(MyBackColorPicker.getValue());
        newShape.setStrokeColor(MyStrokeColorPicker.getValue());

        newShape.name = newShape.getState().getClass().getName().replace("M.Shapes.P","") + " (" + counter[index]++ + ")";
    }

    private void removeShape(TreeItem<String> selectedItem){

        /* remove shape from shapesList */
        for (Shape shape: application.getShapes())
            if (shape.name.equals(selectedItem.getValue())) {

                RemoveShapeCommand removeShapeCommand = new RemoveShapeCommand(shape);
                removeShapeCommand.execute();
                application.refresh(application.getCanvas());

                break;
            }
    }

    private void removeGroup(TreeItem<String> selectedItem) {

        for (MenuItem current : groupsList) {

            if (current.getText().equals(selectedItem.getValue())) {

                RemoveGroupCommand removeGroupCommand = new RemoveGroupCommand(selectedItem.getValue());
                removeGroupCommand.execute();

                application.refresh(application.getCanvas());

                selectedItem.getParent().getChildren().remove(selectedItem);
                contextMenu.getItems().remove(current);
                groupsList.remove(current);
                break;
            }
        }
    }

    public void MyCanvas_Drag(MouseEvent mouseEvent) {

        /* Get the cursor's coordinates. */
        x = mouseEvent.getX();
        y = mouseEvent.getY();

        TreeItem<String> selectedItem = MyTreeView.getSelectionModel().getSelectedItem();

        if (!selectedItem.getValue().contains("Group")){

            /* Get a reference to the selected shape. */
            for (Shape current : application.getShapes()) {
                if (current.name.trim().equals(selectedItem.getValue())) {
                    selectedShape = current;
                    break;
                }
            }

            /* If the user wants to drag the selected shape. */
            if (MyHandRadioBTN.isSelected()) {

                /* Change the cursor shape. */
                MyCanvas.setCursor(Cursor.CLOSED_HAND);


                if(selectedShape.getState() instanceof PTriangle
                        || selectedShape.getState() instanceof PPentagon
                        || selectedShape.getState() instanceof PHexagon) {

                    /* 1. Instantiate a new SetPositionCommand. */
                    SetPositionCommand setPositionCommand = new SetPositionCommand(selectedShape, new Point2D(
                            x - selectedShape.getProperties().get("width") / 10,
                            y - selectedShape.getProperties().get("height") / 10));

                    /* 2. Execute the request. */
                    setPositionCommand.execute();

                }

                if(selectedShape.getState() instanceof PLine) {

                    /* 1. Instantiate a new SetPositionCommand. */
                    SetPositionCommand setPositionCommand = new SetPositionCommand(selectedShape, new Point2D(
                            x - selectedShape.getProperties().get("x2") / 2,
                            y - selectedShape.getProperties().get("y2") / 2));

                    /* 2. Execute the request. */
                    setPositionCommand.execute();

                }

                else {

                    /* 1. Instantiate a new SetPositionCommand. */
                    SetPositionCommand setPositionCommand = new SetPositionCommand(selectedShape, new Point2D(
                            x - selectedShape.getProperties().get("width") / 2,
                            y - selectedShape.getProperties().get("height") / 2));

                    /* 2. Execute the request. */
                    setPositionCommand.execute();

                }
            }

            if(MySizeRadioBTN.isSelected() || MyMouseRadioBTN.isSelected()) {

                assert selectedShape != null;
                resizeShape();
            }

            /* Refresh the canvas to reflect the changes. */
            application.refresh(application.getCanvas());

        }
    }

    public void MyCanvas_Press(MouseEvent mouseEvent) {

        /* Get the mouse press coordinates. */
        x = mouseEvent.getX();
        y = mouseEvent.getY();

        /* If the user wants to draw and selected the mouse radio button. */
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

            /* Set the fill color. */
            SetFillColorCommand setFillColorCommand = new SetFillColorCommand(newShape, MyBackColorPicker.getValue());
            setFillColorCommand.execute();

            /* Set the stroke color. */
            SetColorCommand setColorCommand = new SetColorCommand(newShape, MyStrokeColorPicker.getValue());
            setColorCommand.execute();

            /* Set the shape's name in the tree view. */
            newShape.name = newShape.getState().getClass().getName().replace("model.Shapes.P","") + " (" + counter[index]++ + ")";

            /* Add the new shape to the array list. */
            AddShapeCommand addShapeCommand = new AddShapeCommand(newShape);
            addShapeCommand.execute();

            /* Add the new shape to the tree view. */
            TreeItem<String> newItem = new TreeItem<>(newShape.name);
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

            /* Set the stroke color. */
            SetColorCommand setColorCommand = new SetColorCommand(selectedShape, MyBackColorPicker.getValue());
            setColorCommand.execute();

            /* Set the fill color. */
            SetFillColorCommand setFillColorCommand = new SetFillColorCommand(selectedShape, MyStrokeColorPicker.getValue());
            setFillColorCommand.execute();

            if(mouseEvent.getSource() == MyOpacitySlider){

                /* Set the shape properties. */
                Map<String, Double> properties = new HashMap<>(selectedShape.getProperties());
                properties.replace("opacity", MyOpacitySlider.getValue());

                SetPropertiesCommand setPropertiesCommand = new SetPropertiesCommand(selectedShape, properties);
                setPropertiesCommand.execute();

                /* Refresh the canvas to reflect changes. */
                application.refresh(application.getCanvas());
            }

            if(mouseEvent.getSource() == MyStrokeSlider){

                /* Set the shape properties. */
                Map<String, Double> properties = new HashMap<>(selectedShape.getProperties());
                properties.replace("strokewidth", MyStrokeSlider.getValue());

                SetPropertiesCommand setPropertiesCommand = new SetPropertiesCommand(selectedShape, properties);
                setPropertiesCommand.execute();

                /* Refresh the canvas to reflect changes. */
                application.refresh(application.getCanvas());
            }
        }
    }

    public void Delete_Click() {

        TreeItem<String> selectedItem = MyTreeView.getSelectionModel().getSelectedItem();

        if(!application.getShapes().isEmpty() || !groupsList.isEmpty())
            if(selectedItem.getValue().contains("Group")) removeGroup(selectedItem);
            else removeShape(selectedItem);

    }

    public void saveClicked() {

        application.setSaveNLoadStrategy(new XmlStrategy()); // XMLStrategy for testing, the user should choose the method
        application.save("drawing.xml"); // drawing.xml for testing, the user should choose the file

    }

    public void loadClicked() {

        application.setSaveNLoadStrategy(new XmlStrategy()); // XMLStrategy for testing, the user should choose the method
        application.load("drawing.xml");// drawing.xml for testing, the user should choose the file

        application.getTreeView().getRoot().getChildren().clear();
        for (Shape shape : application.getShapes()) {
            TreeItem<String> item = new TreeItem<>(shape.name);
            application.getTreeView().getRoot().getChildren().add(item);
        }
    }

    public void MyTreeView_Click(MouseEvent mouseEvent) {

        if (!application.getShapes().isEmpty())
        {
            if(!MyTreeView.getSelectionModel().getSelectedItem().getValue().contains("Group"))
            {
                if (mouseEvent.getButton() == MouseButton.SECONDARY)
                {
                    /* set contextMenu position & show it */
                    contextMenu.show(MyTreeView, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                }
                else contextMenu.hide();
            }
            else contextMenu.hide();
        }
    }

    //new read all comments and to the end
    private void contextMenu_Click(ActionEvent event) {

        /* retrieve group & shape name */
        String groupName = ((MenuItem)event.getTarget()).getText();
        TreeItem<String> selectedItem = MyTreeView.getSelectionModel().getSelectedItem();

        /* search for the shape in the shapesList & assign groupName to it */
        for(Shape shape: application.getShapes())
            if (shape.name.equals(selectedItem.getValue()))
            {
                shape.groupName = groupName;
                break;
            }


        /* remove shape from old node in the treeView */
        selectedItem.getParent().getChildren().remove(selectedItem);


        /* search for the group in the treeView & add the shape as sub treeItem */
        for (TreeItem<String> treeItem: root.getChildren())
            if (treeItem.getValue().equals(groupName))
            {
                treeItem.getChildren().add(selectedItem);
                break;
            }
    }

    //new read all comments and to the end
    public void CreateGroup_Click(MouseEvent mouseEvent) {

        /* create a MenuItem that holds group name */
        MenuItem item = new MenuItem("Group " + "(" + groupsCounter++ + ")");

        /* add the group to the treeview
        & groups list (Arraylist<MenuItem> groups)
        & context menu */

        root.getChildren().add(new TreeItem<>(item.getText()));
        contextMenu.getItems().add(item);
        groupsList.add(item);
    }

    //new read all comments and to the end
    public void MyPane_Press(KeyEvent keyEvent) {

        if(!application.getShapes().isEmpty() && MyHandRadioBTN.isSelected())
        {
            TreeItem<String> selectedItem = MyTreeView.getSelectionModel().getSelectedItem();

            if (selectedItem.getValue().contains("Group"))
            {

                if(keyEvent.getCode()== KeyCode.UP)
                    application.getShapes().forEach(shape -> { if (shape.groupName.equals(selectedItem.getValue()))
                        shape.getProperties().replace("y", shape.getProperties().get("y") + 1); });

                if(keyEvent.getCode()==KeyCode.DOWN)
                    application.getShapes().forEach(shape -> { if (shape.groupName.equals(selectedItem.getValue()))
                        shape.getProperties().replace("y", shape.getProperties().get("y") - 1); });

                if(keyEvent.getCode()==KeyCode.LEFT)
                    application.getShapes().forEach(shape -> { if (shape.groupName.equals(selectedItem.getValue()))
                        shape.getProperties().replace("x", shape.getProperties().get("x") + 1); });

                if(keyEvent.getCode()==KeyCode.RIGHT)
                    application.getShapes().forEach(shape -> { if (shape.groupName.equals(selectedItem.getValue()))
                        shape.getProperties().replace("x", shape.getProperties().get("x") - 1); });

                application.refresh(application.getCanvas());
            }
        }
    }

}