package com.client;

import com.remote.WhiteBoard;

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
    private static WhiteBoard whiteBoard;

    public static void main(String[] args) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        whiteBoard = (WhiteBoard) registry.lookup("WhiteBoard");
        String msg = whiteBoard.sayHi("client");
        System.out.println(msg);
    }
}
