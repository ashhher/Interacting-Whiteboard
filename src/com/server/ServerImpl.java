package com.server;

import com.remote.Client;
import com.remote.Server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * @ClassName WhiteBoard
 * @Description TODO
 * @Author XiaoHan
 **/
public class ServerImpl extends UnicastRemoteObject implements Server {
    Map<String, Client> userMap = new HashMap<>();
    byte[] globalImage;
    String globalMsgs = "";
    String manager;

    public ServerImpl() throws RemoteException {
    }

    @Override
    public boolean isUsernameExist(String username) throws RemoteException {
        return userMap.containsKey(username);
    }

    @Override
    public void synchronizeImage(byte[] image) throws RemoteException {
        globalImage = image;
        for (Map.Entry < String, Client > entry: userMap.entrySet()) {
            entry.getValue().updateImage(globalImage);
        }
    }

    @Override
    public byte[] getImage() throws RemoteException {
        return globalImage;
    }

    @Override
    public void synchronizeChat(String msg) throws RemoteException {
        globalMsgs += msg;
        for (Map.Entry < String, Client > entry: userMap.entrySet()) {
            entry.getValue().updateChat(globalMsgs);
        }
    }

    @Override
    public String getChat() throws RemoteException {
        return globalMsgs;
    }

    @Override
    public boolean registerUser(Client client, boolean isManager) throws RemoteException {
        String username = client.getUsername();
        boolean permit = false;
        if (isManager) {
            this.manager = username;
        } else {
            permit = userMap.get(manager).permitUser(username);
        }
        if (isManager || permit) {
            userMap.put(username, client);
            updateUserList();
        }
        return permit;
    }

    @Override
    public void deleteUser(String username, String type) throws RemoteException {
        if (username.equals(this.manager)) {
            for (Map.Entry < String, Client > entry: userMap.entrySet()) {
                if (!entry.getKey().equals(this.manager)) {
                    entry.getValue().exit("Sorry, the manager has leave, you have to exit.");
                }
            }
        }
        if (type.equals("kick")) {
            Client client = userMap.get(username);
            userMap.remove(username);
            updateUserList();
            client.exit("You are kicked out by the manager");
        } else if (type.equals("exit")) {
            userMap.remove(username);
            updateUserList();
        }
    }

    public void updateUserList() throws RemoteException {
        List<String> userList = new ArrayList<>(userMap.keySet());
        String users = String.join("\n", userList);
        for (Map.Entry < String, Client > entry: userMap.entrySet()) {
            entry.getValue().updateUserList(users);
        }
    }

}
