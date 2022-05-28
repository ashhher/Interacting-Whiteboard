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

    public ClientImpl(ClientGUI clientGUI) throws RemoteException {
        this.clientGUI = clientGUI;
    }

    @Override
    public void sayHi(String name) throws RemoteException {
        System.out.println(name);
    }

    @Override
    public void updateImage(byte[] bImg) throws RemoteException {
        clientGUI.setByteImage(bImg);
    }
}
