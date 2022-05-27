package com.server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

/**
 * @ClassName ServerGUI
 * @Description TODO
 * @Author XiaoHan
 **/
public class ServerGUI extends JFrame {
    BufferedImage image = new BufferedImage(1024, 768, BufferedImage.TYPE_INT_BGR);
    Graphics gr = image.getGraphics();
    Graphics2D g = (Graphics2D) gr;
    MyCanvas canvas = new MyCanvas();

    private int x1 = 0,
                y1 = 0,
                x2 = 0,
                y2 = 0;
    private Color color = Color.BLACK;
    private String tool = "pencil";


    private JToolBar toolsPanel;
    private JPanel usersPanel;
    private JPanel chatPanel;
    private JPanel infoPanel;


    public ServerGUI() {
        init();
        addListener();
        this.setVisible(true);
    }

    public void init() {
        this.setTitle("Shared Whiteboard");
        this.setSize(1200,1000);
//        setBounds(0, 0, 1024, 768);
        this.setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setLayout(new BorderLayout());

        initCanvas();
        initToolsPanel();

        usersPanel = new JPanel();
        chatPanel = new JPanel();
        infoPanel = new JPanel();

        this.add(canvas, BorderLayout.CENTER);
        this.add(toolsPanel, BorderLayout.NORTH);
        this.add(usersPanel, BorderLayout.WEST);
        this.add(chatPanel, BorderLayout.EAST);
        this.add(infoPanel, BorderLayout.SOUTH);

        canvas.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

    }

    private void initCanvas() {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 1024, 768);
        g.setColor(color);
        canvas.setImage(image);
    }

    private void initToolsPanel() {
        toolsPanel = new JToolBar();
        String[] toolsArray = {
                "icon/eraser.png",
                "icon/pencil.png",
                "icon/line.png",
                "icon/rect.png",
                "icon/oval.png",
                "icon/polygon.png",
                "icon/color.png"
        };

        for (String toolIcon : toolsArray) {
            ImageIcon icon = new ImageIcon(toolIcon);
            JButton button = new JButton(icon);
            button.setPreferredSize(new Dimension(30,30));
            button.setActionCommand(toolIcon.substring(5,toolIcon.lastIndexOf(".")));
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    tool = e.getActionCommand();

                    // choose a color
                    if (tool.equals("color")) {
                        Color selectColor = JColorChooser.showDialog(ServerGUI.this, "color", color);
                        if (!(selectColor == null)) {
                            color = selectColor;
                        }
                        g.setColor(color);
                        tool = "pencil";
                        canvas.repaint();
                    }
                }
            });
            toolsPanel.add(button);
        }
    }

    private void addListener() {
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // begin to draw

                if (tool.equals("eraser")) {
                    g.setStroke(new BasicStroke(50));
                    g.setColor(Color.WHITE);
                }
                else {
                    g.setStroke(new BasicStroke(3));
                    g.setColor(color);
                }

                x1 = e.getX();
                y1 = e.getY();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                x2 = e.getX();
                y2 = e.getY();

                if (tool.equals("line")) {
                    g.drawLine(x1, y1, x2, y2);
                } else if (tool.equals("rect")) {
                    g.drawRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1));
                } else if (tool.equals("oval")) {
                    g.drawOval(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1));
                } else if (tool.equals("polygon")) {
                    int [] x = {(x1+x2)/2, x2, x1};
                    int [] y = {(y1+y2)/2 - Math.abs(x1-x2)/2, y2, y1};
                    g.drawPolygon(x, y, 3);
                }
                canvas.repaint();
            }
        });

        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (tool.equals("pencil") || tool.equals("eraser")) {
                    x2 = e.getX();
                    y2 = e.getY();

                    g.drawLine(x1, y1, x2, y2);
                    x1 = x2;
                    y1 = y2;
                    canvas.repaint();
                }
            }
        });
    }
}
