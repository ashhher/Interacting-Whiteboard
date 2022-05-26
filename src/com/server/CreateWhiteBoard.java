package com.server;

import com.remote.WhiteBoard;

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
    private static WhiteBoard whiteBoard;

    public static void main(String[] args) throws RemoteException {
        whiteBoard = new WhiteBoardService();
        WhiteBoard skeleton = (WhiteBoard) UnicastRemoteObject.exportObject(whiteBoard, 0);
        Registry registry = LocateRegistry.createRegistry(1099);
        registry.rebind("WhiteBoard", skeleton);
    }
}
