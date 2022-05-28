package com.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Server extends Remote {
    void clientSayHi() throws RemoteException;
    void synchronize(byte[] image) throws RemoteException;
    void registerClient(String name, Client client) throws RemoteException;
    byte[] getImage() throws RemoteException;
}
