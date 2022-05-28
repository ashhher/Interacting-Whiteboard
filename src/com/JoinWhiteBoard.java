package com;

import com.client.ClientGUI;
import com.client.ClientImpl;
import com.remote.Client;
import com.remote.Server;

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

    public static void main(String[] args) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        Server server = (Server) registry.lookup("WhiteBoard");

        ClientGUI gui = new ClientGUI(server, true);
        Client client = new ClientImpl(gui);
        server.registerClient("user1", client);

        gui.init();
    }
}
