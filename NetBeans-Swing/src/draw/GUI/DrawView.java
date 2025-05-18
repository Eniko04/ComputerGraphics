/*
 * DrawView.java
 */

package draw.GUI;

import org.jdesktop.application.Action;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import org.jdesktop.application.ResourceMap;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import draw.Processors.DialogProcessor;

import java.awt.event.MouseEvent;
import java.io.File;


/**
 * The application's main frame.
 */
public class DrawView extends FrameView {

    /**
     * Агрегирания диалогов процесор. Улеснява манипулацията на модела.
     */
    private DialogProcessor dialogProcessor;

    /**
     * Достъп до доалоговия процесор.
     * @return Инстанцията на диалоговия процесор
     */
    public DialogProcessor getDialogProcessor() {
        return dialogProcessor;
    }

    public DialogProcessor getActiveProcessor() {
        JInternalFrame frame = desktop.getSelectedFrame();
        if (frame != null && frame.getContentPane() instanceof DrawViewPortPaint view) {
            return view.getProcessor(); // взима processora от текущия изглед
        }
        return null;
    }


    public DrawView(SingleFrameApplication app) {
        super(app);

        initComponents();


        desktop = new JDesktopPane();
        setComponent(desktop); // заменя viewPort


        // 🔽 Добавено: преоразмеряване на иконата на бутона за елипса
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/draw/GUI/resources/Ellipse.png"));
            Image scaledImage = icon.getImage().getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);
            DrawEllipseButton.setIcon(new ImageIcon(scaledImage));
            DrawEllipseButton.setText(""); // Премахва надписа под иконата
            DrawEllipseButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            DrawEllipseButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        } catch (Exception ex) {
            System.err.println("Неуспешно зареждане на Ellipse.png: " + ex.getMessage());
        }

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(draw.GUI.DrawApp.class).getContext().getActionMap(DrawView.class, this);


        DrawLineButton = new javax.swing.JButton(); // ✅ Първо създаваме бутона
        DrawLineButton.setAction(actionMap.get("drawLine"));
        DrawLineButton.setText("");
        DrawLineButton.setFocusable(false);
        DrawLineButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        DrawLineButton.setName("DrawLineButton");
        DrawLineButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        DrawPolygonButton = new javax.swing.JButton();
        DrawPolygonButton.setAction(actionMap.get("drawPolygon")); // същото име като горния метод
        DrawPolygonButton.setText(""); // без надпис
        DrawPolygonButton.setFocusable(false);
        DrawPolygonButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        DrawPolygonButton.setName("DrawPolygonButton");
        DrawPolygonButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(DrawPolygonButton);

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/draw/GUI/resources/Polygon.png"));
            Image scaledImage = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            DrawPolygonButton.setIcon(new ImageIcon(scaledImage));
        } catch (Exception ex) {
            System.err.println("Грешка при зареждане на Polygon.png: " + ex.getMessage());
        }

        DrawPointButton = new javax.swing.JButton();
        DrawPointButton.setAction(actionMap.get("drawPoint"));
        DrawPointButton.setText("");
        DrawPointButton.setFocusable(false);
        DrawPointButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        DrawPointButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        DrawPointButton.setName("DrawPointButton");

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/draw/GUI/resources/PointTool.png"));
            Image scaled = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            DrawPointButton.setIcon(new ImageIcon(scaled));
        } catch (Exception ex) {
            System.err.println("Грешка при зареждане на икона за точка: " + ex.getMessage());
        }




        toolBar.add(DrawEllipseButton);
        toolBar.add(DrawRectangleButton);
        toolBar.add(DrawPointButton);
        toolBar.add(DrawLineButton);       // 🟢 Добавяме бутона за линия преди стрелката
        toolBar.add(DragToggleButton);     // Стрелката остава последна
// ✅ Добави го в toolBar

// ✅ Сега вече може да зададеш икона
        try {
            java.net.URL imgUrl = getClass().getResource("/draw/GUI/resources/Line.png");

            ImageIcon icon = new ImageIcon(imgUrl);
            Image scaledImage = icon.getImage().getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);
            DrawLineButton.setIcon(new ImageIcon(scaledImage));
        } catch (Exception ex) {
            System.err.println("Грешка при зареждане на Line.png: " + ex.getMessage());
        }

        JMenuItem editShapeItem = new JMenuItem("Редакция на примитив");
        editShapeItem.addActionListener(e -> {
            DialogProcessor dp = getActiveProcessor();
            if (dp != null) {
                EditShape.showEditDialog(this.getComponent(), dp);
            }
        });

        editMenu.addSeparator();
        editMenu.add(editShapeItem);

        //Group
        JButton groupButton = new JButton();
        groupButton.setFocusable(false);
        groupButton.setToolTipText("Групирай избраните фигури");




        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/draw/GUI/resources/Group.png"));
            Image scaled = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            groupButton.setIcon(new ImageIcon(scaled));
        } catch (Exception ex) {
            System.err.println("Грешка при зареждане на иконата за групиране: " + ex.getMessage());
        }

// Събитието при клик
        groupButton.addActionListener(e -> {
            DialogProcessor dp = getActiveProcessor();
            if (dp != null) {
                dp.groupSelectedShapes();
                statusMessageLabel.setText("Последно действие: Групиране");
            }
        });


        toolBar.add(groupButton);

        JButton ungroupButton = new JButton();
        ungroupButton.setFocusable(false);
        ungroupButton.setToolTipText("Разгрупирай избрана група");

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/draw/GUI/resources/Ungroup.png"));
            Image scaled = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            ungroupButton.setIcon(new ImageIcon(scaled));
        } catch (Exception ex) {
            System.err.println("Грешка при зареждане на иконата за разгрупиране: " + ex.getMessage());
            ungroupButton.setText("Ungroup"); // fallback текст
        }

        ungroupButton.addActionListener(e -> {
            DialogProcessor dp = getActiveProcessor();
            if (dp != null) {
                dp.ungroupSelectedShape();
                statusMessageLabel.setText("Последно действие: Разгрупиране");
            }
        });


        toolBar.add(ungroupButton);



        // Създаваме поле за рисуване и го правим главен компонент в изгледа.
        //DrawViewPortPaint drawViewPortPaint = new draw.GUI.DrawViewPortPaint(this);
        //setComponent(drawViewPortPaint);
        // Прихващане на събитията на мишката.
        // Прихващане на събитията на мишката – комбинирано и коригирано

        dialogProcessor = new DialogProcessor();
        createNewView(dialogProcessor, "Главен изглед");


        // status bar, timers, icons и т.н. (всичко остава както си го имаш)
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);

        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }

        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });

        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        ImageIcon mainIcon = resourceMap.getImageIcon("DrawIcon");
        getFrame().setIconImage(mainIcon.getImage());

        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String) (evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer) (evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
    }


    /**
     * Показва диалогова кутия с информация за програмата.
     */
    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = DrawApp.getApplication().getMainFrame();
            aboutBox = new DrawAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        DrawApp.getApplication().show(aboutBox);
    }

    /**
     * Бутон, който поставя на произволно място правоъгълник със зададените размери.
     * Променя се лентата със състоянието и се инвалидира контрола, в който визуализираме.
     */
    @Action
    public void drawRectangle() {
        DialogProcessor dp = getActiveProcessor();
        if (dp != null) {
            dp.AddRandomRectangle();
            statusMessageLabel.setText("Последно действие: Рисуване на правоъгълник");
            dp.repaint();
        }
    }
    public void createNewView(DialogProcessor processor, String title) {
        DrawViewPortPaint view = new DrawViewPortPaint(this, processor);

        // ➕ ДОБАВЯНЕ НА MOUSE LISTENER ЗА СЕЛЕКЦИЯ И ДРАГ
        view.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                if (DragToggleButton.isSelected()) {
                    DialogProcessor dp = getActiveProcessor();
                    if (dp == null) return;

                    Point p = evt.getPoint();

                    if (dp.isOverResizeHandle(p)) {
                        dp.setLastLocation(p);
                        statusMessageLabel.setText("Последно действие: Започнато мащабиране");
                    } else {
                        boolean ctrl = (evt.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) != 0;
                        dp.ContainsPoint(p, ctrl);

                        if (!dp.getSelectedShapes().isEmpty()) {
                            for (var shape : dp.getSelectedShapes()) {
                                if (shape.getFillColor() == null || shape.getFillColor().equals(Color.WHITE)) {
                                    shape.setFillColor(Color.RED);
                                }
                            }
                            dp.setLastLocation(p);
                            statusMessageLabel.setText("Последно действие: Селекция");
                            dp.repaint();
                        }
                    }
                }
            }

            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    DialogProcessor dp = getActiveProcessor();
                    if (dp == null) return;

                    var selected = dp.getSelection();
                    if (selected != null) {
                        selected.rotate(30);
                        statusMessageLabel.setText("Последно действие: Завъртане");
                        dp.repaint();
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                DialogProcessor dp = getActiveProcessor();
                if (dp != null) {
                    dp.clearActiveHandle();
                }
            }
        });

        view.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent evt) {
                if (DragToggleButton.isSelected()) {
                    DialogProcessor dp = getActiveProcessor();
                    if (dp == null) return;

                    if (dp.hasActiveResizeHandle()) {
                        dp.ResizeTo(evt.getPoint());
                    } else if (dp.getSelection() != null) {
                        dp.TranslateTo(evt.getPoint());
                    }
                    dp.repaint();
                }
            }
        });

        // ➕ ДОБАВЯНЕ НА VIEW КЪМ НОВ ПРОЗОРЕЦ
        JInternalFrame frame = new JInternalFrame(title, true, true, true, true);
        frame.setContentPane(view);
        frame.pack();
        frame.setVisible(true);

        desktop.add(frame);
        try {
            frame.setSelected(true);
        } catch (Exception ignored) {}
    }



    // МАХНИ ТОЗИ МЕТОД:
    @Action
    public void newView() {
        DialogProcessor newProcessor = new DialogProcessor(); // нов празен модел
        createNewView(newProcessor, "Нов изглед");
    }






    @Action
    public void drawEllipse() {
        DialogProcessor dp = getActiveProcessor();
        if (dp != null) {
            dp.AddRandomEllipse();
            statusMessageLabel.setText("Последно действие: Рисуване на елипса");
            dp.repaint();
        }
    }

    @Action
    public void drawLine() {
        DialogProcessor dp = getActiveProcessor();
        if (dp != null) {
            dp.AddRandomLine();
            statusMessageLabel.setText("Последно действие: Рисуване на линия");
            dp.repaint();
        }
    }

    @Action
    public void drawPolygon() {
        DialogProcessor dp = getActiveProcessor();
        if (dp != null) {
            dp.AddRandomPolygon();
            statusMessageLabel.setText("Последно действие: Рисуване на многоъгълник");
            dp.repaint();
        }
    }


    @Action
    public void drawPoint() {
        DialogProcessor dp = getActiveProcessor();
        if (dp != null) {
            dp.AddRandomPoint();
            statusMessageLabel.setText("Последно действие: Рисуване на точка");
            dp.repaint();
        }
    }



    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem newViewMenuItem = new javax.swing.JMenuItem();  // само този остава
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        imageMenu = new javax.swing.JMenu();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        toolBar = new javax.swing.JToolBar();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(draw.GUI.DrawApp.class).getContext().getResourceMap(DrawView.class);
        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(draw.GUI.DrawApp.class).getContext().getActionMap(DrawView.class, this);

        fileMenu.setText("File");

        newViewMenuItem.setAction(actionMap.get("newView"));
        newViewMenuItem.setText("New View");
        fileMenu.add(newViewMenuItem);

// Save
        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File("view.json"));
            if (fileChooser.showSaveDialog(getComponent()) == JFileChooser.APPROVE_OPTION) {
                DialogProcessor dp = getActiveProcessor();
                if (dp != null) {
                    dp.saveToFile(fileChooser.getSelectedFile());
                    statusMessageLabel.setText("Файлът е запазен.");
                }
            }
        });
        fileMenu.add(saveItem);

        JMenuItem saveAllItem = new JMenuItem("Save All");
        saveAllItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // 📁 изискваме папка

            if (fileChooser.showSaveDialog(getComponent()) == JFileChooser.APPROVE_OPTION) {
                File directory = fileChooser.getSelectedFile();
                if (!directory.exists()) directory.mkdirs();

                JInternalFrame[] frames = desktop.getAllFrames();
                int index = 1;
                for (JInternalFrame frame : frames) {
                    if (frame.getContentPane() instanceof DrawViewPortPaint view) {
                        DialogProcessor dp = view.getProcessor();
                        File file = new File(directory, "view" + index + ".ser");
                        dp.saveToFile(file);
                        index++;
                    }
                }

                statusMessageLabel.setText("Запазени са всички изгледи.");
            }
        });
        fileMenu.add(saveAllItem); // ➕ Добавяме бутона към менюто
// ✅

        JMenuItem loadItem = new JMenuItem("Load");
        loadItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(getComponent()) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                DialogProcessor dp = getActiveProcessor();
                if (dp == null) {
                    dp = new DialogProcessor();
                    createNewView(dp, "Зареден изглед");
                }
                dp.loadFromFile(selectedFile);
                dp.repaint();
                statusMessageLabel.setText("Файлът е зареден.");
            }
        });
        fileMenu.add(loadItem);

        JMenuItem loadAllItem = new JMenuItem("Load All");
        loadAllItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // 📁 изискваме папка

            if (fileChooser.showOpenDialog(getComponent()) == JFileChooser.APPROVE_OPTION) {
                File directory = fileChooser.getSelectedFile();
                File[] files = directory.listFiles((dir, name) -> name.endsWith(".ser"));
                if (files != null) {
                    for (File file : files) {
                        DialogProcessor dp = new DialogProcessor();
                        dp.loadFromFile(file);
                        createNewView(dp, "Зареден: " + file.getName());
                    }
                    statusMessageLabel.setText("Заредени са всички изгледи от папката.");
                }
            }
        });
        fileMenu.add(loadAllItem); // ➕ Добавяме бутона към менюто


// Exit
        exitMenuItem.setAction(actionMap.get("quit"));
        exitMenuItem.setText("Exit");
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);




        // Edit menu
        editMenu.setText("Edit");
        menuBar.add(editMenu);

        // Image menu
        imageMenu.setText("Image");
        menuBar.add(imageMenu);

        // Help menu
        helpMenu.setText("Help");
        aboutMenuItem.setAction(actionMap.get("showAboutBox"));
        aboutMenuItem.setText("About");
        helpMenu.add(aboutMenuItem);
        menuBar.add(helpMenu);

        // Status bar
        statusPanel.setLayout(new java.awt.BorderLayout());
        statusPanel.add(statusPanelSeparator, java.awt.BorderLayout.NORTH);

        JPanel statusContent = new JPanel(new BorderLayout());
        statusContent.add(statusMessageLabel, BorderLayout.WEST);
        JPanel rightStatus = new JPanel();
        rightStatus.add(progressBar);
        rightStatus.add(statusAnimationLabel);
        statusContent.add(rightStatus, BorderLayout.EAST);
        statusPanel.add(statusContent, BorderLayout.SOUTH);

        // Toolbar
        toolBar.setRollover(true);

        DrawRectangleButton = new javax.swing.JButton(actionMap.get("drawRectangle"));
        DrawRectangleButton.setText("");
        DrawRectangleButton.setIcon(resourceMap.getIcon("DrawRectangleButton.icon"));
        toolBar.add(DrawRectangleButton);

        DrawEllipseButton = new javax.swing.JButton(actionMap.get("drawEllipse"));
        DrawEllipseButton.setText("");
        DrawEllipseButton.setIcon(resourceMap.getIcon("DrawEllipseButton.icon"));
        toolBar.add(DrawEllipseButton);

        DragToggleButton = new javax.swing.JToggleButton();
        DragToggleButton.setActionCommand("drag");
        DragToggleButton.setIcon(resourceMap.getIcon("DragToggleButton.icon"));
        DragToggleButton.setText("");
        toolBar.add(DragToggleButton);

        // Set main components
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
        setToolBar(toolBar);
        setComponent(desktop);  // important for multi-window support
    }



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton DragToggleButton;
    private javax.swing.JButton DrawRectangleButton;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenu imageMenu;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JPanel viewPort;
    private javax.swing.JButton DrawEllipseButton;
    private javax.swing.JButton DrawLineButton;
    private javax.swing.JButton DrawPolygonButton;
    private javax.swing.JButton DrawPointButton;
    private DrawViewPortPaint drawViewPortPaint;


    private JDesktopPane desktop;
    // End of variables declaration//GEN-END:variables

    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;

    private JDialog aboutBox;

    public JLabel getStatusMessageLabel() {
        return statusMessageLabel;
    }


}
