package draw.Model;

import java.awt.*;
import java.awt.geom.AffineTransform;


public class PolygonShape extends Shape {
    private Polygon polygon;

    public PolygonShape(Polygon polygon) {
        super(polygon.getBounds());
        this.polygon = polygon;
    }



    @Override
    public boolean Contains(Point point) {
        AffineTransform transform = new AffineTransform();
        Rectangle bounds = polygon.getBounds();

        double centerX = bounds.getX() + bounds.getWidth() / 2;
        double centerY = bounds.getY() + bounds.getHeight() / 2;

        transform.translate(centerX, centerY);
        transform.rotate(Math.toRadians(getRotationAngle()));
        transform.scale(getScaleX(), getScaleY());
        transform.translate(-centerX, -centerY);

        java.awt.Shape transformed = transform.createTransformedShape(polygon);


        return transformed.contains(point);
    }

    @Override
    public void DrawSelf(Graphics grfx) {
        Graphics2D g = (Graphics2D) grfx.create();
        Rectangle bounds = polygon.getBounds();

        double centerX = bounds.getX() + bounds.getWidth() / 2;
        double centerY = bounds.getY() + bounds.getHeight() / 2;

        g.translate(centerX, centerY);
        g.rotate(Math.toRadians(getRotationAngle()));
        g.scale(getScaleX(), getScaleY());
        g.translate(-centerX, -centerY);

        Composite originalComposite = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getAlpha()));

        g.setColor(getFillColor());
        g.fillPolygon(polygon);

        g.setColor(getStrokeColor());
        g.setStroke(new BasicStroke(getStrokeWidth()));
        g.drawPolygon(polygon);

        g.setComposite(originalComposite);
        g.dispose();

        // ⬇️ Показване на името под многоъгълника
        super.DrawSelf(grfx);
    }


    @Override
    public Point getLocation() {
        return polygon.getBounds().getLocation();
    }

    @Override
    public void setLocation(Point newLocation) {
        Rectangle bounds = polygon.getBounds();
        int dx = newLocation.x - bounds.x;
        int dy = newLocation.y - bounds.y;
        polygon.translate(dx, dy);
        setBoundingBox(polygon.getBounds());
    }

    public Polygon getPolygon() {
        return polygon;
    }

    @Override
    public Rectangle getRectangle() {
        return polygon.getBounds();
    }

    @Override
    public void setRectangle(Rectangle newBounds) {
        Rectangle oldBounds = polygon.getBounds();
        double scaleX = newBounds.getWidth() / oldBounds.getWidth();
        double scaleY = newBounds.getHeight() / oldBounds.getHeight();

        int[] xpoints = polygon.xpoints;
        int[] ypoints = polygon.ypoints;
        int n = polygon.npoints;

        Polygon newPoly = new Polygon();

        for (int i = 0; i < n; i++) {
            int newX = (int) (newBounds.x + (xpoints[i] - oldBounds.x) * scaleX);
            int newY = (int) (newBounds.y + (ypoints[i] - oldBounds.y) * scaleY);
            newPoly.addPoint(newX, newY);
        }

        this.polygon = newPoly;
        setBoundingBox(polygon.getBounds());
    }
}
