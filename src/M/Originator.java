package M;

import java.util.ArrayList;

public class Originator {

    private ArrayList<Shape> state;

    public void setOriginatorState(ArrayList<Shape> state){
        this.state = state;
    }
    public Memento storeMementoState(ArrayList<Shape> state){
       Memento memento = new Memento();
       memento.setState(state);
       return memento;
    }
    public ArrayList<Shape> getMementoState(){
        return this.state;
    }

}
