package model.save;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import com.thoughtworks.xstream.io.xml.DomDriver;
import model.Application;
import model.Shape;
import sun.applet.AppletListener;

import javax.jws.soap.SOAPBinding;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;

public class JsonStrategy implements  ISaveNLoadStrategy{

    private Application application = Application.getInstance();


    @Override
    public void save(String fileName) {

        XStream xStream  = new XStream(new JettisonMappedXmlDriver());
        String json = xStream.toXML(application.getShapes());
        try {
            PrintWriter writer = new PrintWriter(fileName);
            writer.write(json);
            writer.close();
        } catch (Exception ex) {
            ex.getMessage();
        }



    }

    @Override
    public void load(String path) {
        XStream xstream = new XStream(new JettisonMappedXmlDriver());

        String json = "";
        try {
            json = new String(Files.readAllBytes(FileSystems.getDefault().getPath(path)));
        } catch (Exception ex) {
            ex.getMessage();
        }

        /* 2. Refill the shapes list. */
        application.getShapes().clear();
        ArrayList<Shape> shapes = (ArrayList<Shape>) xstream.fromXML(json);
        application.getShapes().addAll(shapes);

        /* 3. Refresh the canvas. */
        application.refresh(application.getCanvas());

    }
}
