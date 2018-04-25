package C;

import M.Shape;
import M.Shapes.*;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public Pane MyPane;
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

    public TreeView <String> MyTreeView;

    private TreeItem<String> root = new TreeItem<String>("Paint");
    private List<Shape> shapesList = new ArrayList<>();
    private List<MenuItem> groupsList = new ArrayList<>();


    private int[] counter = new int[8];
    private int groupsCounter = 1;

    private double x, y;


    private Shape selectedShape = null;
    private Shape newShape = null;


    private ContextMenu contextMenu = new ContextMenu();

    /*******************************************************/

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
    //new read all comments and to the end
    private void removeShape(TreeItem<String> selectedItem){

        /* remove shape from shapesList */
        for (Shape shape: shapesList)
            if (shape.name.equals(selectedItem.getValue()))
            {
                shape.erase(MyCanvas);
                shapesList.remove(shape);
                break;
            }

        /* remove shape from treeView */
        selectedItem.getParent().getChildren().remove(selectedItem);

        // refresh all shapes again
        for (Shape current : shapesList) current.draw(MyCanvas);
    }
    //new read all comments and to the end
    private void removeGroup(TreeItem<String> selectedItem){

        for (MenuItem current : groupsList) {

            if (current.getText().equals(selectedItem.getValue()))
            {
                /* remove all shapes that its "groupName" matches selected group */
                shapesList.removeIf(shape -> shape.groupName.equals(selectedItem.getValue()));


                //////////////// to remove /////////////////
                shapesList.get(0).erase(MyCanvas);

                /* remove the group from the treeview
                & groups list (Arraylist<MenuItem> groups)
                & context menu */

                selectedItem.getParent().getChildren().remove(selectedItem);
                contextMenu.getItems().remove(current);
                groupsList.remove(current);

                // refresh all shapes again
                for (Shape shape : shapesList) shape.draw(MyCanvas);

                break;
            }
        }
    }

    /*******************************************************/

    //region canvas events

    public void MyCanvas_Drag(MouseEvent mouseEvent) {

        x = mouseEvent.getX();
        y = mouseEvent.getY();

        TreeItem<String> selectedItem = MyTreeView.getSelectionModel().getSelectedItem();

        if (!selectedItem.getValue().contains("Group"))
        {
            for (Shape current : shapesList)
            {
                if (current.name.trim().equals(selectedItem.getValue()))
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
            int index = retrieveSelectedShape();

            initializeSelectedShape(index);

            shapesList.add(newShape);

            TreeItem<String> newItem = new TreeItem<String>(newShape.name);
            root.getChildren().add(newItem);


            MultipleSelectionModel msm = MyTreeView.getSelectionModel();
            msm.select(MyTreeView.getRow(newItem));

            selectedShape = newShape;

            newShape = null;
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

    //endregion

    /*******************************************************/

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

    /*******************************************************/

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

    /*******************************************************/
//new read all comments and to the end
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        contextMenu.setOnAction(this::contextMenu_Click);

        MyMouseRadioBTN.setSelected(true);
        CircleBTN.setSelected(true);

        MyTreeView.setRoot(root);
        MyTreeView.setShowRoot(false);

        for (int i = 0; i < 8; i++) counter[i] = 1;
    }

    /*******************************************************/
//new read all comments and to the end
    public void MyTreeView_Click(MouseEvent mouseEvent) {

        if (!shapesList.isEmpty())
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

    /*******************************************************/

    //new read all comments and to the end
    private void contextMenu_Click(ActionEvent event) {

        /* retrieve group & shape name */
        String groupName = ((MenuItem)event.getTarget()).getText();
        TreeItem<String> selectedItem = MyTreeView.getSelectionModel().getSelectedItem();

        /* search for the shape in the shapesList & assign groupName to it */
        for(Shape shape: shapesList)
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

    /*******************************************************/


    //new read all comments and to the end
    public void Delete_Click(MouseEvent mouseEvent) {

        TreeItem<String> selectedItem = MyTreeView.getSelectionModel().getSelectedItem();

        if(!shapesList.isEmpty() || !groupsList.isEmpty())
            if(selectedItem.getValue().contains("Group")) removeGroup(selectedItem);
            else removeShape(selectedItem);


        // we can reduce code and call refresh method in both cases & remove loops and erase invocation in both cases

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

        if(!shapesList.isEmpty() && MyHandRadioBTN.isSelected())
        {
            TreeItem<String> selectedItem = MyTreeView.getSelectionModel().getSelectedItem();

            if (selectedItem.getValue().contains("Group"))
            {

                if(keyEvent.getCode()==KeyCode.UP)
                    shapesList.forEach(shape -> { if (shape.groupName.equals(selectedItem.getValue()))
                        shape.getProperties().replace("y", shape.getProperties().get("y") + 1); });

                if(keyEvent.getCode()==KeyCode.DOWN)
                    shapesList.forEach(shape -> { if (shape.groupName.equals(selectedItem.getValue()))
                        shape.getProperties().replace("y", shape.getProperties().get("y") - 1); });

                if(keyEvent.getCode()==KeyCode.LEFT)
                    shapesList.forEach(shape -> { if (shape.groupName.equals(selectedItem.getValue()))
                        shape.getProperties().replace("x", shape.getProperties().get("x") + 1); });

                if(keyEvent.getCode()==KeyCode.RIGHT)
                    shapesList.forEach(shape -> { if (shape.groupName.equals(selectedItem.getValue()))
                        shape.getProperties().replace("x", shape.getProperties().get("x") - 1); });


                //////////////// to be replaces with refresh /////////////////
                shapesList.get(0).erase(MyCanvas);

                // refresh all shapes again
                for (Shape shape : shapesList) shape.draw(MyCanvas);
            }
        }
    }
}