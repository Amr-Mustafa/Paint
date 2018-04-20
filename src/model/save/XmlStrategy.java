package model.save;

import model.Application;
import model.Shape;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;

public class XmlStrategy implements ISaveNLoadStrategy {

    private Application application = Application.getInstance();

    public void save (String fileName) {

        /* 1. Initialize the serializer. */
        XStream xStream = new XStream(new StaxDriver());

        /* 2. Generate the XML string. */
        String xml = xStream.toXML(application.getShapes());

        /* 3. Print the XML string into a file with the given file name. */
        try {
            PrintWriter writer = new PrintWriter(fileName);
            writer.write(xml);
            writer.close();
        } catch (Exception ex) {
            ex.getMessage();
        }
    }

    public void load(String fileName) {

        /* 1. Initialize and open the stream. */
        XStream xStream = new XStream(new StaxDriver());
        File xmlFile = new File(fileName);

        /* 2. Refill the shapes list. */
        application.getShapes().clear();
        application.getShapes().addAll((ArrayList<Shape>) xStream.fromXML(xmlFile));

        /* 3. Refresh the canvas. */
        application.refresh(application.getCanvas());

    }

}