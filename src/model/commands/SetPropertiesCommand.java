package model.commands;

import model.Application;
import model.Memento;
import model.Shape;
import java.util.Map;

public class SetPropertiesCommand implements Command {

    private Shape shape;
    private Map<String, Double> properties;
    private Memento memento;
    private Application application;

    public SetPropertiesCommand(Shape shape, Map<String, Double> properties) {

        this.shape       = shape;
        this.properties  = properties;
        this.application = Application.getInstance();

    }

    @Override
    public void execute() {

        /* 1. Get the old state before executing the command. */
        memento = application.createMemento();

        /* 2. Perform the request. */
        shape.setProperties(properties);

    }

    @Override
    public void undo() {

        /* 1. Revert the application state to the backup state. */
        application.setState(memento.getState());

    }

}
