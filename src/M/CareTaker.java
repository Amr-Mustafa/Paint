package M;

import java.util.ArrayList;

public class CareTaker {

    private ArrayList<Memento> shapeStates;

    public void saveMementoState(Memento memento){

        shapeStates.add(memento);

    }
    public Memento retrieveMementoState(int index){

        return shapeStates.get(index);
    }
}
