package model.commands;

import model.Application;
import model.Memento;
import model.Shape;
import javafx.geometry.Point2D;

public class SetPositionCommand implements Command {

    private Shape shape;
    private Point2D position;
    private Memento memento;
    private Application application;

    public SetPositionCommand(Shape shape, Point2D position) {

        this.shape  = shape;
        this.position = position;
        application = Application.getInstance();

    }

    @Override
    public void execute() {

        /* 1. Get the old state before executing the command. */
        memento = application.createMemento();

        /* 2. Perform the request. */
        shape.setPosition(position);

        /* 3. Push the command onto the undo stack. */
        application.pushCommand(this);
    }

    @Override
    public void undo() {

        /* 1. Revert the application state to the backup state. */
        application.setState(memento.getState());

    }
}
