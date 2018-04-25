package model.commands;

import model.Application;
import model.Memento;
import model.Shape;

public class RemoveGroupCommand implements Command {

    private String groupName;
    private Memento memento;
    private Application application;

    public RemoveGroupCommand (String groupName) {
        this.groupName = groupName;
        application = Application.getInstance();
    }

    @Override
    public void execute() {

        /* 1. Get the old state before executing the command. */
        memento = application.createMemento();

        /* 2. Perform the request. */

        application.getShapes().removeIf( shape -> shape.groupName.equals(groupName) );

        /* 3. Push the command onto the undo stack. */
        application.pushCommand(this);
    }

    @Override
    public void undo() {

        /* 1. Revert the application state to the backup state. */
        application.setState(memento.getState());

    }
}
