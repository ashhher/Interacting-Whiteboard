package com;

import com.client.ClientGUI;
import com.client.ClientImpl;
import com.remote.Client;
import com.remote.Server;
import com.server.ServerImpl;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * @ClassName CreateWhiteBoard
 * @Description TODO
 * @Author XiaoHan
 **/
public class CreateWhiteBoard {

    public static void main(String[] args) throws RemoteException {
        // RMI
        Server server = new ServerImpl();
        Registry registry = LocateRegistry.createRegistry(1099);
        registry.rebind("WhiteBoard", server);

        // whiteboard
        ClientGUI gui = new ClientGUI(server, false);
        Client client = new ClientImpl(gui);
        server.registerClient("admin", client);

        gui.init();
    }
}
