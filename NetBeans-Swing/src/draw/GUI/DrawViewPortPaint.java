package draw.GUI;

import draw.Model.Shape;
import draw.Processors.DialogProcessor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.HierarchyEvent;

public class DrawViewPortPaint extends JPanel {

    private final DrawView parent;
    private final DialogProcessor processor; // Вече е final и задължително се инициализира

    // Конструктор, използващ processor от parent
    public DrawViewPortPaint(DrawView parent) {
        this(parent, parent.getDialogProcessor());
    }

    // Основен конструктор с подаден processor
    public DrawViewPortPaint(DrawView parent, DialogProcessor processor) {
        super();
        this.parent = parent;
        this.processor = processor;

        setLayout(new BorderLayout());
        setFocusable(true);

        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                requestFocusInWindow();
            }
        });

        InputMap inputMap = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = this.getActionMap();

        // Изтриване
        inputMap.put(KeyStroke.getKeyStroke("control D"), "deleteShape");
        inputMap.put(KeyStroke.getKeyStroke("DELETE"), "deleteShape");
        actionMap.put("deleteShape", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processor.deleteSelectedShape();
                updateStatus("Последно действие: Изтриване");
                repaint();
            }
        });

        // Копиране
        inputMap.put(KeyStroke.getKeyStroke("control C"), "copyShape");
        actionMap.put("copyShape", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processor.copySelectedShape();
                updateStatus("Последно действие: Копиране");
            }
        });

        // Поставяне
        inputMap.put(KeyStroke.getKeyStroke("control V"), "pasteShape");
        actionMap.put("pasteShape", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processor.pasteCopiedShape();
                updateStatus("Последно действие: Поставяне");
                repaint();
            }
        });

        // Селекция под курсора
        inputMap.put(KeyStroke.getKeyStroke("control S"), "selectUnderCursor");
        actionMap.put("selectUnderCursor", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PointerInfo pointerInfo = MouseInfo.getPointerInfo();
                if (pointerInfo == null) return;

                Point location = pointerInfo.getLocation();
                SwingUtilities.convertPointFromScreen(location, DrawViewPortPaint.this);
                var found = processor.ContainsPoint(location);

                if (found != null) {
                    found.setFillColor(Color.RED);
                    processor.setSelectedShapes(new java.util.ArrayList<>(java.util.List.of(found)));
                    processor.setSelection(found);
                    updateStatus("Селектирана фигура под курсора");
                    processor.repaint();
                }
            }
        });

        // Добавяне на фигури
        inputMap.put(KeyStroke.getKeyStroke("control 1"), "addPolygon");
        actionMap.put("addPolygon", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processor.AddRandomPolygon();
                updateStatus("Добавен многоъгълник");
                repaint();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("control 2"), "addEllipse");
        actionMap.put("addEllipse", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processor.AddRandomEllipse();
                updateStatus("Добавена елипса");
                repaint();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("control 3"), "addRectangle");
        actionMap.put("addRectangle", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processor.AddRandomRectangle();
                updateStatus("Добавен правоъгълник");
                repaint();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("control 4"), "addPoint");
        actionMap.put("addPoint", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processor.AddRandomPoint();
                updateStatus("Добавена точка");
                repaint();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("control 5"), "addLine");
        actionMap.put("addLine", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processor.AddRandomLine();
                updateStatus("Добавена линия");
                repaint();
            }
        });

        // Селекция на всички фигури
        inputMap.put(KeyStroke.getKeyStroke("control A"), "selectAllShapes");
        actionMap.put("selectAllShapes", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                var allShapes = processor.getShapeList();
                if (!allShapes.isEmpty()) {
                    for (Shape s : allShapes) {
                        s.setFillColor(Color.RED);
                    }
                    processor.setSelectedShapes(new java.util.ArrayList<>(allShapes));
                    processor.setSelection(allShapes.get(0));
                    updateStatus("Селектирани всички фигури");
                    processor.repaint();
                }
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("control X"), "rotateSelectedShape");

        actionMap.put("rotateSelectedShape", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                var processor = parent.getDialogProcessor();
                var selected = processor.getSelection();

                if (selected != null) {
                    double currentAngle = selected.getRotationAngle();
                    selected.setRotationAngle(currentAngle + 30); // върти с 30°
                    if (parent.getStatusMessageLabel() != null) {
                        parent.getStatusMessageLabel().setText("Завъртана фигура с 30°");
                    }
                    processor.repaint();
                }
            }
        });

    }

    private void updateStatus(String msg) {
        if (parent.getStatusMessageLabel() != null) {
            parent.getStatusMessageLabel().setText(msg);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension layoutSize = super.getPreferredSize();
        int max = Math.max(layoutSize.width, layoutSize.height);
        return new Dimension(max + 100, max + 100);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        processor.ReDraw(this, g);
    }

    public DialogProcessor getProcessor() {
        return processor;
    }



}
