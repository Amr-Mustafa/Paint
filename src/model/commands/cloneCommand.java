package model.commands;

import model.Application;
import model.Memento;
import model.Shape;

public class cloneCommand implements Command {

    private Shape shape;
    private Memento memento;
    private Application application;

    public cloneCommand(Shape shape) {

        this.shape  = shape;
        application = Application.getInstance();

    }

    @Override
    public void execute() {

        /* 1. Get the old state before executing the command. */
        memento = application.createMemento();

        /* 2. Perform the request. */
        try {
            Shape clone = (Shape) shape.clone();
            clone.draw(application.getCanvas()); // the draw method should add the shape to the shapes list
        } catch (Exception ex) {
            System.out.println("Error!");
        }

    }

    @Override
    public void undo() {

        /* 1. Revert the application state to the backup state. */
        application.setState(memento.getState());

    }
}
