package model.save;

import com.thoughtworks.xstream.io.xml.DomDriver;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import model.Application;
import model.Shape;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import model.Shapes.PCircle;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

public class XmlStrategy implements ISaveNLoadStrategy {

    private Application application = Application.getInstance();

    public void save (String fileName) {

        /* 1. Initialize the serializer. */
        XStream xstream = new XStream(new DomDriver());

        /* 2. Generate the XML string. */
        String xml = xstream.toXML(application.getShapes());

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
        XStream xstream = new XStream(new DomDriver());

        String xml = "";
        try {
            xml = new String(Files.readAllBytes(FileSystems.getDefault().getPath(fileName)));
        } catch (Exception ex) {
            ex.getMessage();
        }

        /* 2. Refill the shapes list. */
        application.getShapes().clear();
        ArrayList<Shape> shapes = (ArrayList<Shape>) xstream.fromXML(xml);
        application.getShapes().addAll(shapes);

        /* 3. Refresh the canvas. */
        application.refresh(application.getCanvas());

    }

}