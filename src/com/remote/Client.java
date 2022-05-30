package com.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Client extends Remote {
    void updateImage(byte[] img) throws RemoteException;
    void updateChat(String msgs) throws RemoteException;
    void updateUserList(String userList) throws RemoteException;
    String getUsername() throws RemoteException;
    void exit(String info) throws RemoteException;
    boolean permitUser(String username) throws RemoteException;
}
