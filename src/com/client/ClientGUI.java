package com.client;

import com.remote.Server;
import com.server.MyCanvas;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;

/**
 * @ClassName ServerGUI
 * @Description TODO
 * @Author XiaoHan
 **/
public class ClientGUI{
    JFrame clientFrame;
    BufferedImage image;
    Graphics gr;
    Graphics2D g;
    MyCanvas canvas = new MyCanvas();

    Boolean isClient;
    String username;

    Server server;

    File file = null;

    private int x1 = 0,
                y1 = 0,
                x2 = 0,
                y2 = 0;
    private Color color = Color.BLACK;
    private String tool = "pencil";


    private JToolBar toolsPanel;
    private JPanel usersPanel;
    private JPanel chatPanel;

    private JTextArea jUserList;
    private JTextField jUser;
    private JButton jKick;

    private JTextArea jChatBox;
    private JTextField jTypeBox;
    private JButton jSend;

    private JMenuBar menuBar;
    private JMenu menu;
    private JMenuItem newMenuItem;
    private JMenuItem openMenuItem;
    private JMenuItem saveMenuItem;
    private JMenuItem saveAsMenuItem;


    public ClientGUI(Server server, String username, Boolean isClient) throws RemoteException {
        clientFrame = new JFrame();
        this.server = server;
        this.username = username;
        this.isClient = isClient;

        init();
        addListener();
    }

    public JFrame getClientFrame() {
        return clientFrame;
    }

    public void init() throws RemoteException {
        clientFrame.setTitle("Shared Whiteboard");
        clientFrame.setSize(1400,800);
//        setBounds(0, 0, 1024, 768);
        clientFrame.setLocationRelativeTo(null);
        clientFrame.setLayout(new BorderLayout());
        clientFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        initCanvas();
        initToolsPanel();
        initUsersPanel();
        initChatPanel();
        initMenu();

        clientFrame.add(canvas, BorderLayout.CENTER);
        clientFrame.add(toolsPanel, BorderLayout.NORTH);
        clientFrame.add(usersPanel, BorderLayout.WEST);
        clientFrame.add(chatPanel, BorderLayout.EAST);
        clientFrame.setJMenuBar(menuBar);

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
                "icon/word.png",
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
                    Color selectColor = JColorChooser.showDialog(clientFrame, "color", color);
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

    private void initUsersPanel() {
        usersPanel = new JPanel();
        usersPanel.setLayout(new BorderLayout());
        usersPanel.setSize(200,550);

        JLabel label = new JLabel("User List", SwingConstants.CENTER);
        label.setFont(new Font("Verdana", Font.PLAIN, 15));
        jUserList = new JTextArea(20,20);
//        jUserList.setSize(200,500);
        jUserList.setEditable(false);
        JScrollPane jUserScrollPane = new JScrollPane(jUserList);

        JPanel jKickUser = new JPanel();
        jUser = new JTextField(15);
        jKick = new JButton("Kick");
        jKickUser.add(jUser);
        jKickUser.add(jKick);

        usersPanel.add(label, BorderLayout.NORTH);
        usersPanel.add(jUserScrollPane, BorderLayout.CENTER);
        usersPanel.add(jKickUser, BorderLayout.SOUTH);

        // only for manager
        if (isClient) {
            jKickUser.setVisible(false);
        }
    }

    private void initChatPanel() throws RemoteException {
        chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());
        chatPanel.setSize(200,550);

        JLabel label = new JLabel("Chat", SwingConstants.CENTER);
        label.setFont(new Font("Verdana", Font.PLAIN, 15));
        jChatBox = new JTextArea(20,20);
//        jChatBox.setSize(200,500);
        jChatBox.setEditable(false);
        JScrollPane jChatScrollPane = new JScrollPane(jChatBox);

        JPanel jSendText = new JPanel();
        jTypeBox = new JTextField(15);
        jSend = new JButton("Send");
        jSendText.add(jTypeBox);
        jSendText.add(jSend);

        chatPanel.add(label, BorderLayout.NORTH);
        chatPanel.add(jChatScrollPane, BorderLayout.CENTER);
        chatPanel.add(jSendText, BorderLayout.SOUTH);

        if (isClient) {
            String msgs = server.getChat();
            setChatBox(msgs);
        }
    }

    private void initMenu() {
        menuBar = new JMenuBar();
        menu = new JMenu("file");
        menu.setFont(new Font("Verdana", Font.PLAIN, 18));
        newMenuItem = new JMenuItem("creat whiteboard");
        openMenuItem = new JMenuItem("open a whiteboard");
        saveMenuItem = new JMenuItem("save current whiteboard");
        saveAsMenuItem = new JMenuItem("save current whiteboard to a file");

        menu.add(newMenuItem);
        menu.add(openMenuItem);
        menu.add(saveMenuItem);
        menu.add(saveAsMenuItem);
        menuBar.add(menu);
    }

    private void addListener() {
        clientFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    server.deleteUser(username,"exit");
                    exit("Log out successfully");
                } catch (RemoteException remoteException) {
                    remoteException.printStackTrace();
                }
            }

            @Override
            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }
        });

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
                    case "word":
                        String input = JOptionPane.showInputDialog("Please input your text: ");
                        g.drawString(input, x1, y1);

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

        jSend.addActionListener(e -> {
            String msg = jTypeBox.getText();
            if (msg.length() > 0) {
                try {
                    msg = username + ": \n" + msg + "\n\n";
                    server.synchronizeChat(msg);
                    jTypeBox.setText("");
                } catch (RemoteException remoteException) {
                    remoteException.printStackTrace();
                }
            }
        });

        jKick.addActionListener(e -> {
            String username = jUser.getText();
            try {
                server.deleteUser(username,"kick");
            } catch (RemoteException remoteException) {
                remoteException.printStackTrace();
            }
        });

        newMenuItem.addActionListener(e -> {
            image = new BufferedImage(1024, 768, BufferedImage.TYPE_INT_BGR);
            setImage(image);
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, 1024, 768);
            synchronizeToServer(image);
        });

        openMenuItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser("Open a image");
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Image Files", "png"));
            int option = fileChooser.showOpenDialog(clientFrame);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    setImage(ImageIO.read(file));
                    synchronizeToServer(this.image);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(clientFrame,
                        "This is a invalid image",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        saveMenuItem.addActionListener(e -> {
            if(this.file == null) {
                JOptionPane.showMessageDialog(clientFrame,
                        "Please save the image as a file first.",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                try {
                    ImageIO.write(image, "png", this.file);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    JOptionPane.showMessageDialog(clientFrame,
                            "Fail to save the image.",
                            "Warning",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        saveAsMenuItem.addActionListener(e -> {
            BufferedImage image = new BufferedImage(canvas.getWidth(), canvas.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics saveG = image.getGraphics();
            canvas.printAll(saveG);

            JFileChooser fileChooser = new JFileChooser("Save a image");
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Image Files", "png"));

            int option = fileChooser.showSaveDialog(clientFrame);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".png")) {
                    file = new File(file.getParentFile(), file.getName() + ".png");
                }
                try {
                    ImageIO.write(image, "png", file);
                    this.file = file;
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    JOptionPane.showMessageDialog(clientFrame,
                            "Fail to save the image.",
                            "Warning",
                            JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(clientFrame,
                        "This is a invalid path / file name.",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    public void synchronizeToServer(BufferedImage image) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image,"png", out);
            byte[] bImg = out.toByteArray();
            server.synchronizeImage(bImg);
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

    public void setChatBox(String msgs) {
        jChatBox.setText(msgs);
    }

    public void setUserList(String userList) {
        jUserList.setText(userList);
    }

    boolean permitUser(String username) {
        boolean permit = false;
        String info = "User \""+ username + "\" want to join the whiteboard, do you want to permit?";
        int option = JOptionPane.showConfirmDialog(clientFrame, info,"Permission", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            permit = true;
        }
        return permit;
    }

    public void exit(String info) {
        JOptionPane.showMessageDialog(clientFrame, info,"EXIT", JOptionPane.WARNING_MESSAGE);
        clientFrame.dispose();
        server = null;
    }

}
