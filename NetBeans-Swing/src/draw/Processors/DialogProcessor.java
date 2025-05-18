package draw.Processors;

import draw.Model.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import draw.Model.Shape;
import java.io.*;
import java.io.IOException;
import javax.swing.*;



public class DialogProcessor extends DisplayProcessor {

    // == Селекция ==
    private Shape selection;
    private List<Shape> selectedShapes = new ArrayList<>();
    private Point lastLocation;
    private int activeHandleIndex = -1;

    // == Стилове по подразбиране за нови фигури ==
    private Color currentFillColor = Color.WHITE;
    private Color currentStrokeColor = Color.BLACK;
    private float currentStrokeWidth = 2.0f;
    private float currentAlpha = 1.0f;


    public void saveToFile(File file) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(shapeList);
            System.out.println("✅ Успешно запазено в " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("❌ Грешка при запис: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void loadFromFile(File file) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            shapeList = (List<Shape>) in.readObject();
            System.out.println("✅ Успешно зареден файл: " + file.getAbsolutePath());
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Грешка при зареждане: " + e.getMessage());
            e.printStackTrace();

        }
    }



    public void saveShapesToFile(File file) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(this.getShapeList());
        }
    }

    public void loadShapesFromFile(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            List<Shape> shapes = (List<Shape>) in.readObject();
            this.setShapeList(shapes);
        }
    }



    // ==== Добавяне на фигури ====
    public void AddRandomRectangle() {
        int x = 100 + (int)(Math.random() * 900);
        int y = 100 + (int)(Math.random() * 500);
        RectangleShape rect = new RectangleShape(new Rectangle(x, y, 100, 200));
        applyCurrentStyles(rect);
        shapeList.add(rect);
    }

    public void AddRandomEllipse() {
        int x = 100 + (int)(Math.random() * 900);
        int y = 100 + (int)(Math.random() * 500);
        EllipseShape ellipse = new EllipseShape(new Rectangle(x, y, 150, 100));
        applyCurrentStyles(ellipse);
        shapeList.add(ellipse);
    }

    public void AddRandomLine() {
        int x1 = 100 + (int)(Math.random() * 900);
        int y1 = 100 + (int)(Math.random() * 500);
        LineShape line = new LineShape(new Point(x1, y1), new Point(x1 + 150, y1 + 50));
        applyCurrentStyles(line);
        shapeList.add(line);
    }

    public void AddRandomPolygon() {
        int cx = 100 + (int)(Math.random() * 900);
        int cy = 100 + (int)(Math.random() * 500);
        Polygon polygon = new Polygon();
        for (int i = 0; i < 6; i++) {
            polygon.addPoint(
                    cx + (int)(50 * Math.cos(2 * Math.PI * i / 6)),
                    cy + (int)(50 * Math.sin(2 * Math.PI * i / 6))
            );
        }
        PolygonShape poly = new PolygonShape(polygon);
        applyCurrentStyles(poly);
        shapeList.add(poly);
    }

    public void AddRandomPoint() {
        int x = 10 + (int)(Math.random() * 900);
        int y = 10 + (int)(Math.random() * 500);
        PointShape point = new PointShape(new Point(x, y));
        applyCurrentStyles(point);
        shapeList.add(point);
    }

    private void applyCurrentStyles(Shape shape) {
        shape.setFillColor(currentFillColor);
        shape.setStrokeColor(currentStrokeColor);
        shape.setStrokeWidth(currentStrokeWidth);
        shape.setAlpha(currentAlpha);
    }

    // ==== Избор на фигура ====
    public Shape ContainsPoint(Point point) {
        for (int i = shapeList.size() - 1; i >= 0; i--) {
            if (shapeList.get(i).Contains(point)) {
                return shapeList.get(i);
            }
        }
        return null;
    }

    public Shape ContainsPoint(Point point, boolean addToSelection) {
        for (int i = shapeList.size() - 1; i >= 0; i--) {
            Shape s = shapeList.get(i);
            if (s.Contains(point)) {
                if (addToSelection) {
                    if (!selectedShapes.contains(s)) {
                        selectedShapes.add(s);
                    }
                } else {
                    selectedShapes.clear();
                    selectedShapes.add(s);
                }
                selection = s;
                return s;
            }
        }
        if (!addToSelection) {
            selectedShapes.clear();
            selection = null;
        }
        return null;
    }

    // ==== Групиране ====
    public void groupSelectedShapes() {
        if (selectedShapes.size() < 2) return;

        GroupShape group = new GroupShape(new ArrayList<>(selectedShapes));
        shapeList.removeAll(selectedShapes);
        shapeList.add(group);

        selectedShapes.clear();
        selection = null;

        repaint();
    }

    public void deleteSelectedShape() {
        if (selection != null) {
            shapeList.remove(selection);
            selectedShapes.remove(selection);
            selection = null;
            repaint();
        }
    }

    private Shape clipboard = null;

    public void copySelectedShape() {
        if (selection != null) {
            try {
                clipboard = (Shape) deepCopy(selection);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Shape deepCopy(Shape original) throws Exception {
        if (original instanceof GroupShape g) {
            List<Shape> copiedChildren = new ArrayList<>();
            for (Shape child : g.getChildren()) {
                copiedChildren.add(deepCopy(child)); // 🔁 рекурсивно копие
            }
            return new GroupShape(copiedChildren);
        } else if (original instanceof RectangleShape r) {
            return new RectangleShape(r);
        } else if (original instanceof EllipseShape e) {
            return new EllipseShape(e);
        } else if (original instanceof LineShape l) {
            return new LineShape(l);
        } else if (original instanceof PolygonShape p) {
            Polygon poly = p.getPolygon();
            return new PolygonShape(new Polygon(poly.xpoints, poly.ypoints, poly.npoints));
        } else if (original instanceof PointShape pt) {
            return new PointShape(pt.getLocation());
        } else {
            throw new IllegalArgumentException("Неподдържан тип фигура");
        }
    }

    public void pasteCopiedShape() {
        if (clipboard != null) {
            try {
                Shape pasted = (Shape) deepCopy(clipboard);
                Rectangle r = pasted.getRectangle();
                pasted.setRectangle(new Rectangle(r.x + 20, r.y + 20, r.width, r.height));
                shapeList.add(pasted);
                setSelection(pasted);
                repaint();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // ==== Преместване ====
    public void TranslateTo(Point p) {
        if (selection != null && lastLocation != null) {
            int dx = p.x - lastLocation.x;
            int dy = p.y - lastLocation.y;
            Rectangle r = selection.getRectangle();
            selection.setRectangle(new Rectangle(r.x + dx, r.y + dy, r.width, r.height));
            lastLocation = p;
        }
    }

    // ==== Resize ====
    public boolean isOverResizeHandle(Point p) {
        if (selection == null) return false;

        List<Rectangle> handles = selection.getResizeHandles();
        for (int i = 0; i < handles.size(); i++) {
            if (handles.get(i).contains(p)) {
                activeHandleIndex = i;
                return true;
            }
        }
        activeHandleIndex = -1;
        return false;
    }

    public boolean hasActiveResizeHandle() {
        return activeHandleIndex != -1;
    }

    public void ResizeTo(Point current) {
        if (selection == null || activeHandleIndex == -1) return;

        Rectangle r = selection.getRectangle();
        int minSize = 10;

        int newX = r.x;
        int newY = r.y;
        int newWidth = r.width;
        int newHeight = r.height;

        switch (activeHandleIndex) {
            case 0: // top-left
                newX = current.x;
                newY = current.y;
                newWidth = Math.max(minSize, r.x + r.width - current.x);
                newHeight = Math.max(minSize, r.y + r.height - current.y);
                break;
            case 1: // top-right
                newY = current.y;
                newWidth = Math.max(minSize, current.x - r.x);
                newHeight = Math.max(minSize, r.y + r.height - current.y);
                break;
            case 2: // bottom-left
                newX = current.x;
                newWidth = Math.max(minSize, r.x + r.width - current.x);
                newHeight = Math.max(minSize, current.y - r.y);
                break;
            case 3: // bottom-right
                newWidth = Math.max(minSize, current.x - r.x);
                newHeight = Math.max(minSize, current.y - r.y);
                break;
        }

        selection.setRectangle(new Rectangle(newX, newY, newWidth, newHeight));
    }

    public void clearActiveHandle() {
        activeHandleIndex = -1;
    }

    public void ungroupSelectedShape() {
        if (selection instanceof GroupShape) {
            GroupShape group = (GroupShape) selection;
            shapeList.remove(group);

            for (Shape child : group.getChildren()) {
                shapeList.add(child);
            }

            selectedShapes.clear();
            selection = null;

            repaint();
        }
    }

    // ==== Редакция на стил на селекцията ====
    public void updateSelectionStyle(Color fill, Color stroke, float strokeWidth, float alpha) {
        if (selection != null) {
            selection.setFillColor(fill);
            selection.setStrokeColor(stroke);
            selection.setStrokeWidth(strokeWidth);
            selection.setAlpha(alpha);
        }
    }

    // ==== Гетъри / Сетъри ====
    public Shape getSelection() { return selection; }
    public void setSelection(Shape s) { selection = s; }

    public Point getLastLocation() { return lastLocation; }
    public void setLastLocation(Point p) { lastLocation = p; }

    public List<Shape> getSelectedShapes() { return selectedShapes; }
    public void setSelectedShapes(List<Shape> shapes) {
        selectedShapes = shapes;
        selection = (shapes.size() == 1) ? shapes.get(0) : null;
    }

    public Color getCurrentFillColor() { return currentFillColor; }
    public void setCurrentFillColor(Color color) { currentFillColor = color; }

    public Color getCurrentStrokeColor() { return currentStrokeColor; }
    public void setCurrentStrokeColor(Color color) { currentStrokeColor = color; }

    public float getCurrentStrokeWidth() { return currentStrokeWidth; }
    public void setCurrentStrokeWidth(float width) { currentStrokeWidth = width; }

    public float getCurrentAlpha() { return currentAlpha; }
    public void setCurrentAlpha(float alpha) { currentAlpha = alpha; }


}
