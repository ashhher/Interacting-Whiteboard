package com.server;

import com.remote.Client;
import com.remote.Server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName WhiteBoard
 * @Description TODO
 * @Author XiaoHan
 **/
public class ServerImpl extends UnicastRemoteObject implements Server {
    byte[] globalImage;
    Map<String, Client> clientMap = new HashMap<>();

    public ServerImpl() throws RemoteException {
    }

    @Override
    public void clientSayHi() throws RemoteException {
        for (Map.Entry < String, Client > entry: clientMap.entrySet()) {
//            String name = entry.getKey();
            entry.getValue().sayHi(clientMap.toString());
        }
    }

    @Override
    public void synchronize(byte[] image) throws RemoteException {
        globalImage = image;
        for (Map.Entry < String, Client > entry: clientMap.entrySet()) {
            entry.getValue().updateImage(globalImage);
        }

    }

    @Override
    public void registerClient(String name, Client client) throws RemoteException {
        clientMap.put(name, client);
    }

    @Override
    public byte[] getImage() throws RemoteException {
        return globalImage;
    }

}
