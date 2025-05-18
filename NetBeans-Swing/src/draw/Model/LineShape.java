package draw.Model;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;

public class LineShape extends Shape {

    private Point start;
    private Point end;
    private static final int TOLERANCE = 5;

    public LineShape(Point start, Point end) {
        super(calculateBoundingRectangle(start, end));
        this.start = start;
        this.end = end;
    }

    public LineShape(LineShape line) {
        super(line);
        this.start = new Point(line.start);
        this.end = new Point(line.end);
    }

    private static Rectangle calculateBoundingRectangle(Point p1, Point p2) {
        int x = Math.min(p1.x, p2.x);
        int y = Math.min(p1.y, p2.y);
        int width = Math.abs(p1.x - p2.x);
        int height = Math.abs(p1.y - p2.y);
        return new Rectangle(x, y, width, height);
    }

    @Override
    public boolean Contains(Point point) {
        Rectangle r = getRectangle();

        Line2D.Double line = new Line2D.Double(
                -r.width / 2.0, -r.height / 2.0,
                r.width / 2.0,  r.height / 2.0
        );

        AffineTransform transform = new AffineTransform();
        transform.translate(r.x + r.width / 2.0, r.y + r.height / 2.0);
        transform.rotate(Math.toRadians(getRotationAngle()));
        transform.scale(getScaleX(), getScaleY());

        try {
            AffineTransform inverse = transform.createInverse();
            Point transformedPoint = (Point) inverse.transform(point, new Point());
            return line.ptSegDist(transformedPoint) <= TOLERANCE;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void DrawSelf(Graphics grfx) {
        Graphics2D g = (Graphics2D) grfx.create();
        Rectangle r = getRectangle();

        int cx = r.x + r.width / 2;
        int cy = r.y + r.height / 2;

        g.translate(cx, cy);
        g.rotate(Math.toRadians(getRotationAngle()));
        g.scale(getScaleX(), getScaleY());

        Composite originalComposite = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getAlpha()));

        g.setColor(getStrokeColor());
        g.setStroke(new BasicStroke(getStrokeWidth()));
        g.drawLine(-r.width / 2, -r.height / 2, r.width / 2, r.height / 2);

        g.setComposite(originalComposite);
        g.dispose();

        // ⬇️ Добавено: визуализиране на името под линията
        super.DrawSelf(grfx);
    }


    @Override
    public void setLocation(Point newLocation) {
        Rectangle r = getRectangle();
        Point oldLocation = getLocation();
        int dx = newLocation.x - oldLocation.x;
        int dy = newLocation.y - oldLocation.y;

        start.translate(dx, dy);
        end.translate(dx, dy);

        super.setBoundingBox(calculateBoundingRectangle(start, end));

    }
}
