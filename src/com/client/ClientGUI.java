package com.client;

import com.remote.Server;
import com.server.MyCanvas;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;

/**
 * @ClassName ServerGUI
 * @Description TODO
 * @Author XiaoHan
 **/
public class ClientGUI extends JFrame {
    BufferedImage image;
    Graphics gr;
    Graphics2D g;
    MyCanvas canvas = new MyCanvas();

    Boolean isClient;

    Server server;

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


    public ClientGUI(Server server, Boolean isClient) throws RemoteException {
        this.server = server;
        this.isClient = isClient;

        init();
        addListener();
        this.setVisible(true);
    }

    public void init() throws RemoteException {
        this.setTitle("Shared Whiteboard");
        this.setSize(1200,1000);
//        setBounds(0, 0, 1024, 768);
        this.setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

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

    private void initCanvas() throws RemoteException {
        if (isClient) {
            byte[] bImg = server.getImage();
            setByteImage(bImg);
        } else {
            image = new BufferedImage(1024, 768, BufferedImage.TYPE_INT_BGR);
            setImage(image);
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, 1024, 768);
            synchronizeToServer(image);
        }

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
            button.addActionListener(e -> {
                tool = e.getActionCommand();

                // choose a color
                if (tool.equals("color")) {
                    Color selectColor = JColorChooser.showDialog(ClientGUI.this, "color", color);
                    if (!(selectColor == null)) {
                        color = selectColor;
                    }
                    g.setColor(color);
                    tool = "pencil";
                    canvas.repaint();
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
            public void mouseReleased(MouseEvent event) {
                x2 = event.getX();
                y2 = event.getY();

                switch (tool) {
                    case "line":
                        g.drawLine(x1, y1, x2, y2);
                        break;
                    case "rect":
                        g.drawRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1),
                                Math.abs(y2 - y1));
                        break;
                    case "oval":
                        g.drawOval(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1),
                                Math.abs(y2 - y1));
                        break;
                    case "polygon":
                        int[] x = {(x1 + x2) / 2, x2, x1};
                        int[] y = {(y1 + y2) / 2 - Math.abs(x1 - x2) / 2, y2, y1};
                        g.drawPolygon(x, y, 3);
                        break;
                }

                canvas.repaint();
                synchronizeToServer(image);
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

    public void synchronizeToServer(BufferedImage image) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image,"png", out);
            byte[] bImg = out.toByteArray();
            server.synchronize(bImg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setByteImage(byte[] bImg) {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(bImg);
            BufferedImage image = ImageIO.read(in);
            setImage(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        this.gr = image.getGraphics();
        this.g = (Graphics2D) gr;
        canvas.setImage(image);
    }
}
