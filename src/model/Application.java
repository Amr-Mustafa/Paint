package model;

import model.commands.UpdateShapeCommand;
import model.commands.AddShapeCommand;
import model.commands.Command;
import model.commands.DrawCommand;
import model.commands.RemoveShapeCommand;
import model.save.ISaveNLoadStrategy;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Application implements IDrawingEngine {

    private Canvas canvas;

    private ArrayList<Shape> shapes;

    /* XmlStrategy or JsonStrategy. */
    ISaveNLoadStrategy saveNLoadstrategy;

    private Stack<Command> undoStack;
    private Stack<Command> redoStack;

    /* Instantiate the Singleton. */
    private static Application instance = new Application();

    private Application(){
        undoStack = new Stack<>();
        redoStack = new Stack<>();
        shapes    = new ArrayList<>();
    }

    public void setSaveNLoadstrategy (ISaveNLoadStrategy saveNLoadstrategy) {
        this.saveNLoadstrategy = saveNLoadstrategy;
    }

    /* For external reference to the Singleton. */
    public static Application getInstance (){
        return instance;
    }

    public void undo () {

        /* 1. Get the most recent command from the undoStack. */
        Command cmd = this.undoStack.pop();

        /* 2. Undo the command. */
        cmd.undo();

        /* 3. Push the command onto the undoStack. */
        redoStack.push(cmd);

    }

    public void redo () {

        /* 1. Get the most recent command from the redoStack. */
        Command cmd = this.redoStack.pop();

        /* 2. Execute the command. */
        cmd.execute();

        /* 3. Push the executed command on the undoStack. */
        this.undoStack.push(cmd);

    }

    public Memento createMemento () {

        /* Create a memento the save the current state of the application. */
        Memento memo = new Memento();
        memo.setState(this.getShapes());
        return memo;

    }

    public void setCanvas (Canvas canvas) {
        this.canvas = canvas;
    }

    public Canvas getCanvas () { return this.canvas; }

    public void refresh (Object canvas) {

        /* 1. Clear the canvas. */
        GraphicsContext graphicsContext = ((Canvas)canvas).getGraphicsContext2D();
        graphicsContext.clearRect(0,0,564,200);

        /* 2. Draw all shapes. */
        for (Shape shape : shapes) {
            DrawCommand drawCommand = new DrawCommand(shape);
            drawCommand.execute();
        }

    }

    public void addShape (Shape shape) {
        AddShapeCommand addShapeCommand = new AddShapeCommand(shape);
        addShapeCommand.execute();
    }

    public ArrayList<Shape> getShapes () {
        return this.shapes;
    }

    public void removeShape (Shape shape) {
        RemoveShapeCommand removeShapeCommand = new RemoveShapeCommand(shape);
        removeShapeCommand.execute();
    }

    public void updateShape (Shape oldShape, Shape newShape) {
        UpdateShapeCommand updateShapeCommand = new UpdateShapeCommand();
    }

    public void save (String path) {
        this.saveNLoadstrategy.save(path);
    }

    public void load (String path) {
        this.saveNLoadstrategy.load(path);
    }

    public List<Class<? extends Shape>> getSupportedShapes() {
        return null;
    }

    public void installPluginShape(String jarPath) {

    }

    public void setState (ArrayList<Shape> list) {
        shapes.clear();
        shapes.addAll(list);
    }

}
