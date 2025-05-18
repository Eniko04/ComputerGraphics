package draw.Model;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Клас за групиране на фигури.
 */
public class GroupShape extends Shape {

    private List<Shape> children = new ArrayList<>();

    public GroupShape(List<Shape> shapes) {
        super(calculateBoundingBox(shapes));
        this.children.addAll(shapes);
    }

    public List<Shape> getChildren() {
        return children;
    }

    private static Rectangle calculateBoundingBox(List<Shape> shapes) {
        if (shapes.isEmpty()) return new Rectangle(0, 0, 0, 0);

        Rectangle bounds = new Rectangle(shapes.get(0).getRectangle());
        for (int i = 1; i < shapes.size(); i++) {
            bounds = bounds.union(shapes.get(i).getRectangle());
        }
        return bounds;
    }

    @Override
    public boolean Contains(Point point) {
        for (Shape shape : children) {
            if (shape.Contains(point)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void DrawSelf(Graphics grfx) {
        Graphics2D g = (Graphics2D) grfx.create();
        Rectangle r = getRectangle();

        int cx = r.x + r.width / 2;
        int cy = r.y + r.height / 2;

        g.translate(cx, cy);
        g.rotate(Math.toRadians(getRotationAngle())); // 🔄 Прилага се ротацията за цялата група
        g.scale(getScaleX(), getScaleY());
        g.translate(-cx, -cy);

        for (Shape child : children) {
            child.DrawSelf(g); // рисува всяка подфигура със зададената трансформация
        }

        g.dispose();
    }


    @Override
    public void setLocation(Point newLocation) {
        Point oldLocation = getLocation();
        int dx = newLocation.x - oldLocation.x;
        int dy = newLocation.y - oldLocation.y;

        for (Shape shape : children) {
            shape.setLocation(new Point(
                    shape.getLocation().x + dx,
                    shape.getLocation().y + dy));
        }

        super.setRectangle(calculateBoundingBox(children));
    }

    @Override
    public void setRectangle(Rectangle newBounds) {
        Rectangle oldBounds = getRectangle();
        double scaleX = newBounds.getWidth() / oldBounds.getWidth();
        double scaleY = newBounds.getHeight() / oldBounds.getHeight();

        for (Shape shape : children) {
            Rectangle r = shape.getRectangle();

            int newX = newBounds.x + (int)((r.x - oldBounds.x) * scaleX);
            int newY = newBounds.y + (int)((r.y - oldBounds.y) * scaleY);
            int newW = (int)(r.width * scaleX);
            int newH = (int)(r.height * scaleY);

            shape.setRectangle(new Rectangle(newX, newY, newW, newH));
        }

        super.setRectangle(calculateBoundingBox(children)); // 🔧 правилен начин
    }

    @Override
    public void rotate(double angle) {
        for (Shape shape : children) {
            shape.rotate(angle);
        }
    }

    @Override
    public List<Rectangle> getResizeHandles() {
        return super.getResizeHandles(); // дръжки на bounding box-а
    }

    public void applyStyleToAll(Color fill, Color stroke, float strokeWidth, float alpha) {
        for (Shape shape : children) {
            if (shape instanceof GroupShape group) {
                group.applyStyleToAll(fill, stroke, strokeWidth, alpha); // рекурсивно
            } else {
                shape.setFillColor(fill);
                shape.setStrokeColor(stroke);
                shape.setStrokeWidth(strokeWidth);
                shape.setAlpha(alpha);
            }
        }
    }

}
