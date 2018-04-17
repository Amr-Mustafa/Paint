package M;

import M.Interfaces.ISaveNLoadStrategy;

public class Application {

    ISaveNLoadStrategy saveNLoadstrategy ;
    private static Application instance = new Application();
    private Application(){
    }
    public static Application getInstance (){
        return instance;
    }

}
