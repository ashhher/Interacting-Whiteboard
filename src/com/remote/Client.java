package com.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Client extends Remote {
    void sayHi(String name) throws RemoteException;
    void updateImage(byte[] img) throws RemoteException;
}
