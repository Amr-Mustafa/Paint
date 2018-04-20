package model.commands;

import model.Application;
import model.commands.Command;
import model.Memento;
import model.Shape;

public class UpdateShapeCommand implements Command {

    private Application application;
    private Shape oldShape ;
    private Shape newShape ;
    private Memento memento;

    public UpdateShapeCommand(Shape oldShape, Shape newShape) {

        this.oldShape    = oldShape;
        this.newShape    = newShape;
        this.application = Application.getInstance();

    }

    @Override
    public void execute() {

        /* 1. Get the old state before executing the command. */
        memento = application.createMemento();

        /* 2. Remove the old shape from the shapes list. */
        application.removeShape(oldShape);

        /* 3. Add the new shape to the shapes list. */
        application.addShape(newShape); // addShape method should add the shape to the shapes list and refresh the canvas.
    }

    @Override
    public void undo() {

        application.setState(memento.getState());

    }
}
