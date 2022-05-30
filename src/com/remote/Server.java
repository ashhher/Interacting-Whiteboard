package com.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Server extends Remote {
    boolean isUsernameExist(String username) throws RemoteException;
    void synchronizeImage(byte[] image) throws RemoteException;
    byte[] getImage() throws RemoteException;

    void synchronizeChat(String msg) throws RemoteException;
    String getChat() throws RemoteException;

    boolean registerUser(Client client, boolean isManager) throws RemoteException;
    void deleteUser(String username, String type) throws RemoteException;
}
