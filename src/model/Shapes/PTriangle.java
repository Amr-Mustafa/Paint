package model.Shapes;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

public class PTriangle implements IShape {

    private Map<String, Double> properties;
    private Color backColor;
    private Color strokeColor;

    public PTriangle(double x, double y, double width, double height, double opacity, double strokeWdith) {

        properties = new HashMap<String, Double>();
        properties.put("x", x);
        properties.put("y", y);
        properties.put("width", width);
        properties.put("height", height);
        properties.put("opacity", opacity);
        properties.put("strokewidth", strokeWdith);

    }

    @Override
    public void setPosition(Point2D position) {

        this.properties.replace("x", position.getX());
        this.properties.replace("y", position.getY());
    }

    @Override
    public Point2D getPosition() {

        return new Point2D(properties.get("x"),properties.get("y"));
    }

    @Override
    public void setProperties(Map<String, Double> properties) {
        this.properties = properties;
    }

    @Override
    public Map<String, Double> getProperties() {
        return this.properties;
    }

    @Override
    public void setBackColor(Color color) {
        this.backColor = color;
    }

    @Override
    public Color getBackColor() {
        return this.backColor;
    }

    @Override
    public void setStrokeColor(Color color) {
        this.strokeColor = color;
    }

    @Override
    public Color getStrokeColor() {
        return this.strokeColor;
    }

    @Override
    public void draw(Object canvas) {
        GraphicsContext graphicsContext = ((Canvas) canvas).getGraphicsContext2D();

        graphicsContext.setLineWidth(properties.get("strokewidth"));

        graphicsContext.setFill(backColor);
        graphicsContext.setStroke(strokeColor);

        graphicsContext.fillPolygon(
                new double[]{properties.get("x"),
                        properties.get("x")+properties.get("height"),
                        properties.get("x")-properties.get("height") },

                new double[]{properties.get("y")-properties.get("height"),
                        properties.get("y")+(properties.get("height")*0.3),
                        properties.get("y")+(properties.get("height")*0.3)},
                3);

        graphicsContext.strokePolygon(
                new double[]{properties.get("x"),
                        properties.get("x")+properties.get("height"),
                        properties.get("x")-properties.get("height") },

                new double[]{properties.get("y")-properties.get("height"),
                        properties.get("y")+(properties.get("height")*0.3),
                        properties.get("y")+(properties.get("height")*0.3)},
                3);

    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return null;
    }

    @Override
    public void erase(Object canvas) {
        GraphicsContext graphicsContext = ((Canvas)canvas).getGraphicsContext2D();
        graphicsContext.clearRect(0,0,564,200);
    }
}
