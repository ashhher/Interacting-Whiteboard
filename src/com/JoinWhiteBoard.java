package com;

import com.client.ClientGUI;
import com.client.ClientImpl;
import com.remote.Client;
import com.remote.Server;

import javax.swing.*;
import java.awt.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * @ClassName JoinWhiteBoard
 * @Description TODO
 * @Author XiaoHan
 **/
public class JoinWhiteBoard {
    public static JFrame loginFrame;
    public static JPanel loginPanel;
    public static JLabel loginLabel;
    public static JTextField loginTextField;
    public static JButton loginButton;

    public static void main(String[] args) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        Server server = (Server) registry.lookup("WhiteBoard");

        // Input username
        loginFrame = new JFrame("Shared Whiteboard - User");
        loginFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        loginFrame.setSize(400,200);
        loginFrame.setLocationRelativeTo(null);
        loginPanel = new JPanel();
        loginPanel.setLayout(new FlowLayout());
        loginLabel = new JLabel("Please input your username: ");
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

            try {
                // check if the username exist
                boolean isExist = server.isUsernameExist(username);
                if (isExist) {
                    JOptionPane.showMessageDialog(loginPanel,
                            "This username already exist, please change another one",
                            "Warning",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // whiteboard
                ClientGUI gui = new ClientGUI(server, username,true);
                Client client = new ClientImpl(gui, username);
                boolean permit = server.registerUser(client, false);
                if (!permit) {
                    gui.getClientFrame().setVisible(false);
                    JOptionPane.showMessageDialog(loginFrame,
                            "The manager has reject you, pleas try again.",
                            "Warning",
                            JOptionPane.WARNING_MESSAGE);
                } else {
                    gui.getClientFrame().setVisible(true);
                    loginFrame.setVisible(false);
                }
            } catch (RemoteException remoteException) {
                remoteException.printStackTrace();
            }
        });
    }
}
