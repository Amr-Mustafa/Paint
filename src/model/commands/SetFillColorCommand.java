package model.commands;

import model.Application;
import model.Memento;
import model.Shape;
import javafx.scene.paint.Color;

public class SetFillColorCommand implements Command {

    private Shape shape;
    private Color color;
    private Memento memento;
    private Application application;

    public SetFillColorCommand(Shape shape, Color color) {

        this.shape       = shape;
        this.color       = color;
        this.application = Application.getInstance();

    }

    @Override
    public void execute() {

        /* 1. Get the old state before executing the command. */
        memento = application.createMemento();

        /* 2. Perform the request. */
        shape.setBackColor(color);

    }

    @Override
    public void undo() {

        /* 1. Revert the application state to the backup state. */
        application.setState(memento.getState());

    }

}
