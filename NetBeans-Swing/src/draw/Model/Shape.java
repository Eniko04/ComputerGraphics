package draw.Model;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Абстрактен базов клас за примитивите – съдържа общите характеристики.
 */
public abstract class Shape implements Serializable {

    private static final long serialVersionUID = 1L;

    // Трансформации
    private double rotationAngle = 0.0;
    private double scaleX = 1.0;
    private double scaleY = 1.0;

    // Геометрия и стил
    private Rectangle rectangle = new Rectangle();  // по подразбиране празен, за да не е null
    private Color fillColor = Color.WHITE;
    private Color strokeColor = Color.BLACK;
    private float strokeWidth = 2.0f;
    private float alpha = 1.0f;

    // Име
    private String name = "";

    // --- Конструктори ---

    public Shape() {}

    public Shape(Rectangle rect) {
        this.rectangle = new Rectangle(rect); // Копие
    }

    public Shape(Shape shape) {
        this.rectangle = new Rectangle(shape.getRectangle());
        this.fillColor = shape.getFillColor();
        this.strokeColor = shape.getStrokeColor();
        this.strokeWidth = shape.getStrokeWidth();
        this.alpha = shape.getAlpha();
        this.rotationAngle = shape.getRotationAngle();
        this.scaleX = shape.getScaleX();
        this.scaleY = shape.getScaleY();
        this.name = shape.getName();
    }

    // --- Трансформации ---

    public void rotate(double angle) {
        this.rotationAngle = (this.rotationAngle + angle) % 360;
    }

    public double getRotationAngle() {
        return rotationAngle;
    }

    public void setRotationAngle(double angle) {
        this.rotationAngle = angle % 360;
    }

    public double getScaleX() {
        return scaleX;
    }

    public void setScaleX(double scaleX) {
        this.scaleX = scaleX;
    }

    public double getScaleY() {
        return scaleY;
    }

    public void setScaleY(double scaleY) {
        this.scaleY = scaleY;
    }

    // --- Рисуване ---

    public void DrawSelf(Graphics grfx) {
        Graphics2D g = (Graphics2D) grfx;

        if (name != null && !name.isEmpty()) {
            Rectangle r = getRectangle();
            int centerX = r.x + r.width / 2;
            int bottomY = r.y + r.height + 15;

            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.PLAIN, 12));

            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(name);
            g.drawString(name, centerX - textWidth / 2, bottomY);
        }
    }

    // --- Геометрия ---

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void setRectangle(Rectangle value) {
        this.rectangle = value;
    }

    public int getWidth() {
        return rectangle.width;
    }

    public void setWidth(int value) {
        rectangle.width = value;
    }

    public int getHeight() {
        return rectangle.height;
    }

    public void setHeight(int value) {
        rectangle.height = value;
    }

    public Point getLocation() {
        return rectangle.getLocation();
    }

    public void setLocation(Point value) {
        rectangle.setLocation(value);
    }

    public boolean Contains(Point point) {
        return rectangle.contains(point);
    }

    protected void setBoundingBox(Rectangle rectangle) {
        // Може да се override-не от подкласи
    }

    // --- Стил ---

    public Color getFillColor() {
        return fillColor;
    }

    public void setFillColor(Color value) {
        this.fillColor = value;
    }

    public Color getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(Color strokeColor) {
        this.strokeColor = strokeColor;
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    // --- Име ---

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // --- Дръжки за мащабиране ---

    public List<Rectangle> getResizeHandles() {
        List<Rectangle> handles = new ArrayList<>();
        Rectangle r = getRectangle();
        int size = 8;

        handles.add(new Rectangle(r.x - size / 2, r.y - size / 2, size, size));                         // горе-ляво
        handles.add(new Rectangle(r.x + r.width - size / 2, r.y - size / 2, size, size));               // горе-дясно
        handles.add(new Rectangle(r.x - size / 2, r.y + r.height - size / 2, size, size));              // долу-ляво
        handles.add(new Rectangle(r.x + r.width - size / 2, r.y + r.height - size / 2, size, size));    // долу-дясно

        return handles;
    }
}
