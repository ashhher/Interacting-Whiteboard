package com;

import com.client.ClientGUI;
import com.client.ClientImpl;
import com.remote.Client;
import com.remote.Server;
import com.server.ServerImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * @ClassName CreateWhiteBoard
 * @Description TODO
 * @Author XiaoHan
 **/
public class CreateWhiteBoard {
    public static JFrame loginFrame;
    public static JPanel loginPanel;
    public static JLabel loginLabel;
    public static JTextField loginTextField;
    public static JButton loginButton;


    public static void main(String[] args) throws RemoteException {
        // RMI
        Server server = new ServerImpl();
        Registry registry = LocateRegistry.createRegistry(1099);
        registry.rebind("WhiteBoard", server);

        // Input username
        loginFrame = new JFrame("Shared Whiteboard - Manager");
        loginFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        loginFrame.setSize(400,200);
        loginFrame.setLocationRelativeTo(null);
        loginPanel = new JPanel();
        loginPanel.setLayout(new FlowLayout());
        loginLabel = new JLabel("Please input the manager username: ");
        loginTextField = new JTextField(16);
        loginButton = new JButton("Enter");
        loginLabel.setFont(new Font("Verdana", Font.PLAIN, 18));
        loginTextField.setFont(new Font("Verdana", Font.PLAIN, 18));
        loginButton.setFont(new Font("Verdana", Font.PLAIN, 18));

        loginPanel.add(loginLabel);
        loginPanel.add(loginTextField);
        loginPanel.add(loginButton);

        loginFrame.add(loginPanel);
        loginFrame.setVisible(true);

        loginButton.addActionListener(e -> {
            String username = loginTextField.getText();

            // whiteboard
            try {
                loginFrame.setVisible(false);
                ClientGUI gui = new ClientGUI(server, username,false);
                Client client = new ClientImpl(gui, username);
                server.registerUser(client,true);
                gui.getClientFrame().setVisible(true);
            } catch (RemoteException remoteException) {
                remoteException.printStackTrace();
            }
        });
    }
}
