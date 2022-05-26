package com.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface WhiteBoard extends Remote {
    String sayHi(String name) throws RemoteException;
}
