package model;

import com.thoughtworks.xstream.mapper.Mapper;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import model.Shapes.IShape;
import model.commands.UpdateShapeCommand;
import model.commands.AddShapeCommand;
import model.commands.Command;
import model.commands.DrawCommand;
import model.commands.RemoveShapeCommand;
import model.save.ISaveNLoadStrategy;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import sun.tools.jar.resources.jar;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Application implements IDrawingEngine {

    private Canvas canvas;

    /* List of all the shapes drawn on the canvas. */
    private ArrayList<Shape> shapes;

    /* */
    private TreeView<String> treeView;

    /* XmlStrategy or JsonStrategy. */
    private ISaveNLoadStrategy saveNLoadStrategy;

    private Stack<Command> undoStack;
    private Stack<Command> redoStack;

    /* Instantiate the Singleton. */
    private static Application instance = new Application();

    private Application(){
        undoStack = new Stack<>();
        redoStack = new Stack<>();
        shapes    = new ArrayList<>();
    }

    public void setTreeView (TreeView<String> treeView) {
        this.treeView = treeView;
    }

    public TreeView<String> getTreeView() {
        return treeView;
    }

    public void setState (ArrayList<Shape> list) {
        shapes.clear();
        shapes.addAll(list);
    }

    /**
     * Sets the saveNLoadstrategy as chosen by the user.
     * @param saveNLoadStrategy
     */
    public void setSaveNLoadStrategy (ISaveNLoadStrategy saveNLoadStrategy) {
        this.saveNLoadStrategy = saveNLoadStrategy;
    }

    /**
     * Returns a reference to the singleton object.
     * @return
     */
    public static Application getInstance (){
        return instance;
    }

    public void pushCommand (Command command) {
        this.undoStack.push(command);
    }

    /**
     * Sets the reference to the canvas. This method is called in the initialize method of the controller.
     * @param canvas
     */
    public void setCanvas (Canvas canvas) {
        this.canvas = canvas;
    }

    /**
     * Returns the shapes list (the current state of the application).
     * @return
     */
    public ArrayList<Shape> getShapes () {
        return this.shapes;
    }

    /**
     * Undo the most recent command from the undoStack.
     */
    public void undo () {

        /* 1. Get the most recent command from the undoStack. */
        Command cmd = this.undoStack.pop();

        /* 2. Undo the command. */
        cmd.undo();

        /* 3. Push the command onto the undoStack. */
        redoStack.push(cmd);

    }

    /**
     * Redo the most recent command from the redoStack.
     */
    public void redo () {

        /* 1. Get the most recent command from the redoStack. */
        Command cmd = this.redoStack.pop();

        /* 2. Execute the command. */
        cmd.execute();

        /* 3. Push the executed command on the undoStack. */
        this.undoStack.push(cmd);

    }

    /**
     * Returns a Memento object saving the current state of the application. The state of the application is
     * resembled in the shapes list.
     * @return
     */
    public Memento createMemento () {

        /* Create a memento the save the current state of the application. */
        Memento memo = new Memento();
        memo.setState(this.getShapes());
        return memo;

    }

    /**
     * Returns a referece to the canvas.
     * @return
     */
    public Canvas getCanvas () { return this.canvas; }

    /**
     * Given a reference to the canvas, the method clears it and redraws all shapes in the shapes list.
     * @param canvas
     */
    public void refresh (Object canvas) {

        /* 1. Clear the canvas. */
        GraphicsContext graphicsContext = ((Canvas)canvas).getGraphicsContext2D();
        graphicsContext.clearRect(0,0,564,200);

        /* 2. Draw all shapes from the shapes list. */
        for (Shape shape : shapes) {
            DrawCommand drawCommand = new DrawCommand(shape.getState());
            drawCommand.execute();
        }

        /* 3. Refill the tree view. */
//        treeView.getRoot().getChildren().clear();
//        for (Shape shape : shapes) {
//            TreeItem<String> item = new TreeItem<>(shape.name);
//            treeView.getRoot().getChildren().add(item);
//        }

    }

    /**
     * Given a shape object, the method adds the shape to the shapes list and then refreshes the canvas.
     * @param shape
     */
    public void addShape (Shape shape) {
        AddShapeCommand addShapeCommand = new AddShapeCommand(shape);
        addShapeCommand.execute();
    }

    /**
     * Given a shape object, the method removes the shape from the shapes list and then refreshes the canvas.
     * @param shape
     */
    public void removeShape (Shape shape) {
        RemoveShapeCommand removeShapeCommand = new RemoveShapeCommand(shape);
        removeShapeCommand.execute();
    }

    /**
     * Given an oldShape object and a newShape object, the method removes the oldShape from the shapes list, adds the
     * newShape, and then refreshes the canvas.
     * @param oldShape
     * @param newShape
     */
    public void updateShape (Shape oldShape, Shape newShape) {
        UpdateShapeCommand updateShapeCommand = new UpdateShapeCommand(oldShape, newShape);
        updateShapeCommand.execute();
    }

    /**
     * Given the path to an XML or JSON file, the method save the shapes list into the file as XML or JSON.
     * @param path
     */
    public void save (String path) {
        this.saveNLoadStrategy.save(path);
    }

    /**
     * Given the path to an XML or JSON file, the method loads its content onto the canvas.
     * @param path
     */
    public void load (String path) {
        this.saveNLoadStrategy.load(path);
    }

    /**
     * NOT YET IMPLEMENTED!
     * @return
     */
    public List<Class<? extends Shape>> getSupportedShapes() {
        return null;
    }

    /**
     * NOT YET IMPLEMENTED!
     * @return
     */
    public void installPluginShape(String jarPath) {

    }


}
