package draw.Model;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

/**
 * Класът елипса е основен примитив, който е наследник на базовия Shape.
 */
public class EllipseShape extends Shape {

    public EllipseShape(Rectangle rect) {
        super(rect);
    }

    public EllipseShape(EllipseShape ellipse) {
        super(ellipse);
    }

    /**
     * Проверка дали точка попада вътре в трансформираната елипса.
     */
    @Override
    public boolean Contains(Point point) {
        Rectangle r = getRectangle();
        Ellipse2D ellipse = new Ellipse2D.Double(-r.width / 2.0, -r.height / 2.0, r.width, r.height);

        // Обратна трансформация на точката
        AffineTransform transform = new AffineTransform();
        transform.translate(r.x + r.width / 2.0, r.y + r.height / 2.0);
        transform.rotate(Math.toRadians(getRotationAngle()));
        transform.scale(getScaleX(), getScaleY());

        try {
            AffineTransform inverse = transform.createInverse();
            Point transformedPoint = (Point) inverse.transform(point, new Point());
            return ellipse.contains(transformedPoint);
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Визуализиране на елипсата с трансформации.
     */
    @Override
    public void DrawSelf(Graphics grfx) {
        Graphics2D g = (Graphics2D) grfx.create();
        Rectangle r = getRectangle();

        // Център на елипсата
        int cx = r.x + r.width / 2;
        int cy = r.y + r.height / 2;

        // Приложи трансформации
        g.translate(cx, cy);
        g.rotate(Math.toRadians(getRotationAngle()));
        g.scale(getScaleX(), getScaleY());

        Composite originalComposite = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getAlpha()));

        // Рисуване спрямо центъра
        g.setColor(getFillColor());
        g.fillOval(-r.width / 2, -r.height / 2, r.width, r.height);

        g.setColor(getStrokeColor());
        g.setStroke(new BasicStroke(getStrokeWidth()));
        g.drawOval(-r.width / 2, -r.height / 2, r.width, r.height);

        g.setComposite(originalComposite);
        g.dispose();

        // ⬇️ Добавено: показване на името под елипсата
        super.DrawSelf(grfx);
    }

}
