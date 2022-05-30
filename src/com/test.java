package com;

import com.client.ClientGUI;
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


public class test extends JFrame {

    private JPanel chatPanel;

    private JTextArea jChatBox;
    private JScrollPane jChatScrollPane;
    private JPanel jSendText;
    private JTextField jTypeBox;
    private JButton jSend;


    public test(){
        init();
        addListener();
        this.setVisible(true);
    }

    public void init(){
        this.setTitle("Shared Whiteboard");
        this.setSize(1400,800);
        this.setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.setLayout(new BorderLayout());

        initChatPanel();

        this.add(chatPanel, BorderLayout.EAST);

    }


    private void initChatPanel() {
        chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());
        chatPanel.setSize(200,550);

        JLabel label = new JLabel("Chat", SwingConstants.CENTER);
        label.setFont(new Font("Verdana", Font.PLAIN, 15));
        jChatBox = new JTextArea();
        jChatBox.setSize(200,500);
        jChatBox.setEditable(false);
        jChatScrollPane = new JScrollPane(jChatBox);

        jSendText = new JPanel();
        jTypeBox = new JTextField(15);
        jSend = new JButton("Send");
        jSendText.add(jTypeBox);
        jSendText.add(jSend);

        chatPanel.add(label, BorderLayout.NORTH);
        chatPanel.add(jChatScrollPane, BorderLayout.CENTER);
        chatPanel.add(jSendText, BorderLayout.SOUTH);
    }


    private void addListener() {

        jSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = jTypeBox.getText();
                System.out.println("msg:" + msg);
            }
        });

    }

    public static void main(String[] args) {
        test t = new test();
    }

}
