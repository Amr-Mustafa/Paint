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
    private ArrayList<Shape> shapes = application.getShapes();

    public void save (String fileName) {

        XStream xStream = new XStream(new StaxDriver());
        String xml = xStream.toXML(shapes);

        try {
            PrintWriter writer = new PrintWriter(fileName);
            writer.write(xml);
            writer.close();
        } catch (Exception ex) {
            ex.getMessage();
        }
    }

    public void load(String fileName) {

        XStream xStream = new XStream(new StaxDriver());

        File xmlFile = new File(fileName);

        shapes = (ArrayList<Shape>) xStream.fromXML(xmlFile);
    }

}