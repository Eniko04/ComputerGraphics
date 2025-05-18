package draw.Model;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

public class PointShape extends Shape {

    private Point center;

    public PointShape(Point point) {
        super(new Rectangle(point.x - 3, point.y - 3, 15, 15)); // първоначално малък
        this.center = point;
    }
    public PointShape(PointShape other) {
        super(other); // Копира стилове, позиция и трансформации от Shape
        this.center = new Point(other.center); // Копира центъра на точката
    }


    @Override
    public void setRectangle(Rectangle rect) {
        this.center = new Point(rect.x + rect.width / 2, rect.y + rect.height / 2);
        super.setRectangle(rect);
    }

    @Override
    public Point getLocation() {
        return center;
    }

    @Override
    public void setLocation(Point newLocation) {
        Rectangle r = getRectangle();
        this.center = newLocation;
        super.setRectangle(new Rectangle(
                newLocation.x - r.width / 2,
                newLocation.y - r.height / 2,
                r.width,
                r.height
        ));
    }

    @Override
    public boolean Contains(Point p) {
        Rectangle r = getRectangle();
        Ellipse2D.Double ellipse = new Ellipse2D.Double(-r.width / 2.0, -r.height / 2.0, r.width, r.height);

        AffineTransform transform = new AffineTransform();
        transform.translate(center.x, center.y);
        transform.rotate(Math.toRadians(getRotationAngle()));
        transform.scale(getScaleX(), getScaleY());

        try {
            Point transformed = (Point) transform.createInverse().transform(p, new Point());
            return ellipse.contains(transformed);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void DrawSelf(Graphics grfx) {
        Graphics2D g = (Graphics2D) grfx.create();
        Rectangle r = getRectangle();

        g.translate(center.x, center.y);
        g.rotate(Math.toRadians(getRotationAngle()));
        g.scale(getScaleX(), getScaleY());

        Composite originalComposite = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getAlpha()));

        g.setColor(getFillColor());
        g.fillOval(-r.width / 2, -r.height / 2, r.width, r.height);

        g.setColor(getStrokeColor());
        g.setStroke(new BasicStroke(getStrokeWidth()));
        g.drawOval(-r.width / 2, -r.height / 2, r.width, r.height);

        g.setComposite(originalComposite);
        g.dispose();

        // ⬇️ Добавено: показване на името под точката
        super.DrawSelf(grfx);
    }


}
