package model.commands;

import model.Application;
import model.Memento;
import model.Shape;

public class RemoveShapeCommand implements Command {

    private Shape shape;
    private Memento memento;
    private Application application;

    public RemoveShapeCommand (Shape shape) {
        this.shape = shape;
        application = Application.getInstance();
    }

    @Override
    public void execute() {

        /* 1. Get the old state before executing the command. */
        memento = application.createMemento();

        /* 2. Perform the request. */
        shape.remove(); // remove method should remove the shape from the list and refresh the canvas

        /* 3. Push the command onto the undo stack. */
        application.pushCommand(this);
    }

    @Override
    public void undo() {

        /* 1. Revert the application state to the backup state. */
        application.setState(memento.getState());

    }
}
