package draw.Processors;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import draw.GUI.DrawApp;
import draw.Model.Shape;

/**
 * Класът, който ще бъде използван при управляване на дисплейната система.
 */
public class DisplayProcessor {
    /**
     * Списък с всички елементи формиращи изображението.
     */
    public List<Shape> shapeList = new ArrayList<>();

    public DisplayProcessor() {
    }

    /**
     * Прерисува всички елементи в shapeList върху grfx
     */
    public void ReDraw(Object sender, Graphics grfx) {
        Graphics2D grfx2 = (Graphics2D) grfx;
        grfx2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        Draw(grfx);
    }

    /**
     * Визуализация.
     * Обхождане на всички елементи в списъка и извикване на визуализиращия им метод.
     * @param grfx - Къде да се извърши визуализацията.
     */
    public void Draw(Graphics grfx) {
        for (Shape shape : shapeList) {
            DrawShape(grfx, shape);
        }
    }

    /**
     * Визуализира даден елемент от изображението.
     * @param grfx - Къде да се извърши визуализацията.
     * @param item - Елемент за визуализиране.
     */
    public void DrawShape(Graphics grfx, Shape item) {
        item.DrawSelf(grfx);
    }

    // Гетъри и сетъри
    public List<Shape> getShapeList() {
        return shapeList;
    }

    public void setShapeList(List<Shape> value) {
        shapeList = value;
    }

    public void repaint() {
        DrawApp.getApplication().getMainView().getComponent().repaint();
    }
}
