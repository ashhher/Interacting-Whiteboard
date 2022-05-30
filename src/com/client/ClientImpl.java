package com.client;

import com.remote.Client;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * @ClassName ClientImpl
 * @Description TODO
 * @Author XiaoHan
 **/
public class ClientImpl extends UnicastRemoteObject implements Client {
    ClientGUI clientGUI;
    String username;

    public ClientImpl(ClientGUI clientGUI, String username) throws RemoteException {
        this.clientGUI = clientGUI;
        this.username = username;
    }

    @Override
    public void updateImage(byte[] bImg) throws RemoteException {
        clientGUI.setByteImage(bImg);
    }

    @Override
    public void updateChat(String msgs) throws RemoteException {
        clientGUI.setChatBox(msgs);
    }

    @Override
    public void updateUserList(String userList) throws RemoteException {
        clientGUI.setUserList(userList);
    }

    @Override
    public String getUsername() throws RemoteException {
        return this.username;
    }

    @Override
    public void exit(String info) throws RemoteException {
        clientGUI.exit(info);
    }

    @Override
    public boolean permitUser(String username) throws RemoteException {
        return clientGUI.permitUser(username);
    }

}
