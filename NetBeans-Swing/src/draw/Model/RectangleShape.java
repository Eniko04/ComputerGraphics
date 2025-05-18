package draw.Model;

import java.awt.*;

import java.awt.geom.AffineTransform;


/**
 * Класът правоъгълник е основен примитив, който е наследник на базовия Shape.
 */
public class RectangleShape extends draw.Model.Shape {

    public RectangleShape(Rectangle rect) {
        super(rect);
    }

    public RectangleShape(draw.Model.RectangleShape rectangle) {
        super(rectangle); // наследява свойствата от другия правоъгълник
    }

    /**
     * Проверка за принадлежност на точка point към правоъгълника.
     */
    @Override
    public boolean Contains(Point point) {
        return super.Contains(point);
    }

    /**
     * Частта, визуализираща конкретния примитив с използване на stroke, fill, alpha и др.
     */

    @Override
    public void DrawSelf(Graphics grfx) {
        super.DrawSelf(grfx);

        Graphics2D g = (Graphics2D) grfx;
        Rectangle r = getRectangle();

        Composite originalComposite = g.getComposite();
        AffineTransform originalTransform = g.getTransform();

        // Център на правоъгълника
        double cx = r.getCenterX();
        double cy = r.getCenterY();

        // Прозрачност
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getAlpha()));

        // Преобразувания: мащабиране и завъртане
        g.translate(cx, cy);
        g.rotate(Math.toRadians(getRotationAngle()));
        g.scale(getScaleX(), getScaleY());
        g.translate(-cx, -cy);

        // Рисуване на запълване
        g.setColor(getFillColor());
        g.fillRect(r.x, r.y, r.width, r.height);

        // Рисуване на контур
        g.setColor(getStrokeColor());
        g.setStroke(new BasicStroke(getStrokeWidth()));
        g.drawRect(r.x, r.y, r.width, r.height);

        // Възстановяване
        g.setTransform(originalTransform);
        g.setComposite(originalComposite);
    }

}
