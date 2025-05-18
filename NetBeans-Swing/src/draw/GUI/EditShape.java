package draw.GUI;

import draw.Model.Shape;
import draw.Model.GroupShape;
import draw.Processors.DialogProcessor;

import javax.swing.*;
import java.awt.*;

public class EditShape {

    public static void showEditDialog(Component parent, DialogProcessor dialogProcessor) {
        Shape selected = dialogProcessor.getSelection();
        if (selected == null) {
            JOptionPane.showMessageDialog(parent, "Няма избран примитив.", "Грешка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));

        // Цветове
        Color[] newFillColor = { selected.getFillColor() };
        Color[] newStrokeColor = { selected.getStrokeColor() };

        JButton fillColorButton = new JButton("Цвят на запълване");
        fillColorButton.addActionListener(e -> {
            Color color = JColorChooser.showDialog(parent, "Избор на цвят на запълване", newFillColor[0]);
            if (color != null) newFillColor[0] = color;
        });

        JButton strokeColorButton = new JButton("Цвят на контур");
        strokeColorButton.addActionListener(e -> {
            Color color = JColorChooser.showDialog(parent, "Избор на цвят на контур", newStrokeColor[0]);
            if (color != null) newStrokeColor[0] = color;
        });

        // Дебелина
        SpinnerNumberModel strokeModel = new SpinnerNumberModel(selected.getStrokeWidth(), 0.1, 20.0, 0.5);
        JSpinner strokeSpinner = new JSpinner(strokeModel);

        // Прозрачност
        JSlider alphaSlider = new JSlider(0, 100, (int)(selected.getAlpha() * 100));
        alphaSlider.setMajorTickSpacing(25);
        alphaSlider.setPaintTicks(true);
        alphaSlider.setPaintLabels(true);

        // Широчина и височина
        SpinnerNumberModel widthModel = new SpinnerNumberModel(selected.getWidth(), 10, 2000, 10);
        SpinnerNumberModel heightModel = new SpinnerNumberModel(selected.getHeight(), 10, 2000, 10);
        JSpinner widthSpinner = new JSpinner(widthModel);
        JSpinner heightSpinner = new JSpinner(heightModel);

        // Поле за име
        JTextField nameField = new JTextField(selected.getName());
        panel.add(new JLabel("Име:"));
        panel.add(nameField);

        // Поле за задаване на цвят чрез име
        JTextField fillColorNameField = new JTextField();
        panel.add(new JLabel("Цвят (име):"));
        panel.add(fillColorNameField);

        // Добавяне на останалите компоненти
        panel.add(fillColorButton);
        panel.add(strokeColorButton);
        panel.add(new JLabel("Дебелина на линия:"));
        panel.add(strokeSpinner);
        panel.add(new JLabel("Прозрачност:"));
        panel.add(alphaSlider);
        panel.add(new JLabel("Широчина:"));
        panel.add(widthSpinner);
        panel.add(new JLabel("Височина:"));
        panel.add(heightSpinner);

        int result = JOptionPane.showConfirmDialog(parent, panel, "Редакция на примитив", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            float strokeWidth = ((Number) strokeSpinner.getValue()).floatValue();
            float alpha = alphaSlider.getValue() / 100f;
            int width = (Integer) widthSpinner.getValue();
            int height = (Integer) heightSpinner.getValue();

            if (selected instanceof GroupShape group) {
                group.applyStyleToAll(newFillColor[0], newStrokeColor[0], strokeWidth, alpha);
            } else {
                selected.setFillColor(newFillColor[0]);
                selected.setStrokeColor(newStrokeColor[0]);
                selected.setStrokeWidth(strokeWidth);
                selected.setAlpha(alpha);
            }

            // Скалиране (само за единични фигури)
            if (!(selected instanceof GroupShape)) {
                Rectangle old = selected.getRectangle();
                selected.setRectangle(new Rectangle(old.x, old.y, width, height));
            }

            // Име
            String enteredName = nameField.getText().trim();
            selected.setName(enteredName);

            // Опитваме се да зададем цвят чрез текстово име
            String colorName = fillColorNameField.getText().trim().toLowerCase();
            Color nameColor = getColorByName(colorName);
            if (nameColor != null) {
                selected.setFillColor(nameColor);
            }

            dialogProcessor.repaint();
        }
    }

    private static Color getColorByName(String name) {
        return switch (name) {
            case "червен", "red" -> Color.RED;
            case "зелен", "green" -> Color.GREEN;
            case "син", "blue" -> Color.BLUE;
            case "жълт", "yellow" -> Color.YELLOW;
            case "черен", "black" -> Color.BLACK;
            case "бял", "white" -> Color.WHITE;
            case "сив", "gray" -> Color.GRAY;
            case "оранжев", "orange" -> Color.ORANGE;
            case "розов", "pink" -> Color.PINK;
            default -> null;
        };
    }
}
