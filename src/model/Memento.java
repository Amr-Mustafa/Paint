package model;

import java.util.ArrayList;

public class Memento {

    private ArrayList<Shape> state;

    public void setState(ArrayList state){

        this.state = state;

    }

    public ArrayList getState(){

        return this.state;

    }

}
